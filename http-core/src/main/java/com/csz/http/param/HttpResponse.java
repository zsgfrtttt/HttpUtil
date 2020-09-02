package com.csz.http.param;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author caishuzhan
 */
public interface HttpResponse extends Header, Closeable {

    HttpStatus getStatus();

    String getStatusMsg();

    InputStream body() throws IOException;

    void close() throws IOException;

    long getContentLength();
}
