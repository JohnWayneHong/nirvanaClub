package com.ggb.nirvanahappyclub.bean;

import android.graphics.drawable.Drawable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ggb.common_library.utils.ResourcesUtils;
import com.ggb.nirvanahappyclub.BR;
import com.ggb.nirvanahappyclub.R;

/**
 * @author : hwj
 * @date : 2023/9/27
 * description : 用户标签实体Bean
 */
public class IndexTagBean extends BaseObservable {

    private String id;
    private String name;
    private int hot;

    private boolean isSelected = false;

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

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.nameTextColor);
        notifyPropertyChanged(BR.nameBackground);

    }

    @Bindable
    public int getNameTextColor() {
        return isSelected ? ResourcesUtils.getColor(R.color.index_tags_btn_checked_text_color) : ResourcesUtils.getColor(R.color.index_tags_btn_unchecked_text_color);
    }

    @Bindable
    public Drawable getNameBackground() {
        return isSelected ? ResourcesUtils.getDrawable(R.drawable.index_tags_btn_checked) : ResourcesUtils.getDrawable(R.drawable.index_tags_btn_unchecked);
    }
}
