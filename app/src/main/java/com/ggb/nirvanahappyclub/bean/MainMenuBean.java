package com.ggb.nirvanahappyclub.bean;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.ggb.common_library.utils.ResourcesUtils;
import com.ggb.nirvanahappyclub.R;

public class MainMenuBean extends BaseObservable {

    //0 首页 1 社区 2 关注 3 消息 4 我的
    private int index = 0;

    public void setIndex(int index) {
        this.index = index;
        notifyChange();
    }

    @Bindable
    public int getToolsHomeImageResId() {
        return index == 0 ? R.drawable.index_full : R.drawable.index;
    }

    @Bindable
    public int getToolsHomeTextColor() {
        return getMainMenuBeanTextColor(index == 0);
    }

    @Bindable
    public int getToolsCommunityImageResId() {
        return index == 1 ? R.drawable.community_full : R.drawable.community;
    }

    @Bindable
    public int getToolsCommunityTextColor() {
        return getMainMenuBeanTextColor(index == 1);
    }

    @Bindable
    public int getToolsSubscribeImageResId() {
        return index == 2 ? R.drawable.subscription_full : R.drawable.subscription;
    }

    @Bindable
    public int getToolsSubscribeTextColor() {
        return getMainMenuBeanTextColor(index == 2);
    }

    @Bindable
    public int getToolsMessageImageResId() {
        return index == 3 ? R.drawable.message_full : R.drawable.message;
    }

    @Bindable
    public int getToolsMessageTextColor() {
        return getMainMenuBeanTextColor(index == 3);
    }


    @Bindable
    public int getToolsMineImageResId() {
        return index == 4 ? R.drawable.me_full : R.drawable.me;
    }

    @Bindable
    public int getToolsMineTextColor() {
        return getMainMenuBeanTextColor(index == 4);
    }

    private int getMainMenuBeanTextColor(boolean select) {
        int i = select ? R.color.main_color : R.color.gray_91;
        return ResourcesUtils.getColor(i);
    }
}
