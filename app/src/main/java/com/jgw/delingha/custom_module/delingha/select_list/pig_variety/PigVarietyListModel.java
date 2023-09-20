package com.jgw.delingha.custom_module.delingha.select_list.pig_variety;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonArray;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.PigVarietyBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class PigVarietyListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mProductListLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getPigVarietyList(@Nullable String searchStr, int page,String productSortId) {
        if (mProductListLiveData == null) {
            mProductListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("productSortId", productSortId);
        map.put("current", String.valueOf(page));
        map.put("pageSize", String.valueOf(CustomRecyclerAdapter.ITEM_PAGE_SIZE));
        if (!TextUtils.isEmpty(searchStr)) {
            map.put("search", searchStr);
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getPigVarietyList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map((Function<String, List<PigVarietyBean>>) s -> {
                    JsonArray jsonArray = JsonUtils.parseObject(s).getJsonArray("list");
                    ArrayList<PigVarietyBean> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jb = jsonArray.getJsonObject(i);
                        PigVarietyBean e = new PigVarietyBean();
                        e.pigVarietyId=jb.getString("productId");
                        e.pigVarietyName=jb.getString("productName");
                        list.add(e);
                    }
                    return list;
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mProductListLiveData) {});
        return mProductListLiveData;
    }

}
