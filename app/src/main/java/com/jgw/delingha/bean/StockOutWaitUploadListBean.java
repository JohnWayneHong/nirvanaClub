package com.jgw.delingha.bean;

/**
 * Created by XiongShaoWu
 * on 2020/3/10 出库待执行列表配置字段
 */
public class StockOutWaitUploadListBean {
    public String create_time;//配置创建时间
    public String receiver; //收货客户
    public boolean selected;//是否被选中
    public long config_id;//配置id


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockOutWaitUploadListBean that = (StockOutWaitUploadListBean) o;
        return config_id == that.config_id;
    }
}
