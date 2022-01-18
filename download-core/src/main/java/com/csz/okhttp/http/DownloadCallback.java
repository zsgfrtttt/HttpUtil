package com.csz.okhttp.http;

import java.io.File;

/**
 * @author caishuzhan
 */
public interface DownloadCallback {

    void onSuccess(File file);

    void onFailure(int code, String msg);

    /**
     * 0 - 100
     */
    void onProgress(int progress);
}

