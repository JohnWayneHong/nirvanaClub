package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

/**
 * @author : J-T
 * @date : 2022/7/25 13:40
 * description : 扫码状态查询 label Bean类
 */
public class CodeStatusQueryDetailsLabelBean {
    public boolean selected = false;
    public int selectedDrawable;
    public int unselectedDrawable;
    public String title;

    public CodeStatusQueryDetailsLabelBean(int selectedDrawable, int unselectedDrawable, String title) {
        this.selectedDrawable = selectedDrawable;
        this.unselectedDrawable = unselectedDrawable;
        this.title = title;
    }

    public int getIconRes() {
        return selected ? selectedDrawable : unselectedDrawable;
    }

    public int getTitleColor() {
        return selected ? ResourcesUtils.getColor(R.color.blue_26) : ResourcesUtils.getColor(R.color.gray_c7);
    }

    public int getBarVisible() {
        return selected ? View.VISIBLE : View.INVISIBLE;
    }
}
