package com.csz.http.core;

import com.csz.http.param.HttpHeader;
import com.csz.http.param.HttpRequest;
import com.csz.http.param.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author caishuzhan
 */
public abstract class AbstractHttpRequest implements HttpRequest {

    private HttpHeader mHeader = new HttpHeader();

    private GZIPOutputStream mGzipOutputStream;

    private boolean mExecuted;

    @Override
    public HttpHeader getHeaders() {
        return mHeader;
    }

    @Override
    public OutputStream getBody() throws IOException {
        OutputStream outputStream = getBodyOutputStream();
        if (isGzip()){
            return getGzipOutputStream(outputStream);
        }
        return outputStream;
    }

    @Override
    public HttpResponse execute() throws IOException {
        if (mGzipOutputStream != null){
            mGzipOutputStream.close();
        }
        HttpResponse response = executeInternal(mHeader);
        mExecuted = true;
        return response;
    }

    private OutputStream getGzipOutputStream(OutputStream outputStream) throws IOException {
        if (mGzipOutputStream == null){
            mGzipOutputStream = new GZIPOutputStream(outputStream);
        }
        return mGzipOutputStream;
    }

    private boolean isGzip() {
        String encodeing = getHeaders().getContentEncoding();
        if ("gzip".equals(encodeing)){
            return true;
        }
        return false;
    }

    protected abstract OutputStream getBodyOutputStream();

    protected abstract HttpResponse executeInternal(HttpHeader mHeader) throws IOException;
}
