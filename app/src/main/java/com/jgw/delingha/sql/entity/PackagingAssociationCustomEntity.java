package com.jgw.delingha.sql.entity;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;

@Entity
public class PackagingAssociationCustomEntity extends BasePackageCodeEntity {
    @Id
    private long id;

    @Unique
    private String outerCode; // 码

    private String parentOuterCodeId; // 父码

    private String parentOuterCodeTypeId; // 父码码制（可以删除）

    private Boolean isFull; // 是否满箱

    private Boolean isBoxCode; // 是否为父码

    private String codeLevelName; // 单位

    private int codeStatus;//码的验证状态

    private ToOne<PackageConfigEntity> configEntity;       // 新增的

    @Transient
    private List<PackagingAssociationCustomEntity> sonList;

    public Boolean getBoxCode() {
        return isBoxCode;
    }

    public void setBoxCode(Boolean boxCode) {
        isBoxCode = boxCode;
    }

    public ToOne<PackageConfigEntity> getConfigEntity() {
        return configEntity;
    }

    public void setConfigEntity(ToOne<PackageConfigEntity> configEntity) {
        this.configEntity = configEntity;
    }

    public void setSonList(List<PackagingAssociationCustomEntity> sonList) {
        this.sonList = sonList;
    }

    public List<PackagingAssociationCustomEntity> getSonList() {
        return sonList;
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

    public String getParentOuterCodeId() {
        return this.parentOuterCodeId;
    }

    public void setParentOuterCodeId(String parentOuterCodeId) {
        this.parentOuterCodeId = parentOuterCodeId;
    }

    public String getParentOuterCodeTypeId() {
        return this.parentOuterCodeTypeId;
    }

    public void setParentOuterCodeTypeId(String parentOuterCodeTypeId) {
        this.parentOuterCodeTypeId = parentOuterCodeTypeId;
    }

    public Boolean getIsFull() {
        return this.isFull;
    }

    public void setIsFull(Boolean isFull) {
        this.isFull = isFull;
    }

    public Boolean getIsBoxCode() {
        return this.isBoxCode;
    }

    public void setIsBoxCode(Boolean isBoxCode) {
        this.isBoxCode = isBoxCode;
    }

    public String getCodeLevelName() {
        return this.codeLevelName;
    }

    public void setCodeLevelName(String unit) {
        this.codeLevelName = unit;
    }

    @Override
    public String getParentCode() {
        return parentOuterCodeId;
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
    public String getCode() {
        return getOuterCode();
    }

    @Override
    public void setCode(String code) {
        setOuterCode(code);
    }
}
