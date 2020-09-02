package com.csz.http.core;

import com.csz.http.param.HttpHeader;
import com.csz.http.param.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author caishuzhan
 */
public abstract class BufferHttpRequest extends AbstractHttpRequest{
    
    private ByteArrayOutputStream mByteArrayStream = new ByteArrayOutputStream();

    protected OutputStream getBodyOutputStream() {
        return mByteArrayStream;
    }

    protected HttpResponse executeInternal(HttpHeader header) throws IOException {
        byte[] bytes = mByteArrayStream.toByteArray();
        return executeInternal(header,bytes);
    }

    protected abstract HttpResponse executeInternal(HttpHeader header, byte[] bytes) throws IOException;
}
