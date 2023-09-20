package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;


/**
 * author : Cxz
 * data : 2020/1/17
 * description : 码表
 */
@Entity
public class CodeEntity extends BaseCodeEntity {
    @Id
    private long id;

    @Unique
    private String code;

    private String firstNumberUnitName;

    private int firstNumberUnitCount;

    private int secondNumberUnitCount;

    private int thirdNumberUnitCount;

    private String productClassifyId;

    private String productClassifyName;

    private String productCode;

    private String productId;

    private String productName;

    private String secondNumberUnitName;

    private int codeStatus;

    private int codeLevel;

    private String codeLevelName;

    private String thirdNumberUnitName;

    private String errorMsg;

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

    @Override
    public int getCodeStatus() {
        return codeStatus;
    }

    @Override
    public void setCodeStatus(int codeStatus) {
        this.codeStatus = codeStatus;
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

    public String getFirstNumberUnitName() {
        return this.firstNumberUnitName;
    }

    public void setFirstNumberUnitName(String firstNumberUnitName) {
        this.firstNumberUnitName = firstNumberUnitName;
    }

    public int getFirstNumberUnitCount() {
        return this.firstNumberUnitCount;
    }

    public String getFirstNumberUnitCountText() {
        return firstNumberUnitCount + firstNumberUnitName;
    }

    public void setFirstNumberUnitCount(int firstNumberUnitCount) {
        this.firstNumberUnitCount = firstNumberUnitCount;
    }

    public int getSecondNumberUnitCount() {
        return this.secondNumberUnitCount;
    }

    public String getSecondNumberUnitCountText() {
        return secondNumberUnitCount + secondNumberUnitName;
    }

    public void setSecondNumberUnitCount(int secondNumberUnitCount) {
        this.secondNumberUnitCount = secondNumberUnitCount;
    }

    public int getThirdNumberUnitCount() {
        return this.thirdNumberUnitCount;
    }

    public String getThirdNumberUnitCountText() {
        return thirdNumberUnitCount + thirdNumberUnitName;
    }

    public void setThirdNumberUnitCount(int thirdNumberUnitCount) {
        this.thirdNumberUnitCount = thirdNumberUnitCount;
    }

    public String getProductClassifyId() {
        return this.productClassifyId;
    }

    public void setProductClassifyId(String productClassifyId) {
        this.productClassifyId = productClassifyId;
    }

    public String getProductClassifyName() {
        return this.productClassifyName;
    }

    public void setProductClassifyName(String productClassifyName) {
        this.productClassifyName = productClassifyName;
    }

    public String getProductCode() {
        return this.productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSecondNumberUnitName() {
        return this.secondNumberUnitName;
    }

    public void setSecondNumberUnitName(String secondNumberUnitName) {
        this.secondNumberUnitName = secondNumberUnitName;
    }

    @Override
    public int getCodeLevel() {
        return codeLevel;
    }

    @Override
    public void setCodeLevel(int codeLevel) {
        this.codeLevel = codeLevel;
    }

    @Override
    public String getCodeLevelName() {
        return codeLevelName;
    }

    public void setCodeLevelName(String codeLevelName) {
        this.codeLevelName = codeLevelName;
    }

    public String getThirdNumberUnitName() {
        return this.thirdNumberUnitName;
    }

    public void setThirdNumberUnitName(String thirdNumberUnitName) {
        this.thirdNumberUnitName = thirdNumberUnitName;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
