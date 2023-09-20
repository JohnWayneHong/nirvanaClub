package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

/**
 * @author : J-T
 * @date : 2022/4/12 14:37
 * description :退货表 不校验库存版本 （目前冈本用）
 */
@Entity
public class StockReturnNoVerificationEntity extends BaseCodeEntity {
    @Id
    private long id;

    @Unique
    private String code;

    private ToOne<ConfigurationEntity> configEntity;

    private int codeStatus;

    public void setId(long id) {
        this.id = id;
    }

    public ToOne<ConfigurationEntity> getConfigEntity() {
        return configEntity;
    }

    public void setConfigEntity(ToOne<ConfigurationEntity> configEntity) {
        this.configEntity = configEntity;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCodeStatus() {
        return codeStatus;
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

}
