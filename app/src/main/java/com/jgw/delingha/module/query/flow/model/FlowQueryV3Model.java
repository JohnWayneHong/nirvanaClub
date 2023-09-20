package com.jgw.delingha.module.query.flow.model;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.FlowQueryBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import io.reactivex.disposables.Disposable;

/**
 * author : Cxz
 * data : 2019/11/29
 * description :
 */
public class FlowQueryV3Model {
    public LiveData<Resource<FlowQueryBean>> QueryFlow(String code) {
        ValueKeeperLiveData<Resource<FlowQueryBean>> liveData = new ValueKeeperLiveData<>();

        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getQueryFlow(code)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<FlowQueryBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                       liveData.postValue(new Resource<>(Resource.LOADING,null,""));
                    }
                    @Override
                    public void onNext(FlowQueryBean flowQueryBean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS,flowQueryBean,""));
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR,null,e.getMessage()));
                    }
                });
        return liveData;
    }
}
