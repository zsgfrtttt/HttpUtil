package com.csz.http.convert;

import com.csz.http.param.HttpResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author caishuzhan
 */
public class JsonConvert implements Convert {

    private Gson mGson = new Gson();

    @Override
    public Object parse(HttpResponse response, Type type) {
        try {
            Reader reader = new InputStreamReader(response.body());
            return mGson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean canConvert(String contentType) {
        return "application/json;charset=UTF-8".equals(contentType);
    }

    @Override
    public Object parse(String json, Type type){
        return mGson.fromJson(json,type);
    }
}
