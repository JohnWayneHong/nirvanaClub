package com.jgw.delingha.custom_module.delingha.select_list.pigsty_list;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CommonSelectListBean;
import com.jgw.delingha.bean.PigstyListBean;
import com.jgw.delingha.bean.PigstyOutListBean;
import com.jgw.delingha.bean.SupplierBean;
import com.jgw.delingha.custom_module.delingha.select_list.common_list.CommonSelectListActivity;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class PigstyListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(@Nullable String search, int page) {
        if (mLiveData == null) {
            mLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getPigstyList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> JsonUtils.parseObject(s).getJsonArray("list").toJavaList(PigstyListBean.class))
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mLiveData) {
                });
        return mLiveData;
    }

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOutLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getOutList(@Nullable String search, int page) {
        if (mOutLiveData == null) {
            mOutLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getPigstyOutList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> JsonUtils.parseObject(s).getJsonArray("list").toJavaList(PigstyOutListBean.class))
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOutLiveData) {
                });
        return mOutLiveData;
    }

}
