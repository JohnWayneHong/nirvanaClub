package com.jgw.delingha.bean;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

/**
 * @author : hwj
 * @date : 2023/7/31
 * description :
 */
public class SlaughterWeighingDetailsListBean implements SelectItemSupport {
    private String batchName;
    private String createTime;
    private String id;
    private String operatorName;
    private String butcherCount;
    private String totalWeight;
    private List<String> weights;

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getButcherCount() {
        return butcherCount;
    }

    public void setButcherCount(String butcherCount) {
        this.butcherCount = butcherCount;
    }


    public List<String> getWeights() {
        return weights;
    }

    public void setWeights(List<String> weights) {
        this.weights = weights;
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
        return id+"";
    }

}
