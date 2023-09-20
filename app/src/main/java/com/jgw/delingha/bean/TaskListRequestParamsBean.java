package com.jgw.delingha.bean;

/**
 * TimeParamsRequestTaskList类临时储存变量进行任务列表的网络获取
 */
public class TaskListRequestParamsBean {
    public String startTime;
    public String endTime;
    public int mTaskType;
    public int mCurrentPage;

    public TaskListRequestParamsBean(int mTaskType, int mCurrentPage, String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.mCurrentPage = mCurrentPage;
        this.mTaskType = mTaskType;
    }
}
