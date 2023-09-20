package com.jgw.delingha.custom_module.delingha.order_stock_in.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.delingha.bean.OrderStockInDetailsBean;
import com.jgw.delingha.bean.OrderStockInListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.bean.OrderStockUploadResultBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockInModel {
    public LiveData<Resource<List<OrderStockInListBean.ListBean>>> getOrderStockInList(String search, int page) {
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        MutableLiveData<Resource<List<OrderStockInListBean.ListBean>>> liveData = new MutableLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getDLHOrderStockInList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrderStockInListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockInListBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, data.list, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<OrderStockInDetailsBean.ListBean>>> getOrderStockInDetails(String houseList) {
        ValueKeeperLiveData<Resource<List<OrderStockInDetailsBean.ListBean>>> liveData = new ValueKeeperLiveData<>();

        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getOrderStockInDetails(houseList)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrderStockInDetailsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockInDetailsBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, data.inHouseProducts, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> upload(String mHouseList, String wareHouseInId) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("inHouseList", mHouseList);
                    map.put("wareHouseInId", wareHouseInId);
                   HttpUtils.getGatewayApi(ApiLogisticsService.class)
                            .postDLHOrderStockInUpload(map)
                           .compose(HttpUtils.applyResultNullableMainSchedulers())
                           .subscribe(new CustomObserver<String>() {

                               @Override
                               public void onSubscribe(Disposable d) {
                                   super.onSubscribe(d);
                                   liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                               }

                               @Override
                               public void onNext(String uploadResultBean) {
                                   liveData.setValue(new Resource<>(Resource.SUCCESS, "", null));
                               }

                               @Override
                               public void onError(Throwable e) {
                                   super.onError(e);
                                   liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                               }
                           });
        return liveData;
    }
}
