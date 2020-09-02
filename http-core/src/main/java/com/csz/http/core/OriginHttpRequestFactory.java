package com.csz.http.core;

import com.csz.http.param.HttpMethod;
import com.csz.http.param.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * @author caishuzhan
 */
public class OriginHttpRequestFactory implements HttpRequestFactory {

    private HttpURLConnection mConnection;

    public OriginHttpRequestFactory() {
    }

    @Override
    public HttpRequest createHttpRequest(URI uri, HttpMethod method) throws IOException{
        mConnection = (HttpURLConnection) uri.toURL().openConnection();
        return new OriginHttpRequest(mConnection,method,uri.toString());
    }

    public void setReadTimeOut(int readTimeOut){
        this.mConnection.setReadTimeout(readTimeOut);
    }

    public void setConnectTimeOut(int connectTimeOut){
        this.mConnection.setConnectTimeout(connectTimeOut);
    }
}
