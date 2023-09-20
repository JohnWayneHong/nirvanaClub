package com.jgw.delingha.custom_module.delingha.processing_approach.in.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.ProcessingApproachInDetailsBean;
import com.jgw.delingha.bean.ProcessingApproachInListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class ProcessingApproachInListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<ProcessingApproachInDetailsBean>> mProcessingApproachInDetailsLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteProcessingApproachInLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getProcessingApproachInList(String search, int page) {
        if (mOrderStockOutListLiveData == null) {
            mOrderStockOutListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getProcessingApproachInList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(ProcessingApproachInListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

    public LiveData<Resource<ProcessingApproachInDetailsBean>> getProcessingApproachInDetails(String breedInRecId) {
        if (mProcessingApproachInDetailsLiveData == null) {
            mProcessingApproachInDetailsLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getProcessingApproachInDetails(breedInRecId)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<ProcessingApproachInDetailsBean>(mProcessingApproachInDetailsLiveData) {});
        return mProcessingApproachInDetailsLiveData;
    }

    public LiveData<Resource<String>> deleteProcessingApproachIn(String processingId) {
        if (mDeleteProcessingApproachInLiveData == null) {
            mDeleteProcessingApproachInLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteProcessingApproachIn(processingId)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteProcessingApproachInLiveData) {});
        return mDeleteProcessingApproachInLiveData;
    }

}
