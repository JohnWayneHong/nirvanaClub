package com.ggb.nirvanahappyclub.bean;

import androidx.databinding.BaseObservable;

/**
 * @author : hwj
 * @date : 2024/7/24
 * description :
 */
public class MineSettingListBean extends BaseObservable {

    public MineSettingListBean(String childTitle) {
        this.childTitle = childTitle;
    }

    public MineSettingListBean(String childTitle, String childSubTitle) {
        this.childTitle = childTitle;
        this.childSubTitle = childSubTitle;
    }

    private String childTitle;
    private String childSubTitle;

    public String getChildTitle() {
        return childTitle;
    }

    public void setChildTitle(String childTitle) {
        this.childTitle = childTitle;
    }

    public String getChildSubTitle() {
        return childSubTitle;
    }

    public void setChildSubTitle(String childSubTitle) {
        this.childSubTitle = childSubTitle;
    }
}
