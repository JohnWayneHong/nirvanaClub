package com.jgw.delingha.bean;


import android.view.View;

import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.module.select_list.scan_rule.ScanRuleSelectListActivity;
import com.jgw.delingha.utils.ConstantUtil;

public class ScanRuleBean {
    private String des;
    private int type;
    private boolean selected;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        setDes(getScanRuleDesByType(type));
        setSelected(type == MMKVUtils.getInt(ConstantUtil.SCAN_RULE));
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSelectImageVisible() {
        return selected ? View.VISIBLE : View.GONE;
    }

    public static String getScanRuleDesByType(int type) {
        switch (type) {
            case ScanRuleSelectListActivity.LESS_OR_EQUAL:
                return "(小于等于)扫码数量必须小于等于计划数量";
            case ScanRuleSelectListActivity.EQUAL:
                return "(等于)扫码数量必须等于计划数量";
            case ScanRuleSelectListActivity.GREATER_OR_EQUAL:
                return "(大于等于)扫码数量必须大于等于计划数量";
            default:
                return "异常";
        }
    }
}
