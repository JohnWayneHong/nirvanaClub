package com.jgw.delingha.sql.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToOne;


/**
 * author : xsw
 * data : 2021/3/15
 * description : 入库订单产品信息
 */
@Entity
public class OrderStockOutScanCodeEntity extends BaseOrderScanCodeEntity {

    @Id//注意：long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    @Unique
    private String outerCode;//码

    private int singleNumber;//单码数量

    private int codeLevel;//码层级  1单码 2盒码 3箱码

    private String codeLevelName;//码层级  名称

    private ToOne<OrderStockOutProductInfoEntity> orderStockOutProductInfoEntity;

    private long createTime;

    private int codeStatus;

    public ToOne<OrderStockOutProductInfoEntity> getOrderStockOutProductInfoEntity() {
        return orderStockOutProductInfoEntity;
    }

    public void setOrderStockOutProductInfoEntity(ToOne<OrderStockOutProductInfoEntity> orderStockOutProductInfoEntity) {
        this.orderStockOutProductInfoEntity = orderStockOutProductInfoEntity;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getOuterCode() {
        return this.outerCode;
    }

    public void setOuterCode(String outerCode) {
        this.outerCode = outerCode;
    }

    public int getSingleNumber() {
        return this.singleNumber;
    }

    public void setSingleNumber(int singleNumber) {
        this.singleNumber = singleNumber;
    }

    public int getCodeLevel() {
        return this.codeLevel;
    }

    public void setCodeLevel(int codeLevel) {
        this.codeLevel = codeLevel;
    }

    @Override
    public String getCode() {
        return outerCode;
    }

    @Override
    public void setCode(String code) {
        setOuterCode(code);
    }

    @Override
    public String getCodeLevelName() {
        return codeLevelName;
    }

    @Override
    public int getCodeStatus() {
        return codeStatus;
    }

    public void setCodeLevelName(String codeLevelName) {
        this.codeLevelName = codeLevelName;
    }

    public void setCodeStatus(int codeStatus) {
        this.codeStatus = codeStatus;
    }
}
