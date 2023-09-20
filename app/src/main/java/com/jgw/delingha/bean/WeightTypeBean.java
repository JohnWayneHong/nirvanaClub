package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class WeightTypeBean implements SelectItemSupport {
    public String weightTypeName;
    public String weightTypeId;
    @Override
    public String getShowName() {
        return weightTypeName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return weightTypeId;
    }
}
