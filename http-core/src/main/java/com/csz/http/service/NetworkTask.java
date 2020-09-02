package com.csz.http.service;

import com.csz.http.param.HttpMethod;

/**
 * @author caishuzhan
 */
public class NetworkTask {

    private HttpMethod method;
    private String url;
    private byte[] data;
    private NetworkCallback callback;

    private String contentType;

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public NetworkCallback getCallback() {
        return callback;
    }

    public void setCallback(NetworkCallback callback) {
        this.callback = callback;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
