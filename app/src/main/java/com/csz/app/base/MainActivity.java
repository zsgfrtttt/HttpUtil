package com.csz.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.csz.app.R;
import com.csz.app.base.download.DownloadActivity;
import com.csz.app.base.http.HttpActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * @author caishuzhan
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void download(View view) {
        startActivity(new Intent(this, DownloadActivity.class));
    }

    public void http(View view) {
        startActivity(new Intent(this, HttpActivity.class));
    }
}
