package com.csz.http.core;

import com.csz.http.param.HttpMethod;
import com.csz.http.param.HttpRequest;
import com.csz.http.util.Utils;

import java.io.IOException;
import java.net.URI;

/**
 * @author caishuzhan
 */
public class HttpRequestProvider {

    private static boolean OKHTTP_REQUEST = Utils.isExitsClass("okhttp3.OkHttpClient");

    private HttpRequestFactory mHttpRequestFactory;

    public HttpRequestProvider() {
        if (OKHTTP_REQUEST){
            mHttpRequestFactory = new OkHttpRequestFactory();
        } else{
            mHttpRequestFactory = new OriginHttpRequestFactory();
        }
    }

    public HttpRequest getHttpRequest(URI uri, HttpMethod method) throws IOException {
        return mHttpRequestFactory.createHttpRequest(uri,method);
    }

    public HttpRequestFactory getHttpRequestFactory() {
        return mHttpRequestFactory;
    }

    public void setHttpRequestFactory(HttpRequestFactory httpRequestFactory) {
        this.mHttpRequestFactory = mHttpRequestFactory;
    }
}
