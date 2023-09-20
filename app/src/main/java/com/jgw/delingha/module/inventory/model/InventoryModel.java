package com.jgw.delingha.module.inventory.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.InventoryCodeBean;
import com.jgw.delingha.bean.InventoryDetailsBean;
import com.jgw.delingha.bean.InventoryListBean;
import com.jgw.delingha.bean.InventoryScanBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class InventoryModel {
    public LiveData<Resource<List<InventoryListBean.ListBean>>> getInventoryList(String search, int page) {
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        MutableLiveData<Resource<List<InventoryListBean.ListBean>>> liveData = new MutableLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getInventoryList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<InventoryListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(InventoryListBean data) {
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

    public LiveData<Resource<List<InventoryDetailsBean.ListBean>>> getInventoryDetails(String houseList) {
        ValueKeeperLiveData<Resource<List<InventoryDetailsBean.ListBean>>> liveData = new ValueKeeperLiveData<>();

        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getInventoryDetails(houseList)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<InventoryDetailsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(InventoryDetailsBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, data.inventoryProducts, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    private ValueKeeperLiveData<Resource<InventoryScanBean>> mPostSingleInventoryLiveData;

    public LiveData<Resource<InventoryScanBean>> postSingleInventory(InventoryDetailsBean.ListBean bean, String code) {
        if (mPostSingleInventoryLiveData == null) {
            mPostSingleInventoryLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList", bean.houseList);
        map.put("inventoryProductId", bean.inventoryProductId);
        map.put("productId", bean.productId);
        map.put("productCode", bean.productCode);
        map.put("productName", bean.productName);
        map.put("wareHouseCode", bean.wareHouseCode);
        map.put("wareHouseId", bean.wareHouseId);
        map.put("wareHouseName", bean.wareHouseName);
        map.put("outerCodeId", code);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postSingleInventory(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<InventoryScanBean>() {

                    @Override
                    public void onNext(InventoryScanBean data) {
                        data.outerCodeId = code;
                        data.codeStatus = CodeBean.STATUS_CODE_SUCCESS;
                        mPostSingleInventoryLiveData.setValue(new Resource<>(Resource.SUCCESS, data, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        InventoryScanBean scanBean = new InventoryScanBean(code);
                        scanBean.codeStatus = CodeBean.STATUS_CODE_FAIL;
                        if (!NetUtils.iConnected()) {
                            mPostSingleInventoryLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, scanBean, ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mPostSingleInventoryLiveData.postValue(new Resource<>(Resource.ERROR, scanBean, e.getMessage()));
                        } else {
                            super.onError(e);
                            mPostSingleInventoryLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, scanBean, ""));
                        }
                    }
                });
        return mPostSingleInventoryLiveData;
    }

    public LiveData<Resource<List<InventoryCodeBean.ListBean>>> getInventoryFinishListByProduct(String inventoryProductId, String search, int page) {
        ValueKeeperLiveData<Resource<List<InventoryCodeBean.ListBean>>> liveData = new ValueKeeperLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("inventoryProductId", inventoryProductId);
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getInventoryFinishListByProduct(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<InventoryCodeBean>() {

                    @Override
                    public void onNext(InventoryCodeBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, data.list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            liveData.postValue(new Resource<>(Resource.NETWORK_ERROR, null, ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                        } else {
                            super.onError(e);
                            liveData.postValue(new Resource<>(Resource.NETWORK_ERROR, null, ""));
                        }
                    }
                });
        return liveData;
    }

    private ValueKeeperLiveData<Resource<Long>> mCalculationTotalLiveData;

    public LiveData<Resource<Long>> getCalculationTotal(Integer input) {
        if (mCalculationTotalLiveData == null) {
            mCalculationTotalLiveData = new ValueKeeperLiveData<>();
        }
        mCalculationTotalLiveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
        return mCalculationTotalLiveData;
    }

    public LiveData<Resource<List<InventoryScanBean>>> postGroupInventory(InventoryDetailsBean.ListBean bean, List<String> input) {
        ValueKeeperLiveData<Resource<List<InventoryScanBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.fromIterable(input)
                .map(new Function<String, InventoryScanBean>() {
                    @Override
                    public InventoryScanBean apply(String s) throws Exception {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("houseList", bean.houseList);
                        map.put("inventoryProductId", bean.inventoryProductId);
                        map.put("productId", bean.productId);
                        map.put("productCode", bean.productCode);
                        map.put("productName", bean.productName);
                        map.put("wareHouseCode", bean.wareHouseCode);
                        map.put("wareHouseId", bean.wareHouseId);
                        map.put("wareHouseName", bean.wareHouseName);
                        map.put("outerCodeId", s);
                        HttpResult<InventoryScanBean> httpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postSingleInventory(map)
                                .blockingSingle();
                        if (httpResult != null) {
                            if (httpResult.state == 200) {
                                httpResult.results.outerCodeId = s;
                                httpResult.results.codeStatus = CodeBean.STATUS_CODE_SUCCESS;
                                return httpResult.results;
                            } else if (httpResult.state == 500) {
                                InventoryScanBean bean = new InventoryScanBean(s);
                                bean.codeStatus = CodeBean.STATUS_CODE_FAIL;
                                bean.isRealError = true;
                                return bean;
                            } else {
                                return new InventoryScanBean(s);
                            }
                        } else {
                            return new InventoryScanBean(s);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<InventoryScanBean>() {
                    List<InventoryScanBean> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(InventoryScanBean uploadResultBean) {
                        list.add(uploadResultBean);
                    }

                    @Override
                    public void onComplete() {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, list, ""));
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
