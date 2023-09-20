package com.jgw.delingha.module.setting_center.model;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;

public class SettingCenterModel {
    public LiveData<Resource<String>> logout(){
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        if (NetUtils.iConnected()){
            HttpUtils.getGatewayApi(ApiService.class)
                    .logout()
                    .compose(HttpUtils.applyResultNullableMainSchedulers())
                    .subscribe(new CustomObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            liveData.postValue(new Resource<>(Resource.SUCCESS,null,""));
                        }

                        @Override
                        public void onError(Throwable e) {
                            liveData.postValue(new Resource<>(Resource.ERROR,null,e.getMessage()));
                        }
                    });
        }else {
            liveData.postValue(new Resource<>(Resource.ERROR,null,"无网络"));
        }
        return liveData;
    }
}
