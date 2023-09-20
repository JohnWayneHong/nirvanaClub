package com.jgw.delingha.custom_module.delingha.select_list.employee_list;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.EmployeeBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;

public class EmployeeListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mProductListLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getEmployeeList(@Nullable String searchStr, int page) {
        if (mProductListLiveData == null) {
            mProductListLiveData = new ValueKeeperLiveData<>();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        if (!TextUtils.isEmpty(searchStr)) {
            map.put("search", searchStr);
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getEmployeeList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map((Function<String, List<? extends SelectItemSupport>>) s -> {
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(EmployeeBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mProductListLiveData) {});
        return mProductListLiveData;
    }

}
