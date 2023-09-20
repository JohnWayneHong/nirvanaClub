package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

/**
 * @author : hwj
 * @date : 2023/7/31
 * description :
 */
public class SlaughterWeighingListBean implements SelectItemSupport {

    private String batchName;
    private String butcherCount;
    private String createTime;
    private int deleted;
    private String id;
    private String operatorName;
    private String totalWeight;

    public int getDeleteVisible() {
        return deleted == 0 ? View.VISIBLE : View.GONE;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getButcherCount() {
        return butcherCount;
    }

    public void setButcherCount(String butcherCount) {
        this.butcherCount = butcherCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTotalWeight() {
        return totalWeight ;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getTotalWeightInText() {
        return getTotalWeight() + "kg";
    }

    @Override
    public String getShowName() {
        return batchName;
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return id;
    }

}
