package com.jgw.delingha.module.task_list.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.TaskBean;
import com.jgw.delingha.bean.TaskListRequestParamsBean;
import com.jgw.delingha.module.task_list.adapter.TaskListRecyclerAdapter;
import com.jgw.delingha.module.task_list.model.TaskListModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TaskListViewModel extends BaseViewModel {

    /**
     * WARNING:
     * 此处静态常量 TYPE 为获取任务列表接口参数，勿随意添加
     */
    public static final int TYPE_TASK_EXCHANGE_WAREHOUSE = 1;//调仓
    public static final int TYPE_TASK_EXCHANGE_GOODS = 2;//调货
    public static final int TYPE_TASK_STOCK_RETURN = 3;//退货
    public static final int TYPE_TASK_PACKAGING_ASSOCIATION = 4; // 包装关联
    public static final int TYPE_TASK_STOCK_IN = 6;//入库
    public static final int TYPE_TASK_STOCK_OUT = 7;//出库
    public static final int TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX = 8; // 补码入箱
    public static final int TYPE_TASK_STOCK_GROUP_SPLIT = 9;//整组拆解
    public static final int TYPE_TASK_STOCK_SINGLE_SPLIT = 10;//单个拆解
    public static final int TYPE_TASK_PACKAGE_STOCK_IN = 13; // 包装入库
    public static final int TYPE_TASK_LABEL_EDIT = 14; // 标识纠错
    public static final int TYPE_TASK_IN_WAREHOUSE_PACKAGE = 15; // 在库关联
    public static final int TYPE_TASK_DISASSEMBLE_ALL = 16; // 打散套标
    public static final int TYPE_TASK_RELATE_TO_NFC = 17; // 关联NFC

    private final TaskListModel model;
    private int mCurrentPage = 1;
    private List<TaskBean> mList;
    private Integer mTaskType;
    //任务类型名称
    private List<String> mTaskTypeDescList = new ArrayList<>();
    //任务类型名称与类型对应集合
    private LinkedHashMap<String, Integer> mTaskTypeMap = new LinkedHashMap<>();
    //当前任务列表请求参数LiveData
    private final MutableLiveData<TaskListRequestParamsBean> mTaskListRequestParamsLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<String> mTaskTypeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<TaskBean> mTaskTryAgainLiveData = new ValueKeeperLiveData<>();
    private String mStartTime;
    private String mEndTime;

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        model = new TaskListModel();
    }

    /**
     * 初始化任务名称和任务类型数据
     */
    public void initMenu() {
        if (mTaskTypeMap.isEmpty()) {
            mTaskTypeMap = model.getTaskTypeMap();
            mTaskTypeDescList.addAll(mTaskTypeMap.keySet());
        }
    }

    /**
     * 设置当前任务类型并通知UI修改
     *
     * @param taskType 当前任务类型
     */
    public void setTaskType(int taskType) {
        if (mTaskType != null && mTaskType == taskType) {
            return;
        }
        for (Map.Entry<String, Integer> entry : mTaskTypeMap.entrySet()) {
            if (entry.getValue().equals(taskType)) {
                mTaskTypeLiveData.setValue(entry.getKey());
                break;
            }
        }
        mCurrentPage = 1;
        mTaskType = taskType;
        requestData();
    }

    /**
     * 如存在已有数据就添加进新的数据集合并持有新集合引用
     *
     * @param list Adapter内置集合
     */
    public void setTaskList(@NonNull List<TaskBean> list) {
        mList = list;
    }

    /**
     * 首次请求数据
     */
    public void requestFirstData() {
        if (mList.isEmpty() && mCurrentPage == 1) {
            requestData();
        } else {
            //判断是否存在原始数据,如存在说明是界面旋转保留 不重新请求接口
        }
    }

    private void requestData() {
        TaskListRequestParamsBean paramsBean = new TaskListRequestParamsBean(mTaskType, mCurrentPage, mStartTime, mEndTime);
        mTaskListRequestParamsLiveData.setValue(paramsBean);
    }

    public LiveData<Resource<List<TaskBean>>> getTaskList() {
        return Transformations.switchMap(mTaskListRequestParamsLiveData,
                params -> model.getTaskListNew(params.mTaskType, params.startTime, params.endTime, params.mCurrentPage));
    }

    public void tryAgainRunTask(int position) {
        TaskBean data = mList.get(position);
        mTaskTryAgainLiveData.setValue(data);
    }

    public LiveData<Resource<String>> getTryAgainRunTaskLiveData() {
        return Transformations.switchMap(mTaskTryAgainLiveData, input -> model.tryAgainRunTask(input.taskType, input.taskId));
    }

    public LiveData<String> getTaskTypeLiveData() {
        return mTaskTypeLiveData;
    }

    public void onRefresh() {
        mCurrentPage = 1;
        requestData();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        requestData();
    }

    public void loadList(List<TaskBean> list, TaskListRecyclerAdapter adapter) {
        if (mCurrentPage == 1) {
            adapter.notifyRemoveListItem();
        }
        adapter.notifyAddListItem(list);
    }

    public void setStartTime(String time) {
        mCurrentPage = 1;
        mStartTime = time;
        requestData();
    }

    public void setEndTime(String time) {
        mCurrentPage = 1;
        mEndTime = time;
        requestData();
    }

    public List<String> getTaskTypeDescList() {
        return mTaskTypeDescList;
    }

    public LinkedHashMap<String, Integer> getTaskTypeMap() {
        return mTaskTypeMap;
    }

}

