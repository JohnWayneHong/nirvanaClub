package com.jgw.delingha.bean;

import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_RELATE_TO_NFC;

import android.view.View;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;

public class TaskBean {


    public String createTime;
    public String cusOrgId;
    public String houseList;        //单据号
    public String operationId;
    public String operationName;
    public String operatorName;
    public String organizationId;
    public int status;              // 任务状态 1.处理中 2.成功 3.部分成功 4.任务失败
    public String statusDesc;       // 状态描述
    public int taskType;        // 任务类型 7.出库 6.入库 3.退货 2.调货 1.调仓
    public String taskId;
    public String taskTypeDesc;     // 任务类型描述
    public int totalCodes;          //上传数量
    public int firstNumber;
    public String firstUnitName;
    public int secondNumber;
    public String secondUnitName;
    public int thirdNumber;
    public String thirdUnitName;

    public int getStatusTextColor() {
        switch (status) {
            case 1:
                return ResourcesUtils.getColor(R.color.dealing_color);
            case 2:
                return ResourcesUtils.getColor(R.color.success_color);
            case 3:
                return ResourcesUtils.getColor(R.color.error_color);
            default:
                return ResourcesUtils.getColor(R.color.dealing_color);
        }
    }

    public int getFailButtonVisibility() {
        if (status == 3) {
            return View.VISIBLE;
        }
        return View.GONE;
    }

    public int getTryAgainButtonVisibility() {
        if (status == 3 || status == 4) {
            return View.VISIBLE;
        }
        return View.GONE;
    }

    public String getTotalCount() {
        return totalCodes + "";
    }

    public String getSuccessCount() {
        switch (taskType) {
            case TaskListViewModel.TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX:
            case TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT:
            case TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT:
            case TaskListViewModel.TYPE_TASK_LABEL_EDIT:
                return firstNumber + secondNumber + thirdNumber + "";
            default:
                return thirdNumber + thirdUnitName + "、" + secondNumber + secondUnitName + "、" + firstNumber + firstUnitName;
        }
    }

    public int getSuccessCountVisibility() {
        return taskType == TYPE_TASK_RELATE_TO_NFC ? View.GONE : View.VISIBLE;
    }

}
