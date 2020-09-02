package com.csz.http.core;

import com.csz.http.param.HttpHeader;
import com.csz.http.param.HttpMethod;
import com.csz.http.param.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

/**
 * @author caishuzhan
 */
public class OriginHttpRequest extends BufferHttpRequest {

    private HttpURLConnection mConnection;
    private HttpMethod mMethod;
    private String mUrl;

    public OriginHttpRequest(HttpURLConnection connection, HttpMethod method, String url) {
        this.mConnection = connection;
        this.mMethod = method;
        this.mUrl = url;
    }

    @Override
    protected HttpResponse executeInternal(HttpHeader header, byte[] bytes) throws IOException {
        for (Map.Entry<String, String> entry : header.entrySet()) {
            mConnection.addRequestProperty(entry.getKey(),entry.getValue());
        }
        mConnection.setDoOutput(true);
        mConnection.setDoInput(true);
        mConnection.setRequestMethod(mMethod.name());
        mConnection.connect();
        if (bytes!= null && bytes.length >0){
            OutputStream stream = mConnection.getOutputStream();
            stream.write(bytes);
            stream.close();
        }
        return new OriginHttpResponse(mConnection);
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
