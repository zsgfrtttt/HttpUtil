package com.csz.okhttp.http;

import java.io.File;

/**
 * @author caishuzhan
 */
public interface DownloadCallback {

    void onSuccess(File file);

    void onFailure(int code, String msg);

    void progress(int progress);
}

