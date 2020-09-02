package com.csz.http.core;

import com.csz.http.param.HttpMethod;
import com.csz.http.param.HttpRequest;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author caishuzhan
 */
public class OkHttpRequestFactory implements HttpRequestFactory {

    public OkHttpClient mClient;

    public OkHttpRequestFactory(){
        mClient = new OkHttpClient();
    }

    public OkHttpRequestFactory(OkHttpClient client){
        mClient = client;
    }

    @Override
    public HttpRequest createHttpRequest(URI uri, HttpMethod method) {
        return new OkHttpRequest(mClient,method,uri.toString());
    }

    public void setReadTimeOut(int readTimeOut){
        this.mClient = mClient.newBuilder().readTimeout(readTimeOut, TimeUnit.MILLISECONDS).build();
    }

    public void setWriteTimeOut(int writeTimeOut){
        this.mClient = mClient.newBuilder().writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS).build();
    }

    public void setConnectTimeOut(int connectTimeOut){
        this.mClient = mClient.newBuilder().connectTimeout(connectTimeOut, TimeUnit.MILLISECONDS).build();
    }
}
