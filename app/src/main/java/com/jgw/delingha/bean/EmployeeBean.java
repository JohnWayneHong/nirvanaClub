package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class EmployeeBean implements SelectItemSupport {
    public String name;
    public String employeeId;
    @Override
    public String getShowName() {
        return name;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return employeeId;
    }
}
