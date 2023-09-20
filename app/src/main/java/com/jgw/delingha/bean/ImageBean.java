package com.jgw.delingha.bean;

import android.net.Uri;
import android.text.TextUtils;

import com.jgw.delingha.network.HttpUtils;

/**
 * Created by XiongShaoWu
 * on 2019/10/8
 */
public class ImageBean {
    public String imgUrl;
    public Uri imgUri;
    public int resId;

    public ImageBean(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ImageBean(Uri uri) {
        this.imgUri = uri;
    }

    public ImageBean(int resId) {
        this.resId = resId;
    }

    public String getFinalImgUrl() {
        if (TextUtils.isEmpty(imgUrl)||imgUrl.contains("http")){
            return imgUrl;
        }
        return HttpUtils.getFileUrl()+imgUrl;
    }
}
