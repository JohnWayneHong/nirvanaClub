package com.jgw.delingha.module.select_list.common;

import java.io.Serializable;
public interface SelectItemSupport extends Serializable {

    String getShowName();

    long getItemId();

    default String getStringItemId() {
        return "";
    }
    default String getExtraData() {
        return "";
    }
    default String getExtraDataText() {
        return "";
    }
}
