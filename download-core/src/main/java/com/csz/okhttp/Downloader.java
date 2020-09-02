package com.csz.okhttp;

import android.content.Context;

import com.csz.okhttp.download.DownloadDBHepler;
import com.csz.okhttp.download.FileStorageManager;

/**
 * @author caishuzhan
 */
public class Downloader {
    public static void init(Context context){
        FileStorageManager.getInstance().init(context);
        DownloadDBHepler.getInstance().init(context);
    }
}
