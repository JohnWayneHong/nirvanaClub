package com.ggb.nirvanahappyclub.bean;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

/**
 * @author : hwj
 * @date : 2023/12/26
 * description : 玩安卓首頁Bean
 */
public class CommunityAndroidBean {

    private int curPage;
    private List<CommunityAndroidListBean> datas;
    private int offset;
    private boolean over;
    private int pageCount;
    private int size;
    private int total;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public List<CommunityAndroidListBean> getDatas() {
        return datas;
    }

    public void setDatas(List<CommunityAndroidListBean> datas) {
        this.datas = datas;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static class CommunityAndroidListBean {

        private boolean adminAdd;
        private String apkLink;
        private int audit;
        private String author;
        private boolean canEdit;
        private int chapterId;
        private String chapterName;
        private boolean collect;
        private int courseId;
        private String desc;
        private String descMd;
        private String envelopePic;
        private boolean fresh;
        private String host;
        private int id;
        private boolean isAdminAdd;
        private String link;
        private String niceDate;
        private String niceShareDate;
        private String origin;
        private String prefix;
        private String projectLink;
        private long publishTime;
        private int realSuperChapterId;
        private int selfVisible;
        private long shareDate;
        private String shareUser;
        private int superChapterId;
        private String superChapterName;
        private List<CommunityAndroidListTagBean> tags;
        private String title;
        private int type;
        private int userId;
        private int visible;
        private int zan;
        private String top;

        public boolean isAdminAdd() {
            return adminAdd;
        }

        public void setAdminAdd(boolean adminAdd) {
            this.adminAdd = adminAdd;
        }

        public String getApkLink() {
            return apkLink;
        }

        public void setApkLink(String apkLink) {
            this.apkLink = apkLink;
        }

        public int getAudit() {
            return audit;
        }

        public void setAudit(int audit) {
            this.audit = audit;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getAuthorText() {
            return TextUtils.isEmpty(author) ? shareUser : author;
        }

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public int getChapterId() {
            return chapterId;
        }

        public void setChapterId(int chapterId) {
            this.chapterId = chapterId;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getChapterNameText() {
            if (!TextUtils.isEmpty(superChapterName) && !TextUtils.isEmpty(chapterName)) {
                return superChapterName + "/" + chapterName;
            }

            if (!TextUtils.isEmpty(superChapterName)) {
                return superChapterName ;
            }

            if (!TextUtils.isEmpty(chapterName)) {
                return chapterName ;
            }

            return "";
        }

        public boolean isCollect() {
            return collect;
        }

        public void setCollect(boolean collect) {
            this.collect = collect;
        }

        public int getCourseId() {
            return courseId;
        }

        public void setCourseId(int courseId) {
            this.courseId = courseId;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDescMd() {
            return descMd;
        }

        public void setDescMd(String descMd) {
            this.descMd = descMd;
        }

        public String getEnvelopePic() {
            return envelopePic;
        }

        public void setEnvelopePic(String envelopePic) {
            this.envelopePic = envelopePic;
        }

        public int isShowEnvelopePicVisible() {
            return TextUtils.isEmpty(getEnvelopePic()) ? View.GONE : View.VISIBLE;
        }

        public boolean isFresh() {
            return fresh;
        }

        public void setFresh(boolean fresh) {
            this.fresh = fresh;
        }

        public int isShowFresh() {
            return fresh? View.VISIBLE : View.GONE;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isIsAdminAdd() {
            return isAdminAdd;
        }

        public void setIsAdminAdd(boolean isAdminAdd) {
            this.isAdminAdd = isAdminAdd;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getNiceDate() {
            return niceDate;
        }

        public void setNiceDate(String niceDate) {
            this.niceDate = niceDate;
        }

        public String getNiceShareDate() {
            return niceShareDate;
        }

        public void setNiceShareDate(String niceShareDate) {
            this.niceShareDate = niceShareDate;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getProjectLink() {
            return projectLink;
        }

        public void setProjectLink(String projectLink) {
            this.projectLink = projectLink;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }

        public int getRealSuperChapterId() {
            return realSuperChapterId;
        }

        public void setRealSuperChapterId(int realSuperChapterId) {
            this.realSuperChapterId = realSuperChapterId;
        }

        public int getSelfVisible() {
            return selfVisible;
        }

        public void setSelfVisible(int selfVisible) {
            this.selfVisible = selfVisible;
        }

        public long getShareDate() {
            return shareDate;
        }

        public void setShareDate(long shareDate) {
            this.shareDate = shareDate;
        }

        public String getShareUser() {
            return shareUser;
        }

        public void setShareUser(String shareUser) {
            this.shareUser = shareUser;
        }

        public int getSuperChapterId() {
            return superChapterId;
        }

        public void setSuperChapterId(int superChapterId) {
            this.superChapterId = superChapterId;
        }

        public String getSuperChapterName() {
            return superChapterName;
        }

        public void setSuperChapterName(String superChapterName) {
            this.superChapterName = superChapterName;
        }

        public List<CommunityAndroidListTagBean> getTags() {
            return tags;
        }

        public void setTags(List<CommunityAndroidListTagBean> tags) {
            this.tags = tags;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        public Spanned getTitleText() {
            return Html.fromHtml(getTitle());
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getVisible() {
            return visible;
        }

        public void setVisible(int visible) {
            this.visible = visible;
        }

        public int getZan() {
            return zan;
        }

        public void setZan(int zan) {
            this.zan = zan;
        }

        public String getTop() {
            return top;
        }

        public void setTop(String top) {
            this.top = top;
        }

        public int isShowTop() {
            return TextUtils.equals(top,"1") ? View.VISIBLE : View.GONE;
        }

        public int isShowTag() {
            if (tags.size() > 0) {
                return View.VISIBLE;
            }else {
                return View.GONE;
            }
        }

        public String getTagsName() {
            if (tags.size() > 0) {
                return tags.get(0).name;
            }
            return "";
        }

        public static class CommunityAndroidListTagBean {
            private String name;
            private String url;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
