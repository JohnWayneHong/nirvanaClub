package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class WeightListBean implements SelectItemSupport {


    private String createTime;
    private int disableDel;
    private int id;
    private int operationType;
    private String operator;
    private String operatorName;
    private String organizationId;
    private String organizationName;
    private String rankId;
    private String rankName;
    private String weightId;
    private String animalsId;
    private String animalsValue;
    private String weightNumber;
    private String weightTotal;
    private String weightUnitValue;

    public String getTotalWeightText(){
        return getWeightTotal()+getWeightUnitValue();
    }

    @Override
    public String getShowName() {
        return getRankName();
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return getWeightId();
    }

    public int getDeleteVisible() {
        return getDisableDel() == 1 ? View.VISIBLE : View.GONE;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDisableDel() {
        return disableDel;
    }

    public void setDisableDel(int disableDel) {
        this.disableDel = disableDel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public String getOperationTypeStr() {
        return operationType == 0 ? "按栏抽查" : "整体盘点";
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRankId() {
        return rankId;
    }

    public void setRankId(String rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getWeightId() {
        return weightId;
    }

    public void setWeightId(String weightId) {
        this.weightId = weightId;
    }

    public String getWeightNumber() {
        return weightNumber;
    }

    public void setWeightNumber(String weightNumber) {
        this.weightNumber = weightNumber;
    }

    public String getWeightTotal() {
        return weightTotal;
    }

    public void setWeightTotal(String weightTotal) {
        this.weightTotal = weightTotal;
    }

    public String getWeightUnitValue() {
        return weightUnitValue;
    }

    public void setWeightUnitValue(String weightUnitValue) {
        this.weightUnitValue = weightUnitValue;
    }

    public String getAnimalsId() {
        return animalsId;
    }

    public void setAnimalsId(String animalsId) {
        this.animalsId = animalsId;
    }

    public String getAnimalsValue() {
        return animalsValue;
    }

    public void setAnimalsValue(String animalsValue) {
        this.animalsValue = animalsValue;
    }
}
