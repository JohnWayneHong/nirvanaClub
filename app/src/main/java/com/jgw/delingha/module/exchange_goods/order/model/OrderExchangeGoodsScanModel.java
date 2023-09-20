package com.jgw.delingha.module.exchange_goods.order.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import java.util.HashMap;

import io.reactivex.disposables.Disposable;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsScanModel {
    public LiveData<Resource<OrderExchangeGoodsCodeBean>> getCodeInfo(String code, String productId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("outerCodeId", code);
        map.put("productId", productId);
        MutableLiveData<Resource<OrderExchangeGoodsCodeBean>> liveData = new MutableLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getOrderGoodsCodeInfo(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrderExchangeGoodsCodeBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onNext(OrderExchangeGoodsCodeBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS,data,""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR,new OrderExchangeGoodsCodeBean(code),""));
                    }
                });
        return liveData;
    }
}
