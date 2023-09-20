package com.jgw.delingha.module.exchange_goods.order.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.bean.OrderExchangeGoodsResultBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsDetailsModel {
    public LiveData<OrderExchangeGoodsDetailsBean> getOrderDetails(String orderCode) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList", orderCode);
        MutableLiveData<OrderExchangeGoodsDetailsBean> liveData = new MutableLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getOrderExchangeGoodsDetails(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrderExchangeGoodsDetailsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    public void onNext(OrderExchangeGoodsDetailsBean data) {
                        liveData.setValue(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
        return liveData;
    }

    public LiveData<Resource<OrderExchangeGoodsResultBean>> postOrderDetails(String orderCode, List<OrderExchangeGoodsDetailsBean.ProductsBean> list) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList", orderCode);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            OrderExchangeGoodsDetailsBean.ProductsBean productsBean = list.get(i);
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("goodsOutProductId", productsBean.goodsOutProductId);
            jsonObject.put("houseList", orderCode);
            jsonObject.put("codeLists", productsBean.codeList);
            maps.add(jsonObject.covertMap());
        }
        map.put("outProductSweepVOS", maps);
        MutableLiveData<Resource<OrderExchangeGoodsResultBean>> liveData = new MutableLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postOrderExchangeGoodsList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrderExchangeGoodsResultBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<OrderExchangeGoodsResultBean>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderExchangeGoodsResultBean data) {
                        liveData.setValue(new Resource<OrderExchangeGoodsResultBean>(Resource.SUCCESS, data, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<OrderExchangeGoodsResultBean>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }
}
