package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class BreedInListBean implements SelectItemSupport {

    public String breedInBatch;
    public String inTypeValue;
    public String inCount;
    public String inWeight;
    //后端拼错 就这样吧
    public String createrName;
    public String createTime;

    public String attendeesCount;
    public String breedInRecId;
    public String inDate;
    public String inType;

    public String countTotal;


    public String getBreedInBatch() {
        return breedInBatch;
    }

    public void setBreedInBatch(String breedInBatch) {
        this.breedInBatch = breedInBatch;
    }

    public String getInTypeValue() {
        return inTypeValue;
    }

    public void setInTypeValue(String inTypeValue) {
        this.inTypeValue = inTypeValue;
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

    public String getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(String attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public String getBreedInRecId() {
        return breedInRecId;
    }

    public void setBreedInRecId(String breedInRecId) {
        this.breedInRecId = breedInRecId;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getInType() {
        return inType;
    }

    public void setInType(String inType) {
        this.inType = inType;
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

    public String getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(String countTotal) {
        this.countTotal = countTotal;
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
        return getCountTotal();
    }
    @Override
    public String getExtraDataText() {
        return "在栏"+ getExtraData()+"只/头";
    }
}
