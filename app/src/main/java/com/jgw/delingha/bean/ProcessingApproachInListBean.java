package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class ProcessingApproachInListBean implements SelectItemSupport {

    public String processingBatch;//加工批次
    public String slaughterBatch;//屠宰进场批次
    public String processingId;
    public String inCount;
    public String inWeight;
    public String inTime;
    public String operatorName;
    public String createTime;

    public String attendeesCount;

    public int disableEdit;

    public int getDeleteVisible() {
        return disableEdit == 1 ? View.VISIBLE : View.GONE;
    }

    @Override
    public String getShowName() {
        return processingBatch;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return processingId;
    }

    @Override
    public String getExtraData() {
        return attendeesCount;
    }
    @Override
    public String getExtraDataText() {
        return "在场"+ getExtraData()+"只/头";
    }

    public String getProcessingBatch() {
        return processingBatch;
    }

    public void setProcessingBatch(String processingBatch) {
        this.processingBatch = processingBatch;
    }

    public String getSlaughterBatch() {
        return slaughterBatch;
    }

    public void setSlaughterBatch(String slaughterBatch) {
        this.slaughterBatch = slaughterBatch;
    }

    public String getProcessingId() {
        return processingId;
    }

    public void setProcessingId(String processingId) {
        this.processingId = processingId;
    }

    public String getInCount() {
        return inCount;
    }

    public void setInCount(String inCount) {
        this.inCount = inCount;
    }

    public String getInWeight() {
        return inWeight;
    }

    public String getInWeightText() {
        return getInWeight()+"kg";
    }

    public void setInWeight(String inWeight) {
        this.inWeight = inWeight;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(String attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public int getDisableEdit() {
        return disableEdit;
    }

    public void setDisableEdit(int disableEdit) {
        this.disableEdit = disableEdit;
    }
}
