package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class BreedTaskListBean implements SelectItemSupport {

    private String applyTarget;
    private String createTime;
    private String explains;
    private String finishTime;
    private String id;
    private String operator;
    private String operatorName;
    private String pdId;
    private String status;
    private String taskName;

    private String taskId;

    public String getApplyTarget() {
        return applyTarget;
    }

    public void setApplyTarget(String applyTarget) {
        this.applyTarget = applyTarget;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExplains() {
        return explains;
    }

    public void setExplains(String explains) {
        this.explains = explains;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
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

    public String getPdId() {
        return pdId;
    }

    public void setPdId(String pdId) {
        this.pdId = pdId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getCompleteVisible() {
        return getStatus().equals("1") ? View.VISIBLE : View.GONE;
    }

    public int getCompletePersonVisible() {
        return getStatus().equals("1") ? View.GONE : View.VISIBLE;
    }

    public String getStatusStr() {
        String statusStr = "";
        switch (getStatus()) {
            case "1":
                statusStr = "未完成";
                break;
            case "2" :
                statusStr = "已完成";
                break;
            case "3" :
                statusStr = "超期完成";
                break;
        }
        return statusStr;
    }

    @Override
    public String getShowName() {
        return getTaskName();
    }

    @Override
    public long getItemId() {
        return 0;
    }

    @Override
    public String getStringItemId() {
        return getId();
    }
}
