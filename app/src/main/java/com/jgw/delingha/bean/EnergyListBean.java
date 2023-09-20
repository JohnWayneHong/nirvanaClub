package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class EnergyListBean implements SelectItemSupport {


    private String createTime;
    private int disableDel;
    private String energyEndTime;
    private String energyId;
    private String energyStartTime;
    private String energyUsed;
    private String energyUsedFee;
    private int id;
    private String operator;
    private String operatorName;
    private String organizationId;
    private String organizationName;
    private String otherFee;
    private String rankId;
    private String rankName;
    private String waterUsed;
    private String waterUsedFee;

    public String getEnergyPeriodOfTimeText(){
        return getEnergyStartTime()+" 到 "+getEnergyEndTime();
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
        return getEnergyId();
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

    public String getEnergyEndTime() {
        return energyEndTime;
    }

    public void setEnergyEndTime(String energyEndTime) {
        this.energyEndTime = energyEndTime;
    }

    public String getEnergyId() {
        return energyId;
    }

    public void setEnergyId(String energyId) {
        this.energyId = energyId;
    }

    public String getEnergyStartTime() {
        return energyStartTime;
    }

    public void setEnergyStartTime(String energyStartTime) {
        this.energyStartTime = energyStartTime;
    }

    public String getEnergyUsed() {
        return energyUsed;
    }
    public String getEnergyUsedText() {
        return getEnergyUsed()+"度";
    }

    public void setEnergyUsed(String energyUsed) {
        this.energyUsed = energyUsed;
    }

    public String getEnergyUsedFeeText() {
        return getEnergyUsedFee()+"元";
    }
    public String getEnergyUsedFee() {
        return energyUsedFee;
    }

    public void setEnergyUsedFee(String energyUsedFee) {
        this.energyUsedFee = energyUsedFee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getOtherFeeText() {
        return getOtherFee()+"元";
    }
    public String getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(String otherFee) {
        this.otherFee = otherFee;
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

    public String getWaterUsedText() {
        return getWaterUsed()+"m³";
    }

    public String getWaterUsed() {
        return waterUsed;
    }

    public void setWaterUsed(String waterUsed) {
        this.waterUsed = waterUsed;
    }

    public String getWaterUsedFeeText() {
        return getWaterUsedFee()+"元";
    }

    public String getWaterUsedFee() {
        return waterUsedFee;
    }

    public void setWaterUsedFee(String waterUsedFee) {
        this.waterUsedFee = waterUsedFee;
    }
}
