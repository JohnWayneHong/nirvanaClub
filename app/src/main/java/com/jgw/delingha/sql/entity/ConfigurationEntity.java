package com.jgw.delingha.sql.entity;

import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.FormatUtils;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;


/**
 * author : Cxz
 * data : 2020/1/17
 * description : 出、入、退库等设置页面的配置表
 */
@Entity
public class ConfigurationEntity extends BaseEntity implements ConfigEntity{

    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private long createTime;

    private String wareHouseId;//仓库

    private String wareHouseName;

    private String wareHouseCode;

    private String stockHouseId;//库位

    private String stockHouseName;

    private String remark;//备注

    private ToOne<UserEntity> userEntity;
    /**
     * 入库需要字段
     **/

    private String productId;//产品


    private String productName;


    private String productCode;


    private String batchId;//批次


    private String batchName;


    private String productClassifyId;


    private String productClassifyName;

    /**
     * 出库需要字段
     **/


    private String logisticsCompanyName; //物流公司名


    private String logisticsCompanyCode;//物流公司编号


    private String contacts;//联系人姓名


    private String logisticsNumber;//物流单号

    /**
     * 退货和出库共有字段
     **/

    private String customerCode;//客户


    private String customerName;


    private String customerId;


    private String taskId;


    private Integer planNumber;

    @Transient
    private long indexId;


    public String getCustomerText() {
        if (!TextUtils.isEmpty(customerCode) && !TextUtils.isEmpty(customerName)) {
            return "(" + customerCode + ")" + customerName;
        } else if (!TextUtils.isEmpty(customerName)) {
            return customerName;
        } else {
            return null;
        }
    }

    public String getProductText() {
        if (!TextUtils.isEmpty(productCode) && !TextUtils.isEmpty(productName)) {
            return "(" + productCode + ")" + productName;
        } else if (!TextUtils.isEmpty(productName)) {
            return productName;
        } else {
            return null;
        }
    }

    public String getLogisticsCompanyText() {
        if (!TextUtils.isEmpty(logisticsCompanyCode) && !TextUtils.isEmpty(logisticsCompanyName)) {
            return "(" + logisticsCompanyCode + ")" + logisticsCompanyName;
        } else if (!TextUtils.isEmpty(logisticsCompanyName)) {
            return logisticsCompanyName;
        } else {
            return null;
        }
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWareHouseId() {
        return this.wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getWareHouseName() {
        return this.wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public String getStockHouseId() {
        return this.stockHouseId;
    }

    public void setStockHouseId(String stockHouseId) {
        this.stockHouseId = stockHouseId;
    }

    public String getStockHouseName() {
        return this.stockHouseName;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public ToOne<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(ToOne<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }

    public int getStockHouseVisible() {
        return TextUtils.isEmpty(getStockHouseName()) ? View.GONE : View.VISIBLE;
    }

    public void setStockHouseName(String stockHouseName) {
        this.stockHouseName = stockHouseName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return this.productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getProductClassifyId() {
        return this.productClassifyId;
    }

    public void setProductClassifyId(String productClassifyId) {
        this.productClassifyId = productClassifyId;
    }

    public String getProductClassifyName() {
        return this.productClassifyName;
    }

    public void setProductClassifyName(String productClassifyName) {
        this.productClassifyName = productClassifyName;
    }

    public String getLogisticsCompanyName() {
        return this.logisticsCompanyName;
    }

    public void setLogisticsCompanyName(String logisticsCompanyName) {
        this.logisticsCompanyName = logisticsCompanyName;
    }

    public String getLogisticsCompanyCode() {
        return this.logisticsCompanyCode;
    }

    public void setLogisticsCompanyCode(String logisticsCompanyCode) {
        this.logisticsCompanyCode = logisticsCompanyCode;
    }

    public String getLogisticsNumber() {
        return this.logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public String getCustomerCode() {
        return this.customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    //获取仓库显示名称 编码+名称
    public String getWareHouseContentText() {
        if (TextUtils.isEmpty(wareHouseCode) || TextUtils.isEmpty(wareHouseName)) {
            return null;
        }
        return "(" + wareHouseCode + ")" + wareHouseName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getContacts() {
        return this.contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getDataTime() {
        if (createTime == 0) {
            return "无添加时间";
        }
        return FormatUtils.formatTime(createTime);
    }

    public String getWareHouseCode() {
        return this.wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
    }

    public long getIndexId() {
        return this.indexId;
    }

    public void setIndexId(long indexId) {
        this.indexId = indexId;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPlanNumberText() {
        return getPlanNumber() == null ? null : String.valueOf(getPlanNumber());
    }

    public Integer getPlanNumber() {
        return this.planNumber;
    }

    public void setPlanNumber(int planNumber) {
        this.planNumber = planNumber;
    }

    public void setPlanNumber(Integer planNumber) {
        this.planNumber = planNumber;
    }
}
