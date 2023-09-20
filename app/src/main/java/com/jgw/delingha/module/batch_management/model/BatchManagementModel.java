package com.jgw.delingha.module.batch_management.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.BatchManagementBean;
import com.jgw.delingha.bean.BatchManagementListBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by xswwg
 * on 2020/12/21
 */
public class BatchManagementModel {

    public LiveData<Resource<List<BatchManagementBean>>> getBatchManagementList(String search, int page) {
        ValueKeeperLiveData<Resource<List<BatchManagementBean>>> liveData = new ValueKeeperLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiService.class)
                .getBatchManagementList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<BatchManagementListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(BatchManagementListBean bean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, bean.list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });

        return liveData;
    }

    public LiveData<Resource<String>> deleteBatch(int id) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .deleteBatch(id)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<BatchManagementListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(BatchManagementListBean bean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });

        return liveData;
    }

    public LiveData<Resource<String>> addBatch(BatchManagementBean bean) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .postAddBatch(bean)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String bean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });

        return liveData;
    }
    public LiveData<Resource<String>> editBatch(BatchManagementBean bean) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .putBatch(bean)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String bean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });

        return liveData;
    }
}
