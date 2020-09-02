package com.csz.http.core;

import com.csz.http.param.HttpHeader;
import com.csz.http.param.HttpStatus;

import java.io.InputStream;
import java.util.Iterator;

import kotlin.Pair;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * @author caishuzhan
 */
public class OkHttpResponse extends AbstractHttpResponse {

    private Response mResponse;
    private HttpHeader mHeaders;

    public OkHttpResponse(Response response) {
        this.mResponse = response;
    }

    @Override
    protected InputStream getBodyInternal() {
        return mResponse.body().byteStream();
    }

    @Override
    protected void closeInternal() {
        mResponse.body().close();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.getStatus(mResponse.code());
    }

    @Override
    public String getStatusMsg() {
        return mResponse.message();
    }

    @Override
    public long getContentLength() {
        return mResponse.body().contentLength();
    }

    @Override
    public HttpHeader getHeaders() {
        if (mHeaders == null){
            mHeaders = new HttpHeader();
        }
        mHeaders.clear();
        Headers headers = mResponse.headers();
        Iterator<Pair<String, String>> iterator = headers.iterator();
        while (iterator.hasNext()){
            Pair<String, String> header = iterator.next();
            mHeaders.put(header.getFirst(),header.getSecond());
        }
        return mHeaders;
    }
}
