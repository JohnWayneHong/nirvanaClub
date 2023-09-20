package com.jgw.delingha.custom_module.delingha.breed.enter_fence.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.EnterFenceDetailsBean;
import com.jgw.delingha.bean.EnterFenceListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023年7月6日13:57:58
 * 入栏记录列表 Model
 */
public class EnterFenceListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<EnterFenceDetailsBean>> mEnterFenceDetailsLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteEnterFenceLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getEnterFenceList(String search, int page) {
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
                .getEnterFenceList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(EnterFenceListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

    public LiveData<Resource<EnterFenceDetailsBean>> getEnterFenceDetails(String inFenceId) {
        if (mEnterFenceDetailsLiveData == null) {
            mEnterFenceDetailsLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getEnterFenceDetails(inFenceId)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<EnterFenceDetailsBean>(mEnterFenceDetailsLiveData) {});
        return mEnterFenceDetailsLiveData;
    }

    public LiveData<Resource<String>> deleteEnterFence(String inFenceId) {
        if (mDeleteEnterFenceLiveData == null) {
            mDeleteEnterFenceLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteEnterFence(inFenceId)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteEnterFenceLiveData) {});
        return mDeleteEnterFenceLiveData;
    }
}
