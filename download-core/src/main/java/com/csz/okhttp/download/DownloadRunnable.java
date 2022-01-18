package com.csz.okhttp.download;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Process;
import android.util.Log;

import androidx.arch.core.executor.ArchTaskExecutor;

import com.csz.okhttp.download.db.DownloadEntity;
import com.csz.okhttp.http.DownloadCallback;
import com.csz.okhttp.http.DownloadManager;
import com.csz.okhttp.http.HttpError;
import com.csz.okhttp.http.HttpManager;
import com.csz.okhttp.util.CloseUtil;

import java.io.File;
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

    /**
     * 记录当前的下载文件需要多少个线程
     */
    private final AtomicInteger threadCount;
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

    public DownloadRunnable(String url, long start, long end, long contentLength, AtomicInteger threadCount, AtomicLong totalProgress, DownloadCallback downloadCallback, DownloadEntity entity) {
        this.url = url;
        this.start = start;
        this.end = end;
        this.contentLength = contentLength;
        this.threadCount = threadCount;
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
        long incrementStart = start + mEntity.getProgress_position();
        if (checkDownloadCompleted(file, incrementStart)) return;

        RandomAccessFile randomAccessFile = null;
        FileOutputStream fileOutputStream = null;

        while (!mDone && mRetryCount < 3) {
            try {
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
                randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(incrementStart);
                fileOutputStream = new FileOutputStream(randomAccessFile.getFD());
                byte[] bytes = new byte[getAvailByteSize()];
                InputStream inputStream = response.body().byteStream();
                int len = 0;
                long progress = mEntity.getProgress_position();
                //增加已下载的字节
                totalProgress.addAndGet(progress);
                while ((len = inputStream.read(bytes)) != -1) {
                    pendingCurrentThread();
                    fileOutputStream.write(bytes, 0, len);
                    progress += len;
                    mEntity.setProgress_position(progress);
                    int finalLen = len;
                    invokeCallback(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCallback.onProgress((int) (totalProgress.addAndGet(finalLen) * 100 / contentLength));
                        }
                    });
                    fileOutputStream.flush();
                }
                //inputStream必须关闭,否则文件可能不是最终写入文件
                CloseUtil.close(inputStream, randomAccessFile, fileOutputStream);
                mDone = true;
                //等于0证明多线程的其他任务也下载完成
                if (threadCount.decrementAndGet() == 0) {
                    DownloadManager.getInstance().finish(url);
                    invokeCallback(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCallback.onSuccess(file);
                        }
                    });
                }
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
                DownloadDBHepler.getInstance().insertOrReplace(mEntity);

                incrementStart = mEntity.getProgress_position() + start;
                if (checkDownloadCompleted(file, incrementStart)) return;
                mRetryCount++;
            }
        }

    }

    @SuppressLint("RestrictedApi")
    private void invokeCallback(Runnable runnable) {
        if (mDownloadCallback != null) {
            ArchTaskExecutor.getMainThreadExecutor().execute(runnable);
        }
    }

    /**
     * 挂起当前线程
     */
    private void pendingCurrentThread() {
        if (mPauseLatch != null && mPauseLatch.getCount() == 1) {
            try {
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
        if (incrementStart >= end) {
            //增加已下载的字节
            totalProgress.addAndGet(end - start);
            invokeCallback(new Runnable() {
                @Override
                public void run() {
                    mDownloadCallback.onProgress((int) (totalProgress.get() * 100 / contentLength));
                }
            });
            //等于0证明多线程的其他任务也下载完成
            if (threadCount.decrementAndGet() == 0) {
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
            DownloadDBHepler.getInstance().insertOrReplace(mEntity);
        } else {
            if (mPauseLatch.getCount() != 1) {
                mPauseLatch = new CountDownLatch(1);
                DownloadDBHepler.getInstance().insertOrReplace(mEntity);
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
            return (int) remain;
        }
    }

    public static class Request {
        private AtomicInteger threadCount;
        private String url;
        private long start;
        private long end;
        private long contentLength;
        private DownloadCallback mDownloadCallback;
        private DownloadEntity mEntity;
        private AtomicLong mTotalProgress;

        public Request threadCount(AtomicInteger threadCount) {
            this.threadCount = threadCount;
            return this;
        }

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
            return new DownloadRunnable(url, start, end, contentLength, threadCount, mTotalProgress, mDownloadCallback, mEntity);
        }
    }
}

