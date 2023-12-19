package com.ggb.nirvanahappyclub.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ggb.nirvanahappyclub.BR;

import java.io.Serializable;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023/12/19
 * description : 博客全部内容Bean
 */
public class ArticleContentBean extends BaseObservable implements Serializable{

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
    private String contentMd;
    private String highlight;
    private boolean hasLiked;
    private boolean hasFavorited;
    private List<IndexArticleInfoListBean> tags;
    private IndexArticleAuthInfoListBean authInfo;

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
        return "浏览量："+ readCount;
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

    public String getContentMd() {
        return contentMd;
    }

    public void setContentMd(String contentMd) {
        this.contentMd = contentMd;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }

    public boolean isHasFavorited() {
        return hasFavorited;
    }

    public void setHasFavorited(boolean hasFavorited) {
        this.hasFavorited = hasFavorited;
    }

    public IndexArticleAuthInfoListBean getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(IndexArticleAuthInfoListBean authInfo) {
        this.authInfo = authInfo;
    }

    public List<IndexArticleInfoListBean> getTags() {
        return tags;
    }

    public void setTags(List<IndexArticleInfoListBean> tags) {
        this.tags = tags;
    }

    public static class IndexArticleInfoListBean implements Serializable{
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
    public static class IndexArticleAuthInfoListBean extends BaseObservable implements Serializable {
        private String id;
        private String nickName;
        private String photo;
        private int publishCount;
        private int readCount;
        private int likeCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public int getPublishCount() {
            return publishCount;
        }

        public void setPublishCount(int publishCount) {
            this.publishCount = publishCount;
        }

        public int getReadCount() {
            return readCount;
        }

        public void setReadCount(int readCount) {
            this.readCount = readCount;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }
    }

}
