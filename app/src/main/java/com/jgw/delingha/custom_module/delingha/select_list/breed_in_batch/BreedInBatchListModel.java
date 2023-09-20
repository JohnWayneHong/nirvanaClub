package com.jgw.delingha.custom_module.delingha.select_list.breed_in_batch;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class BreedInBatchListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedInList(String search, int page) {
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
                .getBreedInBatchList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions

                    List<BreedInListBean> list = JsonUtils.parseObject(s).getJsonArray("list").toJavaList(BreedInListBean.class);
                    return list;
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedInByFenceList(String search, int page, String selectByPigstyId) {
        if (mOrderStockOutListLiveData == null) {
            mOrderStockOutListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        map.put("fenceId", selectByPigstyId);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBreedInBatchByFenceList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions

                    List<BreedInListBean> list = JsonUtils.parseObject(s).getJsonArray("list").toJavaList(BreedInListBean.class);
                    return list;
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

}
