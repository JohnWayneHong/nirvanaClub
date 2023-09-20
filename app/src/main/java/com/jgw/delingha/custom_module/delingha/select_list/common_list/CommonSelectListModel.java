package com.jgw.delingha.custom_module.delingha.select_list.common_list;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CommonSelectListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommonSelectListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mListLiveData;

//    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(String mId) {
//        if (mListLiveData == null) {
//            mListLiveData = new ValueKeeperLiveData<>();
//        }
//        HashMap<String,String> map = new HashMap<>();
//        String search = "";
//
//        map.put("pageSize","10000");
//        map.put("parentCategoryId",mId);
//        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
//                .getCommonSelectList(map)
//                .compose(HttpUtils.applyIOSchedulers())
//                .map(s -> JsonUtils.parseObject(s).getJsonArray("list").toJavaList(CommonSelectListBean.class))
//                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mListLiveData) {});
//        return mListLiveData;
//    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getList(@Nullable String search,String mId) {
        if (mListLiveData == null) {
            mListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, String> map = new HashMap<>();

        map.put("pageSize", "10000");
        map.put("parentCategoryId", mId);

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getCommonSelectList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    List<CommonSelectListBean> originalList = JsonUtils.parseObject(s).getJsonArray("list").toJavaList(CommonSelectListBean.class);

                    if (TextUtils.isEmpty(search)) {
                        return originalList;
                    }
                    List<CommonSelectListBean> filteredList = new ArrayList<>();
                    for (CommonSelectListBean item : originalList) {
                        if (item.getShowName().contains(search)) {
                            filteredList.add(item);
                        }
                    }

                    return filteredList;
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mListLiveData) {});

        return mListLiveData;
    }


}
