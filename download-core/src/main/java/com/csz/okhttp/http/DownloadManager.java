package com.csz.okhttp.http;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.arch.core.executor.ArchTaskExecutor;

import com.csz.okhttp.download.db.DownloadDBHepler;
import com.csz.okhttp.download.DownloadRunnable;
import com.csz.okhttp.download.db.DownloadEntity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author caishuzhan
 */
public class DownloadManager {

    private static final int MAX_COUNT = 3;

    private HashSet<String> mDownloadTask;
    private Map<String, List<DownloadRunnable>> mDownloadRunnables;

    private ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(MAX_COUNT, MAX_COUNT, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
        private AtomicInteger atomicInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "downloadThread:" + atomicInteger.getAndDecrement());
            return thread;
        }
    });

    private static final class Holder {
        private static final DownloadManager INSTANCE = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return DownloadManager.Holder.INSTANCE;
    }

    private DownloadManager() {
        mDownloadTask = new HashSet<>();
        mDownloadRunnables = Collections.synchronizedMap(new HashMap<String, List<DownloadRunnable>>());
    }

    @SuppressLint("RestrictedApi")
    public void download(final String url, final DownloadCallback callback) {
        if (mDownloadTask.contains(url)) {
            if (callback != null) {
                invokeCallback(callback, new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(HttpError.TASK_RUNNING_ERROR.getCode(), HttpError.TASK_RUNNING_ERROR.getMsg());
                    }
                });
            }
            return;
        }
        mDownloadTask.add(url);

        final List<DownloadEntity> list = DownloadDBHepler.getInstance().getAll(url);
        int progress = calulateProgress(list);
        if (callback != null) {
            callback.onProgress(progress);
        }
        HttpManager.getInstance().asyncRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                finish(url);
                invokeCallback(callback, new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(HttpError.NETWORK_ERROR.getCode(), HttpError.NETWORK_ERROR.getMsg());
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    finish(url);
                    invokeCallback(callback, new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(response.code(), response.message());
                        }
                    });
                    return;
                }
                long length = response.body().contentLength();
                Log.i("csz", "contentLEngth   " + length);
                if (length == -1) {
                    finish(url);
                    invokeCallback(callback, new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(HttpError.CONTENT_LENGTH_ERROR.getCode(), HttpError.CONTENT_LENGTH_ERROR.getMsg());
                        }
                    });
                    return;
                }
                handleDownload(url, length, callback, list);
            }
        });
    }

    public void pause(final String url) {
        boolean contains = mDownloadTask.contains(url);
        if (contains) {
            synchronized (DownloadManager.class) {
                List<DownloadRunnable> runnables = mDownloadRunnables.get(url);
                if (runnables != null) {
                    for (DownloadRunnable runnable : runnables) {
                        runnable.pause();
                    }
                }
            }
        }
    }

    public void resume(final String url) {
        boolean contains = mDownloadTask.contains(url);
        if (contains) {
            synchronized (DownloadManager.class) {
                List<DownloadRunnable> runnables = mDownloadRunnables.get(url);
                if (runnables != null) {
                    for (DownloadRunnable runnable : runnables) {
                        runnable.resume();
                    }
                }
            }
        }
    }

    public void finish(String url) {
        mDownloadTask.remove(url);
        mDownloadRunnables.remove(url);
    }

    /**
     * 开启下载任务,例如长度25 分段 0-9 | 10 - 19 | 20 -24
     *
     * @param url
     * @param length
     * @param callback
     */
    private void handleDownload(String url, long length, DownloadCallback callback, List<DownloadEntity> cache) {
        boolean newDownloadTask = false;
        if (cache == null || cache.isEmpty()) {
            newDownloadTask = true;
        }
        DownloadEntity entity;
        int threadCount = getAvailableThread(length);
        long segment = length / threadCount;
        AtomicLong totalProgress = new AtomicLong();
        long start, end;
        List<DownloadRunnable> allDownloadList = new ArrayList<>();
        synchronized (DownloadManager.class) {
            for (int i = 0; i < threadCount; i++) {
                start = i * segment;
                if (i == threadCount - 1) {
                    end = length - 1;
                } else {
                    end = (i + 1) * segment - 1;
                }
                if (newDownloadTask || i >= cache.size()) {
                    entity = new DownloadEntity();
                    entity.setDownload_url(url);
                    entity.setStart_position(start);
                    entity.setEnd_position(end);
                    entity.setProgress_position(0L);
                    entity.setThread_id(i);
                    DownloadDBHepler.getInstance().insertOrReplace(entity);
                } else {
                    entity = cache.get(i);
                }
                //增加已下载的字节
                totalProgress.addAndGet(entity.getProgress_position());
                DownloadRunnable downloadRunnable = new DownloadRunnable.Request().url(url).start(start).end(end)
                        .contentLength(length).downloadCallback(callback)
                        .totalProgress(totalProgress).entity(entity).build();
                allDownloadList.add(downloadRunnable);
                mThreadPool.execute(downloadRunnable);
            }
            mDownloadRunnables.put(url, allDownloadList);
        }
    }


    /**
     * 根据文件大小返回合理的下载线程数
     *
     * @param length
     * @return
     */
    private int getAvailableThread(long length) {
        long minSize = 1024 * 1024 * 2;
        if (length <= minSize) {
            return 1;
        } else {
            int count = (int) (length / minSize);
            return count > MAX_COUNT ? MAX_COUNT : count;
        }
    }

    @SuppressLint("RestrictedApi")
    private void invokeCallback(DownloadCallback callback, Runnable runnable) {
        if (callback != null) {
            ArchTaskExecutor.getMainThreadExecutor().execute(runnable);
        }
    }

    /**
     * @param list
     * @return 0 -100
     */
    private int calulateProgress(List<DownloadEntity> list) {
        long total = 0;
        long downloaded = 0;
        for (DownloadEntity entity : list) {
            total += (entity.getEnd_position() - entity.getStart_position());
            downloaded += entity.getProgress_position() ;
        }
        return total == 0 ? 0 : (int) (downloaded * 100 / total);
    }

    /**
     * @param url
     * @return 0 -100
     */
    public int getUrlDownloadProgress(String url){
        final List<DownloadEntity> list = DownloadDBHepler.getInstance().getAll(url);
        int progress = calulateProgress(list);
        return progress;
    }
}
