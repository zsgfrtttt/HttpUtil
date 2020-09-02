# HttpUtil
包含两个库：下载库和请求库，可以自行选择独立依赖
```
implementation 'com.github.zsgfrtttt:HttpUtil:download-core:1.0.1@aar'
implementation 'com.github.zsgfrtttt:HttpUtil:http-core:1.0.1@aar'
```


### 引入依赖 
在Project的build.gradle在添加以下代码
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在Module的build.gradle在添加以下代码
```
	implementation 'com.github.zsgfrtttt:HttpUtil:1.0.1'
```

### 基本使用

**1.下载文件**

```java
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
```

**2、网络请求**
```java
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
```

##### 其他问题可以联系邮箱1058079995@qq.com
