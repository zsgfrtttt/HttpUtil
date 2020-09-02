package com.csz.http.core;

import com.csz.http.param.HttpHeader;
import com.csz.http.param.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author caishuzhan
 */
public class OriginHttpResponse extends AbstractHttpResponse {

    private HttpURLConnection mConnection;

    public OriginHttpResponse(HttpURLConnection connection) {
        this.mConnection = connection;
    }


    @Override
    protected InputStream getBodyInternal() throws IOException {
        return mConnection.getInputStream();
    }

    @Override
    protected void closeInternal() {
        mConnection.disconnect();
    }

    @Override
    public HttpStatus getStatus() {
        try {
            return HttpStatus.getStatus(mConnection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getStatusMsg() {
        try {
            return mConnection.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getContentLength() {
        return mConnection.getContentLength();
    }

    @Override
    public HttpHeader getHeaders() {
        HttpHeader header = new HttpHeader();
        for (Map.Entry<String, List<String>> entry : mConnection.getHeaderFields().entrySet()) {
            header.put(entry.getKey(),entry.getValue().get(0));
        }
        return header;
    }
}
