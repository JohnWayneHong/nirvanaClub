package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

/**
 * 养殖离场记录对象
 */
public class BreedOutListBean implements SelectItemSupport {

    public String breedOutBatch;
    public String leftCount;
    public String breedOutRecId;
    public String breedInBatch;
    public String outTypeValue;
    public String outCount;
    public String outWeight;
    public String fenceName;
    public String outPrice;
    public String creatorName;
    public String createTime;

    public String outDate;
    public String outType;

    public int isEdit;

    public int getDeleteVisible() {
        return isEdit == 1 ? View.VISIBLE : View.GONE;
    }

    @Override
    public String getShowName() {
        return breedOutBatch;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return breedOutRecId;
    }

    @Override
    public String getExtraData() {
        return leftCount;
    }
    @Override
    public String getExtraDataText() {
        return "离场"+ getExtraData()+"只/头";
    }

    public String getBreedOutBatch() {
        return breedOutBatch;
    }

    public void setBreedOutBatch(String breedOutBatch) {
        this.breedOutBatch = breedOutBatch;
    }

    public String getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(String leftCount) {
        this.leftCount = leftCount;
    }

    public String getBreedOutRecId() {
        return breedOutRecId;
    }

    public void setBreedOutRecId(String breedOutRecId) {
        this.breedOutRecId = breedOutRecId;
    }

    public String getBreedInBatch() {
        return breedInBatch;
    }

    public void setBreedInBatch(String breedInBatch) {
        this.breedInBatch = breedInBatch;
    }

    public String getOutTypeValue() {
        return outTypeValue;
    }

    public void setOutTypeValue(String outTypeValue) {
        this.outTypeValue = outTypeValue;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getOutCount() {
        return outCount;
    }

    public void setOutCount(String outCount) {
        this.outCount = outCount;
    }

    public String getOutWeight() {
        return outWeight;
    }

    public String getOutWeightText() {
        return outWeight+"kg";
    }

    public void setOutWeight(String outWeight) {
        this.outWeight = outWeight;
    }

    public String getOutPrice() {
        return outPrice;
    }

    public String getOutPriceText() {
        if (TextUtils.isEmpty(getOutPrice())){
            return "";
        }
        return getOutPrice()+"元/kg";
    }

    public void setOutPrice(String outPrice) {
        this.outPrice = outPrice;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }

    public int getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(int isEdit) {
        this.isEdit = isEdit;
    }
}
