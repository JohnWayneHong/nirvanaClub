package com.jgw.delingha.module.task_list.model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.json.JsonArray;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.TaskBean;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.module.tools_table.ToolsTableUtils;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class TaskListModel {

    public LiveData<Resource<List<TaskBean>>> getTaskListNew(int taskType, String startTime, String endTime, int current) {
        final MutableLiveData<Resource<List<TaskBean>>> taskBeanMutableLiveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getTaskList(taskType, startTime, endTime, current, CustomRecyclerAdapter.ITEM_PAGE_SIZE)
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    JsonArray list = jsonObject.getJsonArray("list");
                    return list.toJavaList(TaskBean.class);
                })
                .subscribe(new CustomObserver<List<TaskBean>>() {
                    @Override
                    public void onNext(List<TaskBean> list) {
                        taskBeanMutableLiveData.setValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        taskBeanMutableLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        taskBeanMutableLiveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return taskBeanMutableLiveData;
    }

    public LinkedHashMap<String, Integer> getTaskTypeMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        String json = MMKVUtils.getString(ConstantUtil.LOCAL_MENU);
        List<ToolsOptionsBean.MobileFunsBean> localMenu = JsonUtils.parseArray(json, ToolsOptionsBean.MobileFunsBean.class);
        for (int i = 0; i < localMenu.size(); i++) {
            ToolsOptionsBean.MobileFunsBean bean = localMenu.get(i);
            switch (bean.appAuthCode) {
                case ToolsTableUtils.RuKu:
                case ToolsTableUtils.ShengChanRuKu:
                    map.put("入库", TaskListViewModel.TYPE_TASK_STOCK_IN);
                    break;
                case ToolsTableUtils.ChuKu:
                case ToolsTableUtils.ZhiJieChuKu:
                    map.put("出库", TaskListViewModel.TYPE_TASK_STOCK_OUT);
                    break;
                case ToolsTableUtils.TuiHuo:
                    map.put("退货", TaskListViewModel.TYPE_TASK_STOCK_RETURN);
                    break;
                case ToolsTableUtils.DiaoCang:
                    map.put("调仓", TaskListViewModel.TYPE_TASK_EXCHANGE_WAREHOUSE);
                    break;
                case ToolsTableUtils.DiaoHuo:
                    map.put("调货", TaskListViewModel.TYPE_TASK_EXCHANGE_GOODS);
                    break;
                case ToolsTableUtils.DanGeChaiJie:
                    map.put("单个拆解", TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT);
                    break;
                case ToolsTableUtils.ZhengZuChaiJie:
                    map.put("整组拆解", TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT);
                    break;
                case ToolsTableUtils.BuMaRuXiang:
                    map.put("补码入箱", TaskListViewModel.TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX);
                    break;
                case ToolsTableUtils.BaoZhuangGuanLian:
                    map.put("包装关联", TaskListViewModel.TYPE_TASK_PACKAGING_ASSOCIATION);
                    break;
                case ToolsTableUtils.BaoZhuangRuKu:
                    map.put("包装入库", TaskListViewModel.TYPE_TASK_PACKAGE_STOCK_IN);
                    break;
                case ToolsTableUtils.BiaoShiJiuCuo:
                    map.put("标识纠错", TaskListViewModel.TYPE_TASK_LABEL_EDIT);
                    break;
                case ToolsTableUtils.ZaiKuGuanLian:
                    map.put("在库关联", TaskListViewModel.TYPE_TASK_IN_WAREHOUSE_PACKAGE);
                    break;
                case ToolsTableUtils.GuanLianNFC:
                    map.put("关联NFC", TaskListViewModel.TYPE_TASK_RELATE_TO_NFC);
                    break;
            }
        }
        return map;

    }

    public LiveData<Resource<String>> tryAgainRunTask(int taskType, String taskId) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskType", taskType);
        map.put("taskId", taskId);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postTaskTryAgain(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }
}
