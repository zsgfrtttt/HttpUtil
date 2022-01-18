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
import com.csz.okhttp.util.MD5Util;

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
    private String url = "https://oss-miaoyin.looyu.vip/android/4.0.2app-miaoyin-release.apk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        iv = findViewById(R.id.iv);
        seekBar = findViewById(R.id.progress);

        DownloadManager.getInstance().download(url, new DownloadCallback() {

            @Override
            public void onSuccess(final File file) { //  225461208   e7f5e25d790547c3b320d8f5d139623b   d377d22efce3efa0fb76b276c7139411
                Log.i("csz","onSuccess   "+ file.length() +"   " + MD5Util.getFileMD5(file)); //225461208  cb3f257daa7187b892f857d858539213    bf04e9cca2d88a46830b4e75b8577318
                Toast.makeText(DownloadActivity.this, "download  succ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.e("csz", "e : " + msg);
            }

            @Override
            public void onProgress(final int progress) {
                Log.i("csz","onProgress   " +progress);
                seekBar.setMax(100);
                seekBar.setProgress(progress);
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
