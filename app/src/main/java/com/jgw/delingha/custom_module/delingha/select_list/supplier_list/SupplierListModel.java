package com.jgw.delingha.custom_module.delingha.select_list.supplier_list;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CommonSelectListBean;
import com.jgw.delingha.bean.SupplierBean;
import com.jgw.delingha.custom_module.delingha.select_list.common_list.CommonSelectListActivity;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class SupplierListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(@Nullable String searchStr, int page) {
        if (mLiveData == null) {
            mLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("pageSize","10000");
        hashMap.put("parentCategoryId",CommonSelectListActivity.SUPPLIERS);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getCommonSelectList(hashMap)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    List<CommonSelectListBean> list = JsonUtils.parseObject(s).getJsonArray("list").toJavaList(CommonSelectListBean.class);
                    return list.get(0);
                })
                .flatMap((Function<CommonSelectListBean, ObservableSource<List<? extends SelectItemSupport>>>) commonSelectListBean -> {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("sortId", commonSelectListBean.categoryId);
                    map.put("current", String.valueOf(page));
                    map.put("pageSize", String.valueOf(CustomRecyclerAdapter.ITEM_PAGE_SIZE));
                    if (!TextUtils.isEmpty(searchStr)) {
                        map.put("search", searchStr);
                    }
                    return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                            .getSupplierList(map)
                            .compose(HttpUtils.applyIOSchedulers())
                            .map(s -> JsonUtils.parseObject(s).getJsonArray("list").toJavaList(SupplierBean.class));
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mLiveData) {
                });
        return mLiveData;
    }

}
