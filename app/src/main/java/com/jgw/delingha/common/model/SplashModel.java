package com.jgw.delingha.common.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.delingha.bean.RegisterDeviceBean;
import com.jgw.delingha.common.RegisterUUIDActivity;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : J-T
 * @date : 2022/5/6 13:48
 * description :
 */
public class SplashModel {
    public LiveData<Resource<RegisterDeviceBean>> getRegisterInfo(String secretKey, String oldSecretKey) {
        MutableLiveData<Resource<RegisterDeviceBean>> liveData = new MutableLiveData<>();
        Observable<Long> timer = Observable.timer(2, TimeUnit.SECONDS);
        Observable<RegisterDeviceBean> registerDeviceBeanObservable = getCheckRegisterInfoObservable(secretKey, oldSecretKey);
        Observable.zip(timer, registerDeviceBeanObservable, (aLong, registerDeviceBean) -> registerDeviceBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<RegisterDeviceBean>() {
                    @Override
                    public void onNext(RegisterDeviceBean registerDeviceBean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, registerDeviceBean, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    private Observable<RegisterDeviceBean> getCheckRegisterInfoObservable(String secretKey, String oldSecretKey) {
        return HttpUtils.getCJMGatewayApi(ApiService.class)
                .getCheckDevice(secretKey)
                .flatMap((Function<HttpResult<String>, ObservableSource<RegisterDeviceBean>>) httpResult -> {
                    RegisterDeviceBean registerDeviceBean = new RegisterDeviceBean();
                    if (httpResult.state == 200) {
                        registerDeviceBean.type = 0;
                        return Observable.just(registerDeviceBean);
                    }
                    if (httpResult.state == 501 && httpResult.results == null) {
                        //如果result为空就是没有设备，需要调用老的pda绑定逻辑查看有没有绑定
                        return getCheckOldDevicesIdObservable(secretKey, oldSecretKey);
                    }
                    switch (httpResult.state) {
                        case 501:
                            // 如果result不为空,表示审批不通过,需要显示提示
                            registerDeviceBean.type = RegisterUUIDActivity.DISABLE;
                            registerDeviceBean.errorMessage = httpResult.msg;
                            break;
                        case 502:
                            registerDeviceBean.type = RegisterUUIDActivity.WAITED_REVIEW;
                            break;
                        default:
                            registerDeviceBean.type = -1;
                            registerDeviceBean.errorMessage = httpResult.msg;
                    }
                    return Observable.just(registerDeviceBean);
                });
    }

    private Observable<RegisterDeviceBean> getCheckOldDevicesIdObservable(String secretKey, String oldSecretKey) {
        final RegisterDeviceBean registerDeviceBean = new RegisterDeviceBean();
        return HttpUtils.getCJMGatewayApi(ApiService.class)
                .getOldCheckDevice(oldSecretKey)
                .flatMap(stringHttpResult12 -> {
                    if (stringHttpResult12.state == 200) {
                        //检查旧码通过
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("oldSecretKey", oldSecretKey);
                        map.put("secretKey", secretKey);
                        return HttpUtils.getCJMGatewayApi(ApiService.class)
                                .autoRegister(map)
                                .map(stringHttpResult1 -> {
                                    if (stringHttpResult1.state == 200) {
                                        registerDeviceBean.type = 0;
                                    } else {
                                        registerDeviceBean.type = -1;
                                    }
                                    return registerDeviceBean;
                                });
                    }
                    registerDeviceBean.type = RegisterUUIDActivity.UNREGISTERED;
                    return Observable.just(registerDeviceBean);
                });
    }
}
