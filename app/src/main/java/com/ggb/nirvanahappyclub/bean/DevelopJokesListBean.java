package com.ggb.nirvanahappyclub.bean;

import com.drake.brv.item.ItemPosition;

public class DevelopJokesListBean implements ItemPosition {
    public DevelopJokesInfoBean info;
    public DevelopJokesJokeBean joke;
    public DevelopJokesUserBean user;

    @Override
    public int getItemPosition() {
        return 0;
    }

    @Override
    public void setItemPosition(int i) {

    }
}
