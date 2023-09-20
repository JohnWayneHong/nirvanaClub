package com.jgw.delingha.module.logistics_statistics.statistics_list;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonArray;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.StatisticsExchangeGoodsResultBean;
import com.jgw.delingha.bean.StatisticsExchangeWarehouseResultBean;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.bean.StatisticsResultBean;
import com.jgw.delingha.bean.StatisticsStockInResultBean;
import com.jgw.delingha.bean.StatisticsStockOutResultBean;
import com.jgw.delingha.bean.StatisticsStockReturnResultBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class StatisticsListModel {
    public LiveData<Resource<List<StatisticsResultBean>>> getStatisticalStockInList(StatisticsParamsBean data, int page) {
        ValueKeeperLiveData<Resource<List<StatisticsResultBean>>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime", data.getStartTime());
        map.put("endTime", data.getEndTime());
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        ArrayList<String> groupBys = new ArrayList<>();
        for (StatisticsParamsBean.StatisticalDimension s : data.getList()) {
            groupBys.add(s.dimensionId);
        }
        map.put("groupBys", groupBys);
        if (!TextUtils.isEmpty(data.getProductId())) {
            map.put("productId", data.getProductId());
        }
        if (!TextUtils.isEmpty(data.getBatchId())) {
            map.put("productBatchId", data.getBatchId());
        }
        if (!TextUtils.isEmpty(data.getWareHouseId())) {
            map.put("wareHouseId", data.getWareHouseId());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postStatisticsStockInList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    JsonArray jsonList = jsonObject.getJsonArray("list");
                    List<StatisticsStockInResultBean> list = jsonList.toJavaList(StatisticsStockInResultBean.class);
                    return list;
                })
                .map((Function<List<StatisticsStockInResultBean>, List<StatisticsResultBean>>) c -> {

                    ArrayList<StatisticsResultBean> list = new ArrayList<>(c);
                    for (StatisticsResultBean s : list) {
                        s.setStatisticsDimensionInfo(data.getDimensionInfo());
                    }
                    return list;
                })
                .subscribe(new CustomObserver<List<StatisticsResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<StatisticsResultBean> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<StatisticsResultBean>>> getStatisticalStockOutList(StatisticsParamsBean data, int page) {
        ValueKeeperLiveData<Resource<List<StatisticsResultBean>>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime", data.getStartTime());
        map.put("endTime", data.getEndTime());
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        ArrayList<String> groupBys = new ArrayList<>();
        for (StatisticsParamsBean.StatisticalDimension s : data.getList()) {
            groupBys.add(s.dimensionId);
        }
        map.put("groupBys", groupBys);
        if (!TextUtils.isEmpty(data.getProductId())) {
            map.put("productId", data.getProductId());
        }
        if (!TextUtils.isEmpty(data.getBatchId())) {
            map.put("productBatchId", data.getBatchId());
        }
        if (!TextUtils.isEmpty(data.getCustomerId())) {
            map.put("receiveOrganizationId", data.getCustomerId());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postStatisticsStockOutList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    JsonArray jsonList = jsonObject.getJsonArray("list");
                    List<StatisticsStockOutResultBean> list = jsonList.toJavaList(StatisticsStockOutResultBean.class);
                    return list;
                })
                .map((Function<List<StatisticsStockOutResultBean>, List<StatisticsResultBean>>) c -> {

                    ArrayList<StatisticsResultBean> list = new ArrayList<>(c);
                    for (StatisticsResultBean s : list) {
                        s.setStatisticsDimensionInfo(data.getDimensionInfo());
                    }
                    return list;
                })
                .subscribe(new CustomObserver<List<StatisticsResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<StatisticsResultBean> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<StatisticsResultBean>>> getStatisticalStockReturnList(StatisticsParamsBean data, int page) {
        ValueKeeperLiveData<Resource<List<StatisticsResultBean>>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime", data.getStartTime());
        map.put("endTime", data.getEndTime());
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        ArrayList<String> groupBys = new ArrayList<>();
        for (StatisticsParamsBean.StatisticalDimension s : data.getList()) {
            groupBys.add(s.dimensionId);
        }
        map.put("groupBys", groupBys);
        if (!TextUtils.isEmpty(data.getProductId())) {
            map.put("productId", data.getProductId());
        }
        if (!TextUtils.isEmpty(data.getBatchId())) {
            map.put("productBatchId", data.getBatchId());
        }
        if (!TextUtils.isEmpty(data.getCustomerId())) {
            map.put("returnCustomerId", data.getCustomerId());
        }
        if (!TextUtils.isEmpty(data.getWareHouseId())) {
            map.put("inWareHouseId", data.getWareHouseId());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postStatisticsStockReturnList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    JsonArray jsonList = jsonObject.getJsonArray("list");
                    List<StatisticsStockReturnResultBean> list = jsonList.toJavaList(StatisticsStockReturnResultBean.class);
                    return list;
                })
                .map((Function<List<StatisticsStockReturnResultBean>, List<StatisticsResultBean>>) c -> {

                    ArrayList<StatisticsResultBean> list = new ArrayList<>(c);
                    for (StatisticsResultBean s : list) {
                        s.setStatisticsDimensionInfo(data.getDimensionInfo());
                    }
                    return list;
                })
                .subscribe(new CustomObserver<List<StatisticsResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<StatisticsResultBean> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<StatisticsResultBean>>> getStatisticalExchangeWarehouseList(StatisticsParamsBean data, int page) {
        ValueKeeperLiveData<Resource<List<StatisticsResultBean>>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime", data.getStartTime());
        map.put("endTime", data.getEndTime());
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        ArrayList<String> groupBys = new ArrayList<>();
        for (StatisticsParamsBean.StatisticalDimension s : data.getList()) {
            groupBys.add(s.dimensionId);
        }
        map.put("groupBys", groupBys);
        if (!TextUtils.isEmpty(data.getProductId())) {
            map.put("productId", data.getProductId());
        }
        if (!TextUtils.isEmpty(data.getWareHouseIdOut())) {
            map.put("outWareHouseId", data.getWareHouseIdOut());
        }
        if (!TextUtils.isEmpty(data.getWareHouseIdIn())) {
            map.put("inWareHouseId", data.getWareHouseIdIn());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postStatisticsExchangeWarehouseList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    JsonArray jsonList = jsonObject.getJsonArray("list");
                    List<StatisticsExchangeWarehouseResultBean> list = jsonList.toJavaList(StatisticsExchangeWarehouseResultBean.class);
                    return list;
                })
                .map((Function<List<StatisticsExchangeWarehouseResultBean>, List<StatisticsResultBean>>) c -> {

                    ArrayList<StatisticsResultBean> list = new ArrayList<>(c);
                    for (StatisticsResultBean s : list) {
                        s.setStatisticsDimensionInfo(data.getDimensionInfo());
                    }
                    return list;
                })
                .subscribe(new CustomObserver<List<StatisticsResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<StatisticsResultBean> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<StatisticsResultBean>>> getStatisticalExchangeGoodsList(StatisticsParamsBean data, int page) {
        ValueKeeperLiveData<Resource<List<StatisticsResultBean>>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("startTime", data.getStartTime());
        map.put("endTime", data.getEndTime());
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        ArrayList<String> groupBys = new ArrayList<>();
        for (StatisticsParamsBean.StatisticalDimension s : data.getList()) {
            groupBys.add(s.dimensionId);
        }
        map.put("groupBys", groupBys);
        if (!TextUtils.isEmpty(data.getProductId())) {
            map.put("productId", data.getProductId());
        }
        if (!TextUtils.isEmpty(data.getCustomerIdOut())) {
            map.put("wareGoodsOutId", data.getCustomerIdOut());
        }
        if (!TextUtils.isEmpty(data.getCustomerIdIn())) {
            map.put("wareGoodsInId", data.getCustomerIdIn());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .postStatisticsExchangeGoodsList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    JsonArray jsonList = jsonObject.getJsonArray("list");
                    List<StatisticsExchangeGoodsResultBean> list = jsonList.toJavaList(StatisticsExchangeGoodsResultBean.class);
                    return list;
                })
                .map((Function<List<StatisticsExchangeGoodsResultBean>, List<StatisticsResultBean>>) c -> {

                    ArrayList<StatisticsResultBean> list = new ArrayList<>(c);
                    for (StatisticsResultBean s : list) {
                        s.setStatisticsDimensionInfo(data.getDimensionInfo());
                    }
                    return list;
                })
                .subscribe(new CustomObserver<List<StatisticsResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<StatisticsResultBean> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
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
