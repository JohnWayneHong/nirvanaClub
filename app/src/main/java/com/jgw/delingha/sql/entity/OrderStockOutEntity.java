package com.jgw.delingha.sql.entity;

import com.jgw.common_library.utils.FormatUtils;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;


/**
 * author : xsw
 * data : 2021/3/15
 * description : 入库订单信息
 */
@Entity
public class OrderStockOutEntity extends BaseOrderEntity {

    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    @Unique
    private String orderCode;//订单号

    private String orderId;//订单Id

    private String createTime;//订单时间从服务端获取字符串不做转化
    private String updateTime;//订单时间从服务端获取字符串不做转化

    private long operatingTime;//本地操作时间

    private String warehouse;

    private String receiveOrganizationName;//收货方

    private String receiveOrganizationCode;//收货方编码

    private int version;//订单版本号 用来比对订单信息是否有变化

    private int orderStatus;//订单状态  0未扫码 1部分扫码 2待上传

    private ToOne<UserEntity> userEntity;

    private String taskId;//订单关联的任务ID 手输和扫码的信息全部存储在任务当中,执行时实际是执行订单关联的任务

    @Backlink(to = "orderStockOutEntity")
    private ToMany<OrderStockOutProductInfoEntity> productList;

    @Transient
    private boolean isInvalid;

    public ToOne<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(ToOne<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }

    public ToMany<OrderStockOutProductInfoEntity> getProductList() {
        return productList;
    }

    public void setProductList(ToMany<OrderStockOutProductInfoEntity> productList) {
        this.productList = productList;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return this.orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setIsInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    public boolean getIsInvalid() {
        return isInvalid;
    }

    @Override
    public int getOrderVersion() {
        return version;
    }

    @Override
    public int getOrderStatus() {
        return orderStatus;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReceiveOrganizationName() {
        return this.receiveOrganizationName;
    }

    public void setReceiveOrganizationName(String receiveOrganizationName) {
        this.receiveOrganizationName = receiveOrganizationName;
    }

    public String getReceiveOrganizationCode() {
        return this.receiveOrganizationCode;
    }

    public void setReceiveOrganizationCode(String receiveOrganizationCode) {
        this.receiveOrganizationCode = receiveOrganizationCode;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public long getOperatingTime() {
        return operatingTime;
    }
    public String getOperatingTimeText() {
        return FormatUtils.formatTime(getOperatingTime());
    }

    public void setOperatingTime(long operatingTime) {
        this.operatingTime = operatingTime;
    }

}
