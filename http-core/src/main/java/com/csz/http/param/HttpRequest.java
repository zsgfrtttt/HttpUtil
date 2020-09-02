package com.csz.http.param;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author caishuzhan
 */
public interface HttpRequest extends Header{

    HttpMethod getMethod();

    URI getUri();

    OutputStream getBody() throws IOException;

    HttpResponse execute() throws IOException;
}
