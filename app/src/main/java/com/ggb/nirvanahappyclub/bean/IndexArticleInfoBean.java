package com.ggb.nirvanahappyclub.bean;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;

import java.util.List;

/**
 * @author : hwj
 * @date : 2023/12/19
 * description : 博客重要信息，不包含博客的全部内容Bean
 */
public class IndexArticleInfoBean extends BaseObservable {

    private String id;
    private String authId;
    private int readCount;
    private int likeCount;
    private int favoriteCount;
    private int commentCount;
    private String title;
    private String createTime;
    private String introduction;
    private String img;
    private String authName;
    private String authPhoto;
    private List<IndexArticleInfoListBean> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public String getReadCountText() {
        return readCount + "";
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getLikeCountText() {
        return likeCount + "";
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getCommentCountText() {
        return commentCount + "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public String getAuthPhoto() {
        return authPhoto;
    }

    public void setAuthPhoto(String authPhoto) {
        this.authPhoto = authPhoto;
    }

    public List<IndexArticleInfoListBean> getTags() {
        return tags;
    }

    public void setTags(List<IndexArticleInfoListBean> tags) {
        this.tags = tags;
    }

    public static class IndexArticleInfoListBean {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
