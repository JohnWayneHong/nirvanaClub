package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

/**
 * author : xsw
 * data : 2020/2/27
 * description : 扫码出库记录操作表(万维)
 */
@Entity
public class WanWeiStockOutEntity extends BaseCodeEntity {
    @Id
    private long id;

    @Unique
    private String code;

    private ToOne<ConfigurationEntity> configEntity;       // 新增的

    private int codeStatus;

    private int singleNumber;//单码数量

    public WanWeiStockOutEntity() {
    }

    public WanWeiStockOutEntity(long id, String code, long configId, int codeStatus, int singleNumber) {
        this.id = id;
        this.code = code;
        this.configEntity.setTargetId(configId);
        this.codeStatus = codeStatus;
        this.singleNumber = singleNumber;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null) {
            return TextUtils.equals(((WanWeiStockOutEntity) obj).code, code);
        } else {
            return false;
        }

    }

    public ToOne<ConfigurationEntity> getConfigEntity() {
        return configEntity;
    }

    public void setConfigEntity(ToOne<ConfigurationEntity> configEntity) {
        this.configEntity = configEntity;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCodeStatus() {
        return this.codeStatus;
    }

    public void setCodeStatus(int codeStatus) {
        this.codeStatus = codeStatus;
    }

    @Override
    public void setCodeLevel(int level) {

    }

    @Override
    public int getCodeLevel() {
        return 0;
    }

    @Override
    public void setCodeLevelName(String unit) {

    }

    @Override
    public String getCodeLevelName() {
        return null;
    }

    @Override
    public ToOne<UserEntity> getUserEntity() {
        return null;
    }

    public int getSingleNumber() {
        return this.singleNumber;
    }

    public void setSingleNumber(int singleNumber) {
        this.singleNumber = singleNumber;
    }

}
