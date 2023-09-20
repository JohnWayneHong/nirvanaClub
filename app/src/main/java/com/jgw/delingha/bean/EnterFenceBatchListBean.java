package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class EnterFenceBatchListBean implements SelectItemSupport {

    private String breedInRecId;
    private int inCount;
    private String inType;
    private String inTypeValue;
    private String inDate;
    private int inWeight;
    private String createrName;
    private String createTime;
    private String breedInBatch;
    private String attendeesCount;
    private String pendingEntriesCount;
    private int isEdit;
    private String acquirerName;
    private String recipientName;

    public String getBreedInRecId() {
        return breedInRecId;
    }

    public void setBreedInRecId(String breedInRecId) {
        this.breedInRecId = breedInRecId;
    }

    public int getInCount() {
        return inCount;
    }

    public void setInCount(int inCount) {
        this.inCount = inCount;
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
    }

    public String getInTypeValue() {
        return inTypeValue;
    }

    public void setInTypeValue(String inTypeValue) {
        this.inTypeValue = inTypeValue;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public int getInWeight() {
        return inWeight;
    }

    public void setInWeight(int inWeight) {
        this.inWeight = inWeight;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBreedInBatch() {
        return breedInBatch;
    }

    public void setBreedInBatch(String breedInBatch) {
        this.breedInBatch = breedInBatch;
    }

    public String getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(String attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public String getPendingEntriesCount() {
        return pendingEntriesCount;
    }

    public void setPendingEntriesCount(String pendingEntriesCount) {
        this.pendingEntriesCount = pendingEntriesCount;
    }

    public String getAcquirerName() {
        return acquirerName;
    }

    public void setAcquirerName(String acquirerName) {
        this.acquirerName = acquirerName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public int getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(int isEdit) {
        this.isEdit = isEdit;
    }

    public int getDeleteVisible() {
        return isEdit == 1 ? View.VISIBLE : View.GONE;
    }

    @Override
    public String getShowName() {
        return breedInBatch;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return breedInRecId;
    }

    @Override
    public String getExtraData() {
        return pendingEntriesCount;
    }
    @Override
    public String getExtraDataText() {
        return "待入栏"+ getExtraData()+"只/头";
    }
}
