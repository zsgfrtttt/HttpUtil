package com.csz.app.base.http;

import com.csz.http.param.HttpMethod;
import com.csz.http.service.NetworkCallback;
import com.csz.http.service.NetworkTask;
import com.csz.http.service.WorkStation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author caishuzhan
 */
public class ApiProvider {

    public static void test(String url, Map<String, String> params, NetworkCallback callback) {
        NetworkTask task = new NetworkTask();
        task.setUrl(url);
        task.setMethod(HttpMethod.POST);
        task.setData(encodeParams(params));
        task.setCallback(callback);

        WorkStation.getInstance().add(task);
    }

    public static void wrapTest(String url, Map<String, String> params, final NetworkCallback callback) {
        NetworkTask task = new NetworkTask();
        task.setUrl(url);
        task.setMethod(HttpMethod.POST);
        task.setData(encodeParams(params));
        task.setCallback(callback);

        WorkStation.getInstance().add(task);
    }

    private static byte[] encodeParams(Map<String, String> params) {
        if (params == null) return null;
        StringBuilder builder = new StringBuilder();
        int i = 0;
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(URLEncoder.encode(entry.getKey(), "utf-8")).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                if (i < params.size() - 1) {
                    builder.append("&");
                }
                i++;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.toString().getBytes();
    }
}
