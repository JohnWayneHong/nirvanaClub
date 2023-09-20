package com.jgw.delingha.module.packaging.statistics.model;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.PackageStatisticsHeaderBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiPackageService;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public class PackageStatisticsModel {
    public LiveData<Resource<PackageStatisticsHeaderBean>> getPackageStatisticsTwoDay() {
        ValueKeeperLiveData<Resource<PackageStatisticsHeaderBean>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiPackageService.class)
                .getPackageTwoDayData()
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<PackageStatisticsHeaderBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NotNull PackageStatisticsHeaderBean data) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, data, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<PackageStatisticsBean>>> getProductStatistics(Map<String,Object> map) {
        ValueKeeperLiveData<Resource<List<PackageStatisticsBean>>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiPackageService.class)
                .getPackageStatisticsByProduct(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<List<PackageStatisticsBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING,null,""));
                    }

                    @Override
                    public void onNext(@NotNull List<PackageStatisticsBean> data) {
                        Collections.sort(data);
                        liveData.postValue(new Resource<>(Resource.SUCCESS,data,""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR,null,e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<PackageStatisticsBean>>> getTimeStatistics(Map<String,Object> map) {
        ValueKeeperLiveData<Resource<List<PackageStatisticsBean>>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiPackageService.class)
                .getPackageStatisticsByTime(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<List<PackageStatisticsBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING,null,""));
                    }

                    @Override
                    public void onNext(@NotNull List<PackageStatisticsBean> data) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS,data,""));
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
