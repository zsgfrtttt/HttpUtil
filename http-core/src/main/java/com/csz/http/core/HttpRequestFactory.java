package com.csz.http.core;

import com.csz.http.param.HttpMethod;
import com.csz.http.param.HttpRequest;

import java.io.IOException;
import java.net.URI;

/**
 * @author caishuzhan
 */
public interface HttpRequestFactory {
    HttpRequest createHttpRequest(URI uri, HttpMethod method) throws IOException;

}
