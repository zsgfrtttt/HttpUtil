package com.csz.http.convert;

import com.csz.http.param.HttpResponse;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * @author caishuzhan
 */
public class StringConvert implements Convert {

    @Override
    public Object parse(HttpResponse response, Type type) {
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        byte[] buffer = new byte[100];
        int len= 0;
        try {
            while ((len = response.body().read(buffer)) != -1){
                byteArrayOutputStream.write(buffer,0,len);
            }
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return new String(byteArrayOutputStream.toByteArray());
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
        return json;
    }
}
