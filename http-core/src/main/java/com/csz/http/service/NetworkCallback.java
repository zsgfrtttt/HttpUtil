package com.csz.http.service;

/**
 * @author caishuzhan
 */
public abstract class NetworkCallback<T>{
    public abstract void onSuccess(NetworkTask task,T t);

    public abstract void onFailure(int code,String message);
}
