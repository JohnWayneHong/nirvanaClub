package com.jgw.delingha.custom_module.delingha.select_list.slaughter_weighing;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.BreedOutListBean;
import com.jgw.delingha.bean.SlaughterInBatchListBean;
import com.jgw.delingha.bean.SlaughterInListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class SlaughterWeighingBatchListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getSlaughterWeighingList(String search, int page) {
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
                .getSlaughterInList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(SlaughterInBatchListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

}
