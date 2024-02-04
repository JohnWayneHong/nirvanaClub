package com.ggb.nirvanahappyclub.bean;

import android.view.View;

public class DevelopJokesJokeBean {
    public String content;
    public String imageSize;
    public String imageUrl;
    public int type;

    public int isShowPicture() {
        if (type < 2) {
            return View.GONE;
        } else {
            return View.VISIBLE;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //    @SerializedName("addTime")val addTime:String,
//    @SerializedName("audit_msg")val audit_msg:String,
//    @SerializedName("content")val content:String,
//    @SerializedName("hot")val hot:Boolean,
//    @SerializedName("imageSize")val imageSize:String,
//    @SerializedName("imageUrl")val imageUrl:String,
//    @SerializedName("jokesId")val jokesId:Int,
//    @SerializedName("latitudeLongitude")val latitudeLongitude:String,
//    @SerializedName("showAddress")val showAddress:String,
//    @SerializedName("thumbUrl")val thumbUrl:String,
//    @SerializedName("type")val type:Int,
//    @SerializedName("userId")val userId:Int,
//    @SerializedName("videoSize")val videoSize:String,
//    @SerializedName("videoTime")val videoTime:Int,
//    @SerializedName("videoUrl")val videoUrl:String
}
