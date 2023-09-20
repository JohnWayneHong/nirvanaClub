package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class FeedingListBean implements SelectItemSupport {

    private String createTime;
    private int disableDel;
    private String feedEndTime;
    private String feedId;
    private String feedStartTime;
    private String id;
    private String operator;
    private String operatorName;
    private String organizationId;
    private String organizationName;
    private String rankId;
    private String rankName;
    private String totalFeed;
    private String totalFeedAmount;
    private String totalFeedUnitValue;

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

    public String getFeedEndTime() {
        return feedEndTime;
    }

    public void setFeedEndTime(String feedEndTime) {
        this.feedEndTime = feedEndTime;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getFeedStartTime() {
        return feedStartTime;
    }

    public void setFeedStartTime(String feedStartTime) {
        this.feedStartTime = feedStartTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTotalFeedText(){
        return getTotalFeed()+getTotalFeedUnitValue();
    }

    public String getTotalFeedAmountText(){
        return getTotalFeedAmount()+"元";
    }

    public String getFeedPeriodOfTimeText(){
        return getFeedStartTime()+" 到 "+getFeedEndTime();
    }

    public String getTotalFeed() {
        return totalFeed;
    }

    public void setTotalFeed(String totalFeed) {
        this.totalFeed = totalFeed;
    }

    public String getTotalFeedAmount() {
        return totalFeedAmount;
    }

    public void setTotalFeedAmount(String totalFeedAmount) {
        this.totalFeedAmount = totalFeedAmount;
    }

    public String getTotalFeedUnitValue() {
        return totalFeedUnitValue;
    }

    public void setTotalFeedUnitValue(String totalFeedUnitValue) {
        this.totalFeedUnitValue = totalFeedUnitValue;
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
        return getFeedId();
    }

    public int getDeleteVisible() {
        return getDisableDel() == 1 ? View.VISIBLE : View.GONE;
    }

}
