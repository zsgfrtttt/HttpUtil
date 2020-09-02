package com.csz.okhttp.http;

/**
 * @author caishuzhan
 */
public enum HttpError {

    NETWORK_ERROR(10,"请求服务器异常"),
    CONTENT_LENGTH_ERROR(11,"content length -1"),
    TASK_RUNNING_ERROR(12,"任务执行中");


    private int code;
    private String msg;

    HttpError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
