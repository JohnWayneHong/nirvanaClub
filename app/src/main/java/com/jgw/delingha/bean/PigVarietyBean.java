package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

/**
 * Created by XiongShaoWu
 * on 2019/10/16
 * 猪品种bean
 */
public class PigVarietyBean implements SelectItemSupport {
    public String pigVarietyId;
    public String pigVarietyName;

    @Override
    public String getShowName() {
        return pigVarietyName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return pigVarietyId;
    }
}
