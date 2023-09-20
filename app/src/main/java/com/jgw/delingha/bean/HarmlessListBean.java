package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class HarmlessListBean implements SelectItemSupport {
    private String breedInBatch;
    private String createTime;
    private String fenceId;
    private String fenceName;
    private String innocentId;
    private String innocentNumber;
    private String innocentTime;
    private String operator;
    private String operatorName;
    private String remarks;

    private String animalsId;
    private String animalsValue;

    public String getBreedInBatch() {
        return breedInBatch;
    }

    public void setBreedInBatch(String breedInBatch) {
        this.breedInBatch = breedInBatch;
    }

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

    public String getInnocentId() {
        return innocentId;
    }

    public void setInnocentId(String innocentId) {
        this.innocentId = innocentId;
    }

    public String getInnocentNumber() {
        return innocentNumber;
    }

    public void setInnocentNumber(String innocentNumber) {
        this.innocentNumber = innocentNumber;
    }

    public String getInnocentTime() {
        return innocentTime;
    }

    public void setInnocentTime(String innocentTime) {
        this.innocentTime = innocentTime;
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
        return getInnocentId();
    }

}
