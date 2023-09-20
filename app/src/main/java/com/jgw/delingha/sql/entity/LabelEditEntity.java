package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

/**
 * author : xsw
 * data : 2020/10/12
 * description : 标签纠错记录操作表
 */
@Entity
public class LabelEditEntity extends BaseCodeEntity {
    @Id
    private long id;

    @Unique
    private String code;

    private ToOne<ConfigurationEntity> configEntity;

    private int codeStatus;

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
    public void setSingleNumber(int number) {

    }

    @Override
    public int getSingleNumber() {
        return 0;
    }

    @Override
    public ToOne<UserEntity> getUserEntity() {
        return null;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(code)) {
            return super.hashCode();
        } else {
            return code.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof LabelEditEntity) {
            return TextUtils.equals(((LabelEditEntity) obj).code, code);
        } else {
            return false;
        }

    }
}
