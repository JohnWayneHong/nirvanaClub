package com.jgw.delingha.module.supplement_to_box.base.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.PackageCodeInfoBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class SupplementToBoxModel {

    private ValueKeeperLiveData<Resource<PackageCodeInfoBean>> mCheckParentCodeInfoLiveData;

    public LiveData<Resource<PackageCodeInfoBean>> getPackageCodeInfo(String code) {
        if (mCheckParentCodeInfoLiveData == null) {
            mCheckParentCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getPackageCodeInfo(code)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<PackageCodeInfoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.LOADING, null, code));
                    }

                    @Override
                    public void onNext(PackageCodeInfoBean result) {
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, result, code));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, null, code));
                    }
                });
        return mCheckParentCodeInfoLiveData;
    }

    private ValueKeeperLiveData<Resource<String>> mCheckSonCodeInfoLiveData;

    public LiveData<Resource<String>> checkSubCodeStatus(String code, PackageCodeInfoBean parentCodeInfo) {
        if (mCheckSonCodeInfoLiveData == null) {
            mCheckSonCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("outerCodeId", code);
        map.put("parentOuterCodeId", parentCodeInfo.outerCodeId);
        map.put("productId", parentCodeInfo.productId);
        if (!TextUtils.isEmpty(parentCodeInfo.productBatchId)) {
            map.put("productBatchId", parentCodeInfo.productBatchId);
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkSubCodeStatus(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.LOADING, code, ""));
                    }

                    @Override
                    public void onNext(String result) {
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, code, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, code, e.getMessage()));
                    }
                });
        return mCheckSonCodeInfoLiveData;
    }

    public LiveData<Resource<String>> upload(List<String> codes, PackageCodeInfoBean packageCodeInfoBean) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("outerCodeIdList", codes);
        map.put("parentOuterCodeId", packageCodeInfoBean.outerCodeId);
        map.put("packageSpecification", packageCodeInfoBean.lastNumber);
        map.put("productCode", packageCodeInfoBean.productCode);
        map.put("productId", packageCodeInfoBean.productId);
        map.put("productName", packageCodeInfoBean.productName);
        map.put("relationTypeCode", packageCodeInfoBean.relationTypeCode);
        map.put("relationTypeName", packageCodeInfoBean.relationTypeName);
        if (!TextUtils.isEmpty(packageCodeInfoBean.productBatchId)) {
            map.put("productBatchId", packageCodeInfoBean.productBatchId);
            map.put("productBatchName", packageCodeInfoBean.productBatch);
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postSupplementToBox(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String result) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));

                    }
                });
        return liveData;
    }
}
