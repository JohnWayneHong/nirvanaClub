package com.jgw.delingha.module.fail_log.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.FailLogBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;

public class FailLogListModel {

    public LiveData<Resource<FailLogBean>> getFailLogList(String houseList, int logType, String code, String date, int current) {
        final MutableLiveData<Resource<FailLogBean>> resourceMutableLiveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(houseList)) {
            map.put("houseList", houseList);
        }
        if (!TextUtils.isEmpty(code)) {
            map.put("outerCodeId", code);
        }
        if (!TextUtils.isEmpty(date)) {
            map.put("date", date);
        }

        map.put("logType", logType);
        map.put("current", current);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getFailLogList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<FailLogBean>() {
                    @Override
                    public void onNext(FailLogBean failLogBean) {
                        resourceMutableLiveData.setValue(new Resource<>(Resource.SUCCESS, failLogBean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        resourceMutableLiveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        resourceMutableLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }
                });
        return resourceMutableLiveData;
    }
}
