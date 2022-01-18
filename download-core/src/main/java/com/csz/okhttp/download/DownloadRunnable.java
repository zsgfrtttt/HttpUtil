package com.csz.okhttp.download;

import android.annotation.SuppressLint;
import android.os.Process;
import android.util.Log;

import androidx.arch.core.executor.ArchTaskExecutor;

import com.csz.okhttp.download.db.DownloadDBHepler;
import com.csz.okhttp.download.db.DownloadEntity;
import com.csz.okhttp.http.DownloadCallback;
import com.csz.okhttp.http.DownloadManager;
import com.csz.okhttp.http.HttpError;
import com.csz.okhttp.http.HttpManager;
import com.csz.okhttp.util.CloseUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Response;

/**
 * @author caishuzhan
 */
public class DownloadRunnable implements Runnable {

    private static final int MAX_RETRY = 3;

    /**
     * 记录当前的下载文件需要多少个线程
     */
    private final AtomicLong totalProgress;
    private final String url;
    private final long start;
    private final long end;
    private final long contentLength;
    private final DownloadCallback mDownloadCallback;
    private final DownloadEntity mEntity;
    private boolean mDone = false;
    private CountDownLatch mPauseLatch;
    private int mRetryCount;
    private int progress;
    private boolean pending;
    private long incrementStart;
    private RandomAccessFile randomAccessFile = null;
    private FileOutputStream fileOutputStream = null;

    public DownloadRunnable(String url, long start, long end, long contentLength, AtomicLong totalProgress, DownloadCallback downloadCallback, DownloadEntity entity) {
        this.url = url;
        this.start = start;
        this.end = end;
        this.contentLength = contentLength;
        this.totalProgress = totalProgress;
        this.mDownloadCallback = downloadCallback;
        this.mEntity = entity;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void run() {
        //设置线程优先级，越小优先级越高
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        File file = FileStorageManager.getInstance().getFileByName(url);
        incrementStart = start + mEntity.getProgress_position();

        while (!mDone && mRetryCount < MAX_RETRY) {
            try {
                if (checkDownloadCompleted(file, incrementStart)) return;
                Response response = HttpManager.getInstance().syncRequest(url, incrementStart, end);
                if (response == null) {
                    invokeCallback(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCallback.onFailure(HttpError.NETWORK_ERROR.getCode(), HttpError.NETWORK_ERROR.getMsg());
                        }
                    });
                    return;
                }
                initlizeOutputFile(file);
                byte[] bytes = new byte[getAvailByteSize()];
                InputStream inputStream = response.body().byteStream();
                int len = 0;
                long progress = mEntity.getProgress_position();
                while ((len = inputStream.read(bytes)) != -1) {
                    pendingCurrentThread();
                    initlizeOutputFile(file); // pending == true
                    fileOutputStream.write(bytes, 0, len);
                    fileOutputStream.flush();
                    progress += len;
                    incrementStart += len;
                    mEntity.setProgress_position(progress);
                  //  DownloadDBHepler.getInstance().insertOrReplace(mEntity);
                    long l = totalProgress.addAndGet(len);
                    invokeCallback(new Runnable() {
                        @Override
                        public void run() {
                            int off = (int) (l * 100 / contentLength);
                            if (DownloadRunnable.this.progress != off) {
                                DownloadRunnable.this.progress = off;
                                mDownloadCallback.onProgress(DownloadRunnable.this.progress);
                            }
                        }
                    });
                }
                //inputStream必须关闭,否则文件可能不是最终写入文件
                CloseUtil.close(inputStream, randomAccessFile, fileOutputStream);
                mDone = true;
            } catch (IOException e) {
                Log.i("csz", "DownloadRunnable   " + e.getMessage());
                e.printStackTrace();
                invokeCallback(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadCallback.onFailure(HttpError.NETWORK_ERROR.getCode(), HttpError.NETWORK_ERROR.getMsg());
                    }
                });
            } finally {
                CloseUtil.close(randomAccessFile, fileOutputStream);
                if (mDone) {
                    DownloadDBHepler.getInstance().insertOrReplace(mEntity);
                }
                incrementStart = mEntity.getProgress_position() + start;
                if (checkDownloadCompleted(file, incrementStart)) return;
                mRetryCount++;
                if (mRetryCount == MAX_RETRY){
                    DownloadManager.getInstance().finish(url);
                    invokeCallback(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCallback.onFailure(HttpError.RETRY_ERROR.getCode(), HttpError.RETRY_ERROR.getMsg());
                        }
                    });
                }
            }
        }

    }

    private void initlizeOutputFile(File file) throws IOException {
        if (pending || fileOutputStream == null || randomAccessFile == null) {
            pending = false;
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(incrementStart);
            fileOutputStream = new FileOutputStream(randomAccessFile.getFD());
        }
    }

    @SuppressLint("RestrictedApi")
    private void invokeCallback(Runnable runnable) {
        if (mDownloadCallback != null) {
            ArchTaskExecutor.getMainThreadExecutor().execute(runnable);
        }
    }

    /**
     * 只捕获InterruptedException，不捕获IOException
     * 挂起当前线程
     */
    private void pendingCurrentThread() throws IOException {
        if (mPauseLatch != null && mPauseLatch.getCount() == 1) {
            try {
                fileOutputStream.flush();
                pending = true;
                CloseUtil.close(randomAccessFile, fileOutputStream);
           //     DownloadDBHepler.getInstance().insertOrReplace(mEntity);
                mPauseLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查当前任务是否已经下载完成
     *
     * @param file
     * @param incrementStart
     * @return
     */
    @SuppressLint("RestrictedApi")
    private boolean checkDownloadCompleted(File file, long incrementStart) {
        //不能incrementStart >= end , 例如 start:0  end:9  长度是10 ，+9 == 9
        if (incrementStart > end) {
            //等于0证明多线程的其他任务也下载完成
            if (totalProgress.get() == contentLength) {
                DownloadManager.getInstance().finish(url);
                invokeCallback(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadCallback.onSuccess(file);
                    }
                });
            }
            return true;
        }
        return false;
    }

    public void pause() {
        if (mPauseLatch == null) {
            mPauseLatch = new CountDownLatch(1);
          //  DownloadDBHepler.getInstance().insertOrReplace(mEntity);
        } else {
            if (mPauseLatch.getCount() != 1) {
                mPauseLatch = new CountDownLatch(1);
         //       DownloadDBHepler.getInstance().insertOrReplace(mEntity);
            }
        }
    }

    public void resume() {
        if (mPauseLatch != null && mPauseLatch.getCount() == 1) {
            mPauseLatch.countDown();
        }
    }

    /**
     * 获取适合的字节缓存区大小
     *
     * @return
     */
    private int getAvailByteSize() {
        long remain = end - start - mEntity.getProgress_position();
        if (remain >= 100 * 1024 * 1024) {
            return 1024 * 100;
        } else if (remain >= 20 * 1024 * 1024) {
            return 1024 * 40;
        } else if (remain >= 1024 * 24) {
            return 1024 * 24;
        } else {
            return 1024 * 8;
        }
    }

    public static class Request {
        private String url;
        private long start;
        private long end;
        private long contentLength;
        private DownloadCallback mDownloadCallback;
        private DownloadEntity mEntity;
        private AtomicLong mTotalProgress;

        public Request url(String url) {
            this.url = url;
            return this;
        }

        public Request start(long start) {
            this.start = start;
            return this;
        }

        public Request end(long end) {
            this.end = end;
            return this;
        }

        public Request contentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Request downloadCallback(DownloadCallback mDownloadCallback) {
            this.mDownloadCallback = mDownloadCallback;
            return this;
        }

        public Request entity(DownloadEntity mEntity) {
            this.mEntity = mEntity;
            return this;
        }

        public Request totalProgress(AtomicLong mTotalProgress) {
            this.mTotalProgress = mTotalProgress;
            return this;
        }

        public DownloadRunnable build() {
            return new DownloadRunnable(url, start, end, contentLength, mTotalProgress, mDownloadCallback, mEntity);
        }
    }
}

