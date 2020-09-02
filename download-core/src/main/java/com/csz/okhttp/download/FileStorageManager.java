package com.csz.okhttp.download;

import android.content.Context;
import android.os.Environment;

import com.csz.okhttp.util.MD5Util;

import java.io.File;
import java.io.IOException;

/**
 * @author caishuzhan
 */
public class FileStorageManager {

    private static final FileStorageManager ourInstance = new FileStorageManager();

    public static FileStorageManager getInstance() {
        return ourInstance;
    }

    private FileStorageManager() {
    }

    private Context mContext;

    public void init(Context context) {
        this.mContext = context;
    }

    public File getFileByName(String url){
        File parent;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            parent = mContext.getExternalCacheDir();
        } else{
            parent = mContext.getCacheDir();
        }

        String fileName = MD5Util.generateCode(url);
        File file = new File(parent,fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
