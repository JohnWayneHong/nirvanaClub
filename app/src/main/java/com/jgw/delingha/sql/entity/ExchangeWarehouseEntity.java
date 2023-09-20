package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

@Entity
public class ExchangeWarehouseEntity extends BaseCodeEntity {
    @Id
    private long id;

    @Unique
    private String outerCode;

    private int codeStatus;

    private int singleNumber;

    private ToOne<ExchangeWarehouseConfigurationEntity> configEntity;

    public ToOne<ExchangeWarehouseConfigurationEntity> getConfigEntity() {
        return configEntity;
    }

    public void setConfigEntity(ToOne<ExchangeWarehouseConfigurationEntity> configEntity) {
        this.configEntity = configEntity;
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

    public int getSingleNumber() {
        return this.singleNumber;
    }

    public void setSingleNumber(int singleNumber) {
        this.singleNumber = singleNumber;
    }

}
