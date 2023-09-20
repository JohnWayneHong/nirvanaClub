package com.jgw.delingha.custom_module.delingha.daily_management.health_care.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.HealthCareListBean;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.bean.WeightListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 保健记录 列表 Model
 */
public class HealthCareListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mHealthCareListLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteHealthCareLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(String search, int page) {
        if (mHealthCareListLiveData == null) {
            mHealthCareListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getHealthCareList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(HealthCareListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mHealthCareListLiveData) {});
        return mHealthCareListLiveData;
    }

    public LiveData<Resource<String>> deleteHealthCare(String id) {
        if (mDeleteHealthCareLiveData == null) {
            mDeleteHealthCareLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteHealthCare(id)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteHealthCareLiveData) {});
        return mDeleteHealthCareLiveData;
    }

}
