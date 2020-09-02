package com.csz.http.core;

import com.csz.http.param.HttpHeader;
import com.csz.http.param.HttpMethod;
import com.csz.http.param.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author caishuzhan
 */
public class OkHttpRequest extends BufferHttpRequest {

    private OkHttpClient mClient;
    private HttpMethod mMethod;
    private String mUrl;

    public OkHttpRequest(OkHttpClient client, HttpMethod method, String url) {
        this.mClient = client;
        this.mMethod = method;
        this.mUrl = url;
    }

    @Override
    protected HttpResponse executeInternal(HttpHeader header, byte[] bytes) throws IOException {
        boolean hasBody = mMethod == HttpMethod.POST;
        RequestBody body = null;
        if (hasBody){
            body = RequestBody.create(bytes, MediaType.parse("application/x-www-form-urlencoded"));
        }
        Request.Builder builder = new Request.Builder().url(mUrl).method(mMethod.name(),body);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(),entry.getValue());
        }
        return new OkHttpResponse(mClient.newCall(builder.build()).execute());
    }

    @Override
    public HttpMethod getMethod() {
        return mMethod;
    }

    @Override
    public URI getUri() {
        return URI.create(mUrl);
    }
}
