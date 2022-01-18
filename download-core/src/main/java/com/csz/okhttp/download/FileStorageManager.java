package com.csz.okhttp.download;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

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
        this.mContext = context.getApplicationContext();
    }

    /**
     * MD5 + 后缀 作为文件名
     * @param url
     * @return
     */
    public File getFileByName(String url) {
        File parent;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            parent = mContext.getExternalCacheDir();
        } else {
            parent = mContext.getCacheDir();
        }
        String name = Uri.parse(url).getLastPathSegment();
        String suffix = "";
        if (!TextUtils.isEmpty(name) && name.contains(".")) {
            suffix = name.substring(name.lastIndexOf("."));
        }
        String fileName = MD5Util.generateCode(url);
        File file = new File(parent, fileName + suffix);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
