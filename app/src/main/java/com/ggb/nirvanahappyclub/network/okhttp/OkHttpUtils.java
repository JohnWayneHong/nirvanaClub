package com.ggb.nirvanahappyclub.network.okhttp;


import com.jgw.common_library.http.okhttp.CommonInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by xsw on 2016/8/23.
 */
public class OkHttpUtils {

    public static final int TIMEOUT = 30;
    public static final int TIMEOUT_LONG_TIME = 600;

    /**
     * 全局唯一通用Client防止文件描述符和内存泄漏
     */
    public static OkHttpClient getOkHttpClient() {
        return SingleInstance.INSTANCE;
    }

    private static class SingleInstance {
        private static final OkHttpClient INSTANCE = new OkHttpClient().newBuilder()
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .addInterceptor(new CustomInterceptor())
                .addInterceptor(new CommonInterceptor())
                .build();
    }

    private static volatile OkHttpClient client;

    public static OkHttpClient getLongTimeOkHttpClient() {
        if (client == null) {
            synchronized (OkHttpUtils.class) {
                if (client == null) {
                    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                    client = builder
                            .readTimeout(TIMEOUT_LONG_TIME, TimeUnit.SECONDS)//设置读取超时时间
                            .writeTimeout(TIMEOUT_LONG_TIME, TimeUnit.SECONDS)//设置写的超时时间
                            .connectTimeout(TIMEOUT_LONG_TIME, TimeUnit.SECONDS)//设置连接超时时间
                            .addInterceptor(new CustomInterceptor())
                            .addInterceptor(new CommonInterceptor())
                            .build();
                }
            }
        }
        return client;
    }
}
