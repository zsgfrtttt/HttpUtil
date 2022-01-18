package com.csz.okhttp.download.db;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 用于存储下载的分段数据
 */
public class DownloadDBHepler {

    private static final String SP_NAME = "DownloadDBHepler";

    private SharedPreferences mPreferences;

    private DownloadDBHepler() {
    }

    public void init(Context context) {
        Context application = context.getApplicationContext();
        mPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    private static class Holder {
        private static DownloadDBHepler instance = new DownloadDBHepler();
    }

    public static DownloadDBHepler getInstance() {
        return Holder.instance;
    }

    public void insertOrReplace(DownloadEntity entity) {
        mPreferences.edit().putString(generateKey(entity), entity.toString()).apply();
    }

    private String generateKey(DownloadEntity entity) {
        return entity.getDownload_url() + "_" + entity.getStart_position() + "_" + entity.getEnd_position();
    }

    public List<DownloadEntity> getAll(String url) {
        Map<String, String> all = (Map<String, String>) mPreferences.getAll();
        List<DownloadEntity> list = new ArrayList<>();
        if (all != null) {
            for (Map.Entry<String, String> entry : all.entrySet()) {
                String value = entry.getValue();
                try {
                    JSONObject obj = new JSONObject(value);
                    if (url.equals(obj.getString("download_url"))) {
                        DownloadEntity entity = new DownloadEntity();
                        entity.setStart_position(obj.getLong("start_position"));
                        entity.setProgress_position(obj.getLong("progress_position"));
                        entity.setEnd_position(obj.getLong("end_position"));
                        entity.setDownload_url(obj.getString("download_url"));
                        entity.setThread_id(obj.getInt("thread_id"));
                        if (generateKey(entity).equals(entry.getKey())){
                            list.add(entity);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

}
