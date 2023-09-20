package com.jgw.delingha.common.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.RegisterBean;
import com.jgw.delingha.bean.RegisterDeviceBean;
import com.jgw.delingha.common.RegisterUUIDActivity;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : J-T
 * @date : 2022/5/7 9:38
 * description :
 */
public class RegisterUUIDModel {

    public LiveData<Resource<String>> register(RegisterBean registerBean) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .pdaRegister(registerBean)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, "", ""));
                    }

                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<RegisterDeviceBean>> refreshRegisterState(String secretKey) {
        MutableLiveData<Resource<RegisterDeviceBean>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getCJMGatewayApi(ApiService.class)
                .getCheckDevice(secretKey)
                .map(stringHttpResult -> {
                    RegisterDeviceBean registerDeviceBean = new RegisterDeviceBean();
                    if (stringHttpResult.state == 200) {
                        registerDeviceBean.type = RegisterUUIDActivity.REGISTERED;
                    } else if (stringHttpResult.state == 501) {
                        //不通过
                        registerDeviceBean.type = RegisterUUIDActivity.DISABLE;
                        registerDeviceBean.errorMessage = stringHttpResult.msg;
                    } else {
                        //待审核
                        registerDeviceBean.type = RegisterUUIDActivity.WAITED_REVIEW;
                    }
                    return registerDeviceBean;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<RegisterDeviceBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(RegisterDeviceBean bean) {
                        if (bean.type == RegisterUUIDActivity.REGISTERED) {
                            liveData.postValue(new Resource<>(Resource.SUCCESS, bean, ""));
                        } else {
                            liveData.postValue(new Resource<>(Resource.ERROR, bean, ""));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.NETWORK_ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }


}
