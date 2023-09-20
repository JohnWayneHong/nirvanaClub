package com.jgw.delingha.bean;

import java.util.List;

/**
 * @author : J-T
 * @date : 2022/7/22 9:39
 * description : 码状态查询Base Bean
 */
public abstract class BaseCodeStatusQueryBean {
    /**
     * 获取信息列表
     */
    public abstract List<CodeStatusQueryInfoItemBean> getInfoList();
}
