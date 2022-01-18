# HttpUtil
包含两个库：下载库和请求库


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
	##方式1 (包含下载库和请求封装库）
	implementation 'com.github.zsgfrtttt:HttpUtil:1.1.2'
	##方式2 (子库拆分依赖，需要同时依赖okhttp）
	implementation 'com.github.zsgfrtttt.HttpUtil:download-core:1.1.2@aar'
    	implementation 'com.github.zsgfrtttt.HttpUtil:http-core:1.1.2@aar'
    	implementation ("com.squareup.okhttp3:okhttp:4.2.1")
```

### 基本使用

**1.下载文件**

```java
 Downloader.init(this);
DownloadManager.getInstance().download(url, new DownloadCallback() {

            @Override
            public void onSuccess(final File file) {
                Log.i("csz","onSuccess   "+ file.length());
                Toast.makeText(DownloadActivity.this, "download  succ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.e("csz", "e : " + msg);
            }

            @Override
            public void onProgress(final int progress) {
                seekBar.setProgress(progress);
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
