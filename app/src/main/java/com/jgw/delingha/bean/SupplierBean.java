package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

/**
 * Created by XiongShaoWu
 * on 2019/10/17
 * 供应商数据对象
 */
public class SupplierBean implements SelectItemSupport {
    public String supperId;
    public String supperName;
;

    @Override
    public String getShowName() {
        return supperName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return supperId;
    }
}
