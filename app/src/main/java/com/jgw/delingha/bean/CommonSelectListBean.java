package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/10/14
 */
public class CommonSelectListBean implements SelectItemSupport {
    public String categoryId;
    public String categoryName;
    public String defaultTitleName;
    public List<CommonSelectListBean> children;
    public boolean selected;

    @Override
    public String getShowName() {
        return categoryName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return categoryId;
    }
}
