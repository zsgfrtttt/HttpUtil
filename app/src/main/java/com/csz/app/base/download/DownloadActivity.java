package com.csz.app.base.download;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.csz.app.R;
import com.csz.okhttp.http.DownloadCallback;
import com.csz.okhttp.http.DownloadManager;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//http://hot.m.shouji.360tpcdn.com/191206/e01002387a835bbe3aa8c21ade9f3d3a/com.qihoo360.mobilesafe_266.apk
//https://data.photo-ac.com/data/thumbnails/62/6285acd688ee0eac57c5034f5218a192_w.jpeg
//https://big1.vqs.com/zqw/20191121/A17AFDD0DFBA14BC53F80A6A06185C95.apk
/**
 * @author caishuzhan
 */
public class DownloadActivity extends AppCompatActivity {

    private ImageView iv;
    private SeekBar seekBar;
    private String url = "https://big1.vqs.com/zqw/20191121/A17AFDD0DFBA14BC53F80A6A06185C95.apk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        iv = findViewById(R.id.iv);
        seekBar = findViewById(R.id.progress);

        DownloadManager.getInstance().download(url, new DownloadCallback() {

            @Override
            public void onSuccess(final File file) {
                //由于系统原因file的length和MD5会有延迟
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DownloadActivity.this, "download  succ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.e("csz", "e : " + msg);
            }

            @Override
            public void progress(final int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress(progress);
                    }
                });
            }
        });
    }

    public void pause(View view) {
        DownloadManager.getInstance().pause(url);
    }

    public void start(View view) {
        DownloadManager.getInstance().resume(url);
    }
}
