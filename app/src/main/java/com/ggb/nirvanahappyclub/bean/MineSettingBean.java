package com.ggb.nirvanahappyclub.bean;

import androidx.databinding.BaseObservable;

import java.io.Serializable;
import java.util.List;

public class MineSettingBean extends BaseObservable implements Serializable {

    public MineSettingBean(String title) {
        this.title = title;
    }

    public MineSettingBean(String title, List<MineSettingListBean> childList) {
        this.title = title;
        this.childList = childList;
    }

    private String title;
    private List<MineSettingListBean> childList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MineSettingListBean> getChildList() {
        return childList;
    }

    public void setChildList(List<MineSettingListBean> childList) {
        this.childList = childList;
    }
}