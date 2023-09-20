package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

@Entity
public class ExchangeGoodsEntity extends BaseCodeEntity {
    @Id
    private long id;

    @Unique
    private String outerCode;

    private int codeStatus;

    private ToOne<ExchangeGoodsConfigurationEntity> configEntity;

    public ToOne<ExchangeGoodsConfigurationEntity> getConfigEntity() {
        return configEntity;
    }

    public void setConfigEntity(ToOne<ExchangeGoodsConfigurationEntity> configEntity) {
        this.configEntity = configEntity;
    }

    @Override
    public ToOne<UserEntity> getUserEntity() {
        return null;
    }

    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getOuterCode() {
        return this.outerCode;
    }


    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
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
    public void setSingleNumber(int number) {

    }

    @Override
    public int getSingleNumber() {
        return 0;
    }


    @Override
    public String getCode() {
        return this.outerCode;
    }

    @Override
    public void setCode(String code) {
        setOuterCode(code);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        ExchangeGoodsEntity obj1 = (ExchangeGoodsEntity) obj;
        return TextUtils.equals(outerCode, obj1 == null ? null : obj1.outerCode);
    }

    @Override
    public int hashCode() {
        return outerCode == null ? 0 : outerCode.hashCode();
    }
}
