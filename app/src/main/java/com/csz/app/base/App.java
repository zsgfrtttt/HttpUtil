package com.csz.app.base;

import android.app.Application;

import com.csz.okhttp.Downloader;
import com.csz.okhttp.download.FileStorageManager;

/**
 * @author caishuzhan
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Downloader.init(this);
    }
}
