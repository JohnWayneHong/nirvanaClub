package com.jgw.delingha.module.query.related.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.utils.CodeTypeUtils;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class RelatedQueryModel {

    public LiveData<Resource<CodeRelationInfoResultBean>> getCodeRelationInfo(String code, boolean isShowSingle) {
        final MutableLiveData<Resource<CodeRelationInfoResultBean>> resourceMutableLiveData = new ValueKeeperLiveData<>();
        ApiService gatewayApi = HttpUtils.getGatewayApi(ApiService.class);
        Observable<HttpResult<CodeRelationInfoResultBean>> httpResultObservable;
        if (isShowSingle){
            httpResultObservable= gatewayApi.searchCodeRelationShowSingle(code, CodeTypeUtils.getCodeTypeId(code));
        }else {
            httpResultObservable= gatewayApi.searchCodeRelation(code, CodeTypeUtils.getCodeTypeId(code));
        }
        httpResultObservable
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<CodeRelationInfoResultBean>() {
                    @Override
                    public void onNext(CodeRelationInfoResultBean codeRelationInfoResultBean) {
                        resourceMutableLiveData.setValue(new Resource<>(Resource.SUCCESS, codeRelationInfoResultBean, ""));
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
