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
public class ExchangeGoodsConfigurationEntity extends BaseEntity implements ConfigEntity{
    @Id
    private long id;

    private long createTime;

    private String wareGoodsInCode;//调入仓库

    private String wareGoodsInId;

    private String wareGoodsInName;

    private String wareGoodsOutCode;//调出仓库

    private String wareGoodsOutId;

    private String wareGoodsOutName;

    private String taskId;

    @Transient
    private long indexId;

    private ToOne<UserEntity> userEntity;

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
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

    public String getDisplayOutGoods() {
        if (TextUtils.isEmpty(wareGoodsOutCode) && TextUtils.isEmpty(wareGoodsOutName)) {
            return null;
        }
        return "(" + wareGoodsOutCode + ")" + wareGoodsOutName;
    }

    public String getDisplayInGoods() {
        if (TextUtils.isEmpty(wareGoodsInCode) && TextUtils.isEmpty(wareGoodsInName)) {
            return null;
        }
        return "(" + wareGoodsInCode + ")" + wareGoodsInName;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWareGoodsInCode() {
        return this.wareGoodsInCode;
    }

    public void setWareGoodsInCode(String wareGoodsInCode) {
        this.wareGoodsInCode = wareGoodsInCode;
    }

    public String getWareGoodsInId() {
        return this.wareGoodsInId;
    }

    public void setWareGoodsInId(String wareGoodsInId) {
        this.wareGoodsInId = wareGoodsInId;
    }

    public String getWareGoodsInName() {
        return this.wareGoodsInName;
    }

    public void setWareGoodsInName(String wareGoodsInName) {
        this.wareGoodsInName = wareGoodsInName;
    }

    public String getWareGoodsOutCode() {
        return this.wareGoodsOutCode;
    }

    public void setWareGoodsOutCode(String wareGoodsOutCode) {
        this.wareGoodsOutCode = wareGoodsOutCode;
    }

    public String getWareGoodsOutId() {
        return this.wareGoodsOutId;
    }

    public void setWareGoodsOutId(String wareGoodsOutId) {
        this.wareGoodsOutId = wareGoodsOutId;
    }

    public String getWareGoodsOutName() {
        return this.wareGoodsOutName;
    }

    public void setWareGoodsOutName(String wareGoodsOutName) {
        this.wareGoodsOutName = wareGoodsOutName;
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

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
