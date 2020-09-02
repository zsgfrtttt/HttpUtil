package com.csz.okhttp.http;

import com.csz.okhttp.download.FileStorageManager;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author caishuzhan
 */
public class HttpManager {

    private OkHttpClient mOkHttpClient;

    private static final class Holder {
        private static final HttpManager INSTANCE = new HttpManager();
    }

    public static HttpManager getInstance() {
        return HttpManager.Holder.INSTANCE;
    }

    private HttpManager() {
        mOkHttpClient = new OkHttpClient();
    }

    /**
     * 同步请求
     *
     * @param url
     * @return
     */
    public Response syncRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 同步请求,分段
     *
     * @param url
     * @return
     */
    public Response syncRequest(String url,long start,long end) {
        Request request = new Request.Builder().url(url).header("Range","bytes="+start+"-"+end).build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 异步请求
     *
     * @param url
     * @return
     */
    public void asyncRequest(String url, @NotNull final Callback callback) {
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 异步请求,直接单线程下载
     *
     * @param url
     * @return
     */
    public void asyncRequest(String url, @NotNull final DownloadCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (callback != null){
                     callback.onFailure(HttpError.NETWORK_ERROR.getCode(),e.getMessage());
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (callback == null) return;
                if (!response.isSuccessful()){
                    callback.onFailure(response.code(),response.message());
                } else {
                    File file = FileStorageManager.getInstance().getFileByName(call.request().url().toString());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] bytes = new byte[1024 * 16];
                    InputStream inputStream = response.body().byteStream();
                    int len = 0;
                    long total = response.body().contentLength();
                    long progress = 0;
                    while ((len = inputStream.read(bytes)) != -1){
                        fileOutputStream.write(bytes,0,len);
                        progress += len;
                        callback.progress((int) (progress * 100 / total));
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    callback.onSuccess(file);
                }
            }
        });
    }
}
