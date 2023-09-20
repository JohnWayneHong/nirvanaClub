package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

/**
 * 全部拆解(打散套标,其中任何一个节点调用,全组分离)
 */
@Entity
public class DisassembleAllEntity extends BaseCodeEntity {

    @Id
    private long id;

    @Unique
    private String code;

    private int codeStatus;

    private ToOne<UserEntity> userEntity;

    public ToOne<UserEntity> getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(ToOne<UserEntity> userEntity) {
        this.userEntity = userEntity;
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
}
