package com.csz.http.service;

import com.csz.http.convert.Convert;
import com.csz.http.convert.JsonConvert;
import com.csz.http.convert.StringConvert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author caishuzhan
 */
public class WrapperHttpCallback extends NetworkCallback<String> {

    private NetworkCallback mCallback;
    private List<Convert> mConverts;

    public WrapperHttpCallback(NetworkCallback callback, List<Convert> converts) {
        this.mCallback = callback;
        this.mConverts = converts;
    }

    @Override
    public void onSuccess(NetworkTask task, String t) {
        String contentType = task.getContentType();
        for (Convert convert : mConverts) {
            if (convert.canConvert(contentType)) {
                Object o = null;
                if (convert.getClass() == JsonConvert.class) {
                    o = convert.parse(t, getType());
                }
                if (convert.getClass() == StringConvert.class) {
                    o = t;
                }
                mCallback.onSuccess(task, o);
            }
        }
    }

    public Type getType() {
        Type genericSuperclass = mCallback.getClass().getGenericSuperclass();
        return ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    }

    @Override
    public void onFailure(int code, String message) {
        mCallback.onFailure(code, message);
    }
}
