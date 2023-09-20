package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

/**
 * 入栏记录对象
 */
public class EnterFenceListBean implements SelectItemSupport {

    public String breedInBatch;
    public String breedInRecId;
    public String creatorName;
    public String createTime;
    public String fenceId;
    public String fenceName;
    public String inFenceCount;
    public String inFenceId;
    public String inFenceWeight;
    public int isEdit;
    public String organizationName;


    public int getDeleteVisible() {
        return isEdit == 1 ? View.VISIBLE : View.GONE;
    }

    @Override
    public String getShowName() {
        return fenceName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return fenceId;
    }

    @Override
    public String getExtraData() {
        return inFenceCount;
    }
    @Override
    public String getExtraDataText() {
        return "待入栏"+ getExtraData()+"只/头";
    }

    public String getBreedInBatch() {
        return breedInBatch;
    }

    public void setBreedInBatch(String breedInBatch) {
        this.breedInBatch = breedInBatch;
    }

    public String getBreedInRecId() {
        return breedInRecId;
    }

    public void setBreedInRecId(String breedInRecId) {
        this.breedInRecId = breedInRecId;
    }


    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getInFenceCount() {
        return inFenceCount;
    }

    public void setInFenceCount(String inFenceCount) {
        this.inFenceCount = inFenceCount;
    }

    public String getFenceId() {
        return fenceId;
    }

    public void setFenceId(String fenceId) {
        this.fenceId = fenceId;
    }

    public String getInFenceId() {
        return inFenceId;
    }

    public void setInFenceId(String inFenceId) {
        this.inFenceId = inFenceId;
    }

    public String getInFenceWeight() {
        return inFenceWeight;
    }

    public String getInFenceWeightText() {
        return inFenceWeight+"kg";
    }

    public void setInFenceWeight(String inFenceWeight) {
        this.inFenceWeight = inFenceWeight;
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


    public int getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(int isEdit) {
        this.isEdit = isEdit;
    }
}
