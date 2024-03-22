package com.ggb.nirvanahappyclub.network.okhttp;


import androidx.annotation.NonNull;

import com.ggb.common_library.http.okhttp.CommonInterceptor;
import com.ggb.common_library.utils.LogUtils;
import com.ggb.common_library.utils.MMKVUtils;
import com.ggb.nirvanahappyclub.bean.CookieBean;
import com.ggb.nirvanahappyclub.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
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
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> list) {
                        List<CookieBean> cookieBeanList = new ArrayList<>();

                        if (list.isEmpty()) {
                            return;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            LogUtils.xswShowLog("响应的cookie是"+ list.get(i));
                            CookieBean cookieBean = new CookieBean();
                            cookieBean.setCookie(list.get(i).toString());
                            cookieBean.setCookieName(list.get(i).name());
                            cookieBeanList.add(cookieBean);
                            MMKVUtils.save(ConstantUtil.USER_TOKEN,list.get(i).toString());
                        }
                        MMKVUtils.saveTempData(cookieBeanList);
                    }

                    @NonNull
                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
                        List<CookieBean> cookieBeanList = MMKVUtils.getTempDataList(CookieBean.class);
                        List<Cookie> cookieList = new ArrayList<>();
                        if (cookieBeanList != null && !cookieBeanList.isEmpty()) {
                            for (int i = 0; i < cookieBeanList.size(); i++) {
                                Cookie cookie = Cookie.parse(httpUrl,cookieBeanList.get(i).toString());
                                if (cookie != null) {
                                    if (cookie.expiresAt() > System.currentTimeMillis()) {
                                        cookieList.add(cookie);
                                    }else {
                                        cookieBeanList.remove(cookieBeanList.get(i));
                                        MMKVUtils.saveTempData(cookieBeanList);
                                    }
                                }

                            }
                        }

                        return cookieList;
                    }
                })
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
