package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class WeightObjectBean implements SelectItemSupport {
    public String weightObjectName;
    public String weightObjectId;
    @Override
    public String getShowName() {
        return weightObjectName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return weightObjectId;
    }
}
