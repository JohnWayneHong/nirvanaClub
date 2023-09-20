package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import com.jgw.common_library.utils.FormatUtils;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

/**
 * author : Cxz
 * data : 2020/1/17
 * description : 调仓配置表
 */
@Entity
public class ExchangeWarehouseConfigurationEntity extends BaseEntity implements ConfigEntity{
    @Id
    private long id;

    private long createTime;

    private String inWareHouseId;//调入仓库

    private String inWareHouseName;

    private String inWareHouseCode;

    private String outWareHouseId;//调出仓库

    private String outWareHouseName;

    private String outWareHouseCode;

    private String inStoreHouseId;//存放库位

    private String inStoreHouseName;

    private String taskId;

    private ToOne<UserEntity> userEntity;

    @Transient
    private long indexId;

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getIndexId() {
        return indexId;
    }

    public void setIndexId(long indexId) {
        this.indexId = indexId;
    }

    public ToOne<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(ToOne<UserEntity> userEntity) {
        this.userEntity = userEntity;
    }

    public String getDisplayInWarehouse() {
        if (TextUtils.isEmpty(inWareHouseName) && TextUtils.isEmpty(inWareHouseCode)) {
            return null;
        }
        return "(" + inWareHouseCode + ")" + inWareHouseName;
    }

    public String getDisplayOutWarehouse() {
        if (TextUtils.isEmpty(outWareHouseCode) && TextUtils.isEmpty(outWareHouseName)) {
            return null;
        }
        return "(" + outWareHouseCode + ")" + outWareHouseName;
    }

    public String getDisplayStoreHouse() {
        return inStoreHouseName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInWareHouseId() {
        return this.inWareHouseId;
    }

    public void setInWareHouseId(String inWareHouseId) {
        this.inWareHouseId = inWareHouseId;
    }

    public String getInWareHouseName() {
        return this.inWareHouseName;
    }

    public void setInWareHouseName(String inWareHouseName) {
        this.inWareHouseName = inWareHouseName;
    }

    public String getInWareHouseCode() {
        return this.inWareHouseCode;
    }

    public void setInWareHouseCode(String inWareHouseCode) {
        this.inWareHouseCode = inWareHouseCode;
    }

    public String getOutWareHouseId() {
        return this.outWareHouseId;
    }

    public void setOutWareHouseId(String outWareHouseId) {
        this.outWareHouseId = outWareHouseId;
    }

    public String getOutWareHouseName() {
        return this.outWareHouseName;
    }

    public void setOutWareHouseName(String outWareHouseName) {
        this.outWareHouseName = outWareHouseName;
    }

    public String getOutWareHouseCode() {
        return this.outWareHouseCode;
    }

    public void setOutWareHouseCode(String outWareHouseCode) {
        this.outWareHouseCode = outWareHouseCode;
    }

    public String getInStoreHouseId() {
        return this.inStoreHouseId;
    }

    public void setInStoreHouseId(String inStoreHouseId) {
        this.inStoreHouseId = inStoreHouseId;
    }

    public String getInStoreHouseName() {
        return this.inStoreHouseName;
    }

    public void setInStoreHouseName(String inStoreHouseName) {
        this.inStoreHouseName = inStoreHouseName;
    }

    public String getDataTime() {
        if (createTime == 0) {
            return "无添加时间";
        }
        return FormatUtils.formatTime(createTime);
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getTaskId() {
        return this.taskId;
    }


}
