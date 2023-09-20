package com.jgw.delingha.custom_module.delingha.daily_management.disinfect.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.DisinfectListBean;
import com.jgw.delingha.bean.WeightListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 消毒记录 列表 Model
 */
public class DisinfectListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mDisinfectListLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteDisinfectLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(String search, int page) {
        if (mDisinfectListLiveData == null) {
            mDisinfectListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getDisinfectList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(DisinfectListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mDisinfectListLiveData) {});
        return mDisinfectListLiveData;
    }

    public LiveData<Resource<String>> deleteDisinfect(String id) {
        if (mDeleteDisinfectLiveData == null) {
            mDeleteDisinfectLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteDisinfect(id)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteDisinfectLiveData) {});
        return mDeleteDisinfectLiveData;
    }

}
