package com.jgw.delingha.module.supplement_to_box.produce.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.PackageCheckCodeRequestParamBean;
import com.jgw.delingha.bean.ProducePackageCodeInfoBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiPackageService;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.utils.CodeTypeUtils;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ProduceSupplementToBoxModel {

    private ValueKeeperLiveData<Resource<ProducePackageCodeInfoBean>> mCheckParentCodeInfoLiveData;

    public LiveData<Resource<ProducePackageCodeInfoBean>> checkBoxCode(String code) {
        if (mCheckParentCodeInfoLiveData == null) {
            mCheckParentCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();

        map.put("parentCode", code);
        map.put("parentCodeTypeId", CodeTypeUtils.getCodeTypeId(code));
        //包装场景类型 0- 是生产包装 1- 是仓储包装 2-混合包装 3-补码入箱
        map.put("packageSceneType", CodeTypeUtils.SupplementToBoxType);
//        map.put("packageLevel", param.packageLevel);
        HttpUtils.getGatewayApi(ApiService.class)
                .checkPackagingParentCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.LOADING, null, code));
                    }

                    @Override
                    public void onNext(String s) {
                        ProducePackageCodeInfoBean bean = JsonUtils.parseObject(s, ProducePackageCodeInfoBean.class);
                        bean.packageLevel=bean.codeLevel;
                        bean.packageCode=bean.outerCode;
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, bean, code));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, null, code));
                        super.onError(e);
                    }
                });
        return mCheckParentCodeInfoLiveData;
    }


    private ValueKeeperLiveData<Resource<String>> mCheckSonCodeInfoLiveData;

    public LiveData<Resource<String>> checkCode(PackageCheckCodeRequestParamBean param) {
        if (mCheckSonCodeInfoLiveData == null) {
            mCheckSonCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        //此空值不可删除
//        map.put("relationTypeCode", "");
        String code = param.code;
        map.put("outerCode", code);
        map.put("codeTypeId", CodeTypeUtils.getCodeTypeId(code));
        map.put("packageSceneType", CodeTypeUtils.SupplementToBoxType);
        String boxCode = param.boxCode;
        map.put("parentCode", boxCode);
        map.put("parentCodeTypeId", CodeTypeUtils.getCodeTypeId(boxCode));
        HttpUtils.getGatewayApi(ApiService.class)
                .checkPackagingCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.LOADING, code, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, code, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, code, e.getMessage()));
                        super.onError(e);
                    }
                });

        return mCheckSonCodeInfoLiveData;
    }

    public LiveData<Resource<String>> upload(List<String> codes, ProducePackageCodeInfoBean packageCodeInfoBean) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        packageCodeInfoBean.sonCodeList=codes;
        HttpUtils.getGatewayApi(ApiPackageService.class)
                .postProduceSupplementToBox(packageCodeInfoBean)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String result) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, result, ""));

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
