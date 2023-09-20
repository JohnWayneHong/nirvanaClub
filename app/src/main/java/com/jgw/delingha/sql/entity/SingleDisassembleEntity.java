package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

@Entity
public class SingleDisassembleEntity extends BaseCodeEntity {

    @Id
    private long id;

    @Unique
    private String code;

    private int codeStatus;

    private int codeLevel;

    private String codeLevelName;

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
    public String getCodeLevelName() {
        return codeLevelName;
    }

    public void setCodeLevelName(String codeLevelName) {
        this.codeLevelName = codeLevelName;
    }

    public int getCodeLevel() {
        return codeLevel;
    }

    public void setCodeLevel(int codeLevel) {
        this.codeLevel = codeLevel;
    }

    @Override
    public void setSingleNumber(int number) {
    }

    @Override
    public int getSingleNumber() {
        return 0;
    }

}
