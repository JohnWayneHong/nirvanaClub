package com.ggb.nirvanahappyclub.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ggb.common_library.utils.ResourcesUtils;
import com.ggb.nirvanahappyclub.R;

public class MainMenuBean extends BaseObservable {
    private int index = 0;

    public void setIndex(int index) {
        this.index = index;
        notifyChange();
    }

    @Bindable
    public int getToolsTableImageResId() {
        return index == 0 ? R.drawable.icon_home_selected : R.drawable.icon_home_nomal;
    }

    @Bindable
    public int getToolsTableTextColor() {
        return getMainMenuBeanTextColor(index == 0);
    }

    @Bindable
    public int getSettingCenterImageResId() {
        return index == 1 ? R.drawable.icon_setting_center_selected : R.drawable.icon_setting_center_nomal;
    }

    @Bindable
    public int getSettingCenterTextColor() {
        return getMainMenuBeanTextColor(index == 1);
    }

    private int getMainMenuBeanTextColor(boolean select) {
        int i = select ? R.color.main_color : R.color.gray_91;
        return ResourcesUtils.getColor(i);
    }
}
