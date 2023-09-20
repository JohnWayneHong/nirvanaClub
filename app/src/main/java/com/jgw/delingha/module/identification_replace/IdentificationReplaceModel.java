package com.jgw.delingha.module.identification_replace;

import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;

/**
 * @author : J-T
 * @date : 2022/8/2 10:22
 * description :标识替换Model
 */
public class IdentificationReplaceModel {
    private ValueKeeperLiveData<Resource<Integer>> checkCodeLiveData;
    private ValueKeeperLiveData<Resource<String>> uploadLiveData;

    /**
     * type 1:旧码  2:新码
     */
    public MutableLiveData<Resource<Integer>> checkCode(String code, int type) {
        if (checkCodeLiveData == null) {
            checkCodeLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkIdentificationReplaceCode(code, type)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        checkCodeLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        checkCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        checkCodeLiveData.postValue(new Resource<>(Resource.ERROR, type, e.getMessage()));
                    }
                });
        return checkCodeLiveData;
    }


    /**
     * oldCode 旧码  code 新码
     */
    public MutableLiveData<Resource<String>> upload(String oldCode, String code) {
        if (uploadLiveData == null) {
            uploadLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, String> map = new HashMap<>(2);
        map.put("oldOuterCodeId", oldCode);
        map.put("outerCodeId", code);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .uploadIdentificationReplace(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        uploadLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        uploadLiveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        uploadLiveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return uploadLiveData;
    }
}
