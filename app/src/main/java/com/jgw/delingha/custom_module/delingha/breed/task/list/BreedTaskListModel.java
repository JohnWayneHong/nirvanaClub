package com.jgw.delingha.custom_module.delingha.breed.task.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.BreedOutListBean;
import com.jgw.delingha.bean.BreedTaskListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023-8-8 10:03:40
 * description : 养殖任务 列表 Model
 */
public class BreedTaskListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mTaskListLiveData;
    private ValueKeeperLiveData<Resource<String>> mCompleteBreedTaskLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedTaskList(String search, int page, String taskType, int taskStatus) {
        if (mTaskListLiveData == null) {
            mTaskListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        if (!TextUtils.isEmpty(taskType)) {
            map.put("taskType",taskType);
        }
        if (taskStatus != 0) {
            map.put("status",taskStatus);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBreedTaskList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(BreedTaskListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mTaskListLiveData) {});
        return mTaskListLiveData;
    }


    public LiveData<Resource<String>> completeBreedTask(HashMap<String,Object> hashMap) {
        if (mCompleteBreedTaskLiveData == null) {
            mCompleteBreedTaskLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .doBreedTask(hashMap)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mCompleteBreedTaskLiveData) {});
        return mCompleteBreedTaskLiveData;
    }
}
