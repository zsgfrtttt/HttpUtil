package com.csz.http.core;

import com.csz.http.param.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author caishuzhan
 */
public abstract class AbstractHttpResponse implements HttpResponse {

    public static final String GZIP = "gzip";

    private GZIPInputStream mGzipInputStream;

    private boolean isGzip(){
        String encoding = getHeaders().getContentEncoding();
        if (GZIP.equals(encoding)){
            return true;
        }
        return false;
    }

    @Override
    public InputStream body() throws IOException {
        InputStream inputStream = getBodyInternal();
        if (isGzip()){
            return getBodyGzip(inputStream);
        }
        return inputStream;
    }

    @Override
    public void close() throws IOException {
        if (mGzipInputStream != null){
            mGzipInputStream.close();
        }
        closeInternal();
    }

    private InputStream getBodyGzip(InputStream inputStream) throws IOException {
        if (mGzipInputStream == null){
            mGzipInputStream = new GZIPInputStream(inputStream);
        }
        return mGzipInputStream;
    }

    protected abstract InputStream getBodyInternal() throws IOException;

    protected abstract void closeInternal();
}
