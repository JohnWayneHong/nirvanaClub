package com.ggb.nirvanahappyclub.bean;

import android.text.TextUtils;
import android.view.View;

/**
 * @author : hwj
 * @date : 2024/1/30
 * description : 我的 功能实体Bean
 */
public class MeFunctionBean {

    public MeFunctionBean(String title) {
        this.title = title;
    }

    public MeFunctionBean(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public MeFunctionBean(String title, String subtitle, boolean isSelected) {
        this.title = title;
        this.subtitle = subtitle;
        this.isSelected = isSelected;
    }

    public String title;
    public String subtitle;
    public boolean isSelected;

    public int isShowSubtitle() {
        if (TextUtils.isEmpty(subtitle)) {
            return View.GONE;
        }else {
            return View.VISIBLE;
        }
    }

    public String getSubtitleText() {
        return TextUtils.isEmpty(subtitle) ? "" : subtitle;
    }

}
