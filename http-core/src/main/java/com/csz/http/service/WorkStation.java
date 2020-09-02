package com.csz.http.service;

import android.os.Handler;
import android.os.Looper;

import com.csz.http.convert.Convert;
import com.csz.http.convert.StringConvert;
import com.csz.http.core.HttpRequestProvider;
import com.csz.http.param.HttpRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author caishuzhan
 */
public class WorkStation {

    private static final int MAX_RUNNING_TASK = 30;

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {

        private AtomicInteger mIndex = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "http thread :" + mIndex.incrementAndGet());
        }
    });
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Deque<NetworkTask> mRunningtask = new ArrayDeque<>();
    private Deque<NetworkTask> mCache = new ArrayDeque<>();
    private static List<Convert> sConverts = new ArrayList<>();

    static {
        // sConverts.add(new JsonConvert());
        sConverts.add(new StringConvert());
    }

    private HttpRequestProvider mProvider;

    private static final class Holder {
        private static final WorkStation INSTANCE = new WorkStation();
    }

    public static WorkStation getInstance() {
        return Holder.INSTANCE;
    }

    private WorkStation() {
        mProvider = new HttpRequestProvider();
    }

    public synchronized void add(NetworkTask task) {
        if (mRunningtask.size() >= MAX_RUNNING_TASK) {
            mCache.add(task);
        } else {
            executeHttpRunnableByTask(task);
        }
    }

    private void executeHttpRunnableByTask(NetworkTask task) {
        task.setCallback(new WrapperHttpCallback(task.getCallback(), sConverts));
        mRunningtask.add(task);
        try {
            HttpRequest request = mProvider.getHttpRequest(URI.create(task.getUrl()), task.getMethod());
            HttpRunnable runnable = new HttpRunnable(request, task, this);
            THREAD_POOL_EXECUTOR.execute(runnable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void finish(NetworkTask task) {
        mRunningtask.remove(task);
        if (mRunningtask.size() >= MAX_RUNNING_TASK) {
            return;
        }
        if (mCache.size() > 0) {
            for (; mRunningtask.size() < MAX_RUNNING_TASK && mCache.size() > 0; ) {
                NetworkTask first = mCache.removeFirst();
                executeHttpRunnableByTask(first);
            }
        }

    }

    public Handler getMainHandler() {
        return mMainHandler;
    }
}
