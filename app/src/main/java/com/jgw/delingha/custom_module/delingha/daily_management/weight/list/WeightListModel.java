package com.jgw.delingha.custom_module.delingha.daily_management.weight.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.FeedingListBean;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.bean.WeightListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class WeightListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<SlaughterInDetailsBean>> mSlaughterInDetailsLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteSlaughterInLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(String search, int page) {
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
                .getWeightList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(WeightListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

//    public LiveData<Resource<SlaughterInDetailsBean>> getSlaughterInDetails(String breedInRecId) {
//        if (mSlaughterInDetailsLiveData == null) {
//            mSlaughterInDetailsLiveData = new ValueKeeperLiveData<>();
//        }
//        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
//                .getSlaughterInDetails(breedInRecId)
//                .compose(HttpUtils.applyIOSchedulers())
//                .subscribe(new CustomObserver<SlaughterInDetailsBean>(mSlaughterInDetailsLiveData) {});
//        return mSlaughterInDetailsLiveData;
//    }

    public LiveData<Resource<String>> deleteSlaughterIn(String id) {
        if (mDeleteSlaughterInLiveData == null) {
            mDeleteSlaughterInLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteWeight(id)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteSlaughterInLiveData) {});
        return mDeleteSlaughterInLiveData;
    }

}
