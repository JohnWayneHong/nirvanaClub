package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class BreedTaskBean implements SelectItemSupport {
    public String status;
    public String statusId;
    @Override
    public String getShowName() {
        return status;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return statusId;
    }
}
