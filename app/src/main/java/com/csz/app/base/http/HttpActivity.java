package com.csz.app.base.http;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.csz.app.R;
import com.csz.http.service.NetworkCallback;
import com.csz.http.service.NetworkTask;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author caishuzhan
 */
public class HttpActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        tv = findViewById(R.id.tv);
    }

    public void request(View view) {
        Map map = new HashMap();
        map.put("token", "abc");
        ApiProvider.test("http://59.37.129.253:8084/appServer/userInfo/faceHack", map, new NetworkCallback<Product>() {
            @Override
            public void onSuccess(NetworkTask task, Product o) {
                tv.setText(o.toString());
            }

            @Override
            public void onFailure(int code, String message) {
                tv.setText("error   "+message);
            }
        });
    }

    public class Product {

        private String status;
        private String code;
        private String msg;
        private String debugMsg;
        private String data;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getDebugMsg() {
            return debugMsg;
        }

        public void setDebugMsg(String debugMsg) {
            this.debugMsg = debugMsg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Product{" + "status='" + status + '\'' + ", code='" + code + '\'' + ", msg='" + msg + '\'' + ", debugMsg='" + debugMsg + '\'' + ", data='" + data + '\'' + '}';
        }
    }

}
