package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class SlaughterInListBean implements SelectItemSupport {

    public String inBatch;//屠宰进场批次
    public String id;//屠宰进场批次id
    public String breedLeaveBatch;//养殖 离场 批次
    public String inCount;
    public String inWeight;
    public String inTime;
    public String operatorName;
    public String createTime;
    public String certificateImageUrl;

    public String unProcessingCount;

    public int deleted;

    public String getInFactoryId() {
        return id;
    }

    public void setInFactoryId(String inFactoryId) {
        this.id = inFactoryId;
    }

    public int getDeleteVisible() {
        return deleted == 0 ? View.VISIBLE : View.GONE;
    }

    @Override
    public String getShowName() {
        return inBatch;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return id;
    }

    @Override
    public String getExtraData() {
        return unProcessingCount;
    }
    @Override
    public String getExtraDataText() {
        return "待加工"+ getExtraData()+"只/头";
    }

    public String getInBatch() {
        return inBatch;
    }

    public void setInBatch(String inBatch) {
        this.inBatch = inBatch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBreedLeaveBatch() {
        return breedLeaveBatch;
    }

    public void setBreedLeaveBatch(String breedLeaveBatch) {
        this.breedLeaveBatch = breedLeaveBatch;
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
        return inWeight+"kg";
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

    public String getCertificateImageUrl() {
        return certificateImageUrl;
    }

    public void setCertificateImageUrl(String certificateImageUrl) {
        this.certificateImageUrl = certificateImageUrl;
    }

    public int canUploadPic() {
        String[] split = getCertificateImageUrl().split(",");
        return split.length < 5 ? View.VISIBLE : View.GONE;
    }

    public String getUnProcessingCount() {
        return unProcessingCount;
    }

    public void setUnProcessingCount(String unProcessingCount) {
        this.unProcessingCount = unProcessingCount;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
