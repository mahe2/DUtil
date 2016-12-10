package com.othershe.dutil.net;

import com.othershe.dutil.callback.DownloadCallback;
import com.othershe.dutil.callback.FileCallback;
import com.othershe.dutil.download.FileUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

public class OkHttpManager {
    private OkHttpClient okHttpClient;
    private FileCallback fileCallback;
    private String url;
    private String path;
    private String name;

    private OkHttpManager() {
        okHttpClient = new OkHttpClient.Builder()
//                .addNetworkInterceptor(getInterceptor())
                .build();
    }

    public static OkHttpManager getInstance() {
        return OkHttpHolder.instance;
    }

    private static class OkHttpHolder {
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public void initRequest(String url, long start, long end, final Callback callback) {
        this.url = url;
        this.path = path;
        this.name = name;

        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + start + "-" + end)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    public void initRequest(String url, final Callback callback) {
        this.url = url;
        this.path = path;
        this.name = name;

        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=0-")
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    private Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), fileCallback))
                        .build();
            }
        };
    }
}