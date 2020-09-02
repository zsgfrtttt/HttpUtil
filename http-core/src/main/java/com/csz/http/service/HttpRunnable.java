package com.csz.http.service;


import com.csz.http.param.HttpRequest;
import com.csz.http.param.HttpResponse;
import com.csz.http.param.HttpStatus;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author caishuzhan
 */
public class HttpRunnable implements Runnable {

    private HttpRequest mHttpRequest;
    private NetworkTask mTask;
    private WorkStation mStation;

    public HttpRunnable(HttpRequest httpRequest, NetworkTask task, WorkStation workStation) {
        this.mHttpRequest = httpRequest;
        this.mTask = task;
        this.mStation = workStation;
    }

    @Override
    public void run() {
        final NetworkCallback callback = mTask.getCallback();
        try {
            mHttpRequest.getBody().write(mTask.getData());
            final HttpResponse response = mHttpRequest.execute();

            String contentType = response.getHeaders().getContentType();
            mTask.setContentType(contentType);

            if (callback != null) {
                if (response.getStatus().isSuccess()) {
                    mStation.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(mTask, new String(getData(response)));
                        }
                    });
                } else {
                    mStation.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(response.getStatus().getCode(), response.getStatusMsg());
                        }
                    });
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            if (callback != null) {
                mStation.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(HttpStatus.UNKNOWN.getCode(), e.getMessage());
                    }
                });
            }
        } finally {
            mStation.finish(mTask);
        }

    }

    private byte[] getData(HttpResponse response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            InputStream inputStream = response.body();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
