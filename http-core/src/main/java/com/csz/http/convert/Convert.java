package com.csz.http.convert;

import com.csz.http.param.HttpResponse;

import java.lang.reflect.Type;

/**
 * @author caishuzhan
 */
public interface Convert {

    Object parse(HttpResponse response , Type type);

    boolean canConvert(String contentType);

    Object parse(String json, Type type);

}
