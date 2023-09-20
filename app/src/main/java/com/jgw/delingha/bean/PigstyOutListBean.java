package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class PigstyOutListBean implements SelectItemSupport {

    private String id;
    private String weightTotal;
    private String createTime;
    private String updateTime;
    private String countTotal;
    private String breedInBatch;
    private String fenceName;
    private String creatorId;
    private String creatorName;
    private String organizationId;
    private String organizationName;
    private String fenceId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWeightTotal() {
        return weightTotal;
    }

    public void setWeightTotal(String weightTotal) {
        this.weightTotal = weightTotal;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(String countTotal) {
        this.countTotal = countTotal;
    }

    public String getBreedInBatch() {
        return breedInBatch;
    }

    public void setBreedInBatch(String breedInBatch) {
        this.breedInBatch = breedInBatch;
    }

    public String getFenceName() {
        return fenceName;
    }

    public void setFenceName(String fenceName) {
        this.fenceName = fenceName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
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

    public String getFenceId() {
        return fenceId;
    }

    public void setFenceId(String fenceId) {
        this.fenceId = fenceId;
    }

    @Override
    public String getShowName() {
        return getFenceName();
    }

    @Override
    public String getStringItemId() {
        return getFenceId();
    }

    @Override
    public long getItemId() {
        return 0;
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
