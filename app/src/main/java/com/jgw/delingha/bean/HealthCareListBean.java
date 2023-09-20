package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class HealthCareListBean implements SelectItemSupport {

    private String createTime;
    private String fenceId;
    private String fenceName;
    private String healthCareId;
    private String healthCareNumber;
    private String healthCareTime;
    private String operator;
    private String operatorName;
    private String reasonId;
    private String reasonValue;
    private String remarks;
    private String animalsId;
    private String animalsValue;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFenceId() {
        return fenceId;
    }

    public void setFenceId(String fenceId) {
        this.fenceId = fenceId;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getHealthCareId() {
        return healthCareId;
    }

    public void setHealthCareId(String healthCareId) {
        this.healthCareId = healthCareId;
    }

    public String getHealthCareNumber() {
        return healthCareNumber;
    }

    public void setHealthCareNumber(String healthCareNumber) {
        this.healthCareNumber = healthCareNumber;
    }

    public String getHealthCareTime() {
        return healthCareTime;
    }

    public void setHealthCareTime(String healthCareTime) {
        this.healthCareTime = healthCareTime;
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

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public String getReasonValue() {
        return reasonValue;
    }

    public void setReasonValue(String reasonValue) {
        this.reasonValue = reasonValue;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public int getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(int isEdit) {
        this.isEdit = isEdit;
    }

    public int isEdit;

    public int getDeleteVisible() {
        return isEdit == 1 ? View.VISIBLE : View.GONE;
    }

    @Override
    public String getShowName() {
        return getFenceName();
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return getHealthCareId();
    }

}
