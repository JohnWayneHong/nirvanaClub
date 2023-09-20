package com.jgw.delingha.common.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.CustomerOperator;
import com.jgw.delingha.sql.operator.LogisticsCompanyOperator;
import com.jgw.delingha.sql.operator.ProductBatchOperator;
import com.jgw.delingha.sql.operator.ProductInfoOperator;
import com.jgw.delingha.sql.operator.StorePlaceOperator;
import com.jgw.delingha.sql.operator.WareHouseOperator;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取配置内各种信息的model
 * (如客户信息,仓库,商品等)
 */
public class ConfigInfoModel {

    private final CustomerOperator mCustomerOperator;
    private final LogisticsCompanyOperator mLogisticsCompanyOperator;
    private final ConfigurationOperator mConfigOperator;
    private final ProductInfoOperator productInfoOperator;
    private final ProductBatchOperator productBatchOperator;
    private final WareHouseOperator wareHouseOperator;
    private final StorePlaceOperator storePlaceOperator;

    public ConfigInfoModel() {
        productInfoOperator = new ProductInfoOperator();
        productBatchOperator = new ProductBatchOperator();
        wareHouseOperator = new WareHouseOperator();
        storePlaceOperator = new StorePlaceOperator();
        mCustomerOperator = new CustomerOperator();
        mLogisticsCompanyOperator = new LogisticsCompanyOperator();
        mConfigOperator = new ConfigurationOperator();
    }

    public LiveData<Resource<ConfigurationEntity>> getConfigInfo(long configId) {
        MutableLiveData<Resource<ConfigurationEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mConfigOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ConfigurationEntity>() {
                    @Override
                    public void onNext(@NonNull ConfigurationEntity configurationEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, configurationEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取配置信息失败"));
                    }
                });
        return liveData;
    }

    public ConfigurationEntity getConfigEntity(long configId) {
        return mConfigOperator.queryDataById(configId);
    }

    public LiveData<Resource<CustomerEntity>> getCustomerInfo(Long customerId) {
        MutableLiveData<Resource<CustomerEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(customerId)
                .map(mCustomerOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<CustomerEntity>() {
                    @Override
                    public void onNext(@NonNull CustomerEntity entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<LogisticsCompanyEntity>> getLogisticsCompanyInfo(Long id) {
        MutableLiveData<Resource<LogisticsCompanyEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(mLogisticsCompanyOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<LogisticsCompanyEntity>() {
                    @Override
                    public void onNext(@NonNull LogisticsCompanyEntity entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfoEntity(Long id) {
        MutableLiveData<Resource<ProductBatchEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> productBatchOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ProductBatchEntity>() {
                    @Override
                    public void onNext(@NonNull ProductBatchEntity productBatchEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, productBatchEntity, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoEntity(Long id) {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> wareHouseOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<WareHouseEntity>() {
                    @Override
                    public void onNext(@NonNull WareHouseEntity wareHouseEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, wareHouseEntity, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getCurrentWarehouseInfoEntity() {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(aLong -> {
                    //重新登录会获取新的基础数据 所以基础数据都是当前用户的
                    List<WareHouseEntity> list = wareHouseOperator.queryAll();
                    if (list != null && list.size() == 1) {
                        return list.get(0);
                    }
                    throw new CustomHttpApiException(500, "数据异常");
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<WareHouseEntity>() {
                    @Override
                    public void onNext(@NonNull WareHouseEntity wareHouseEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, wareHouseEntity, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<StorePlaceEntity>> getStorePlaceInfoEntity(Long id) {
        MutableLiveData<Resource<StorePlaceEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> storePlaceOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StorePlaceEntity>() {
                    @Override
                    public void onNext(@NonNull StorePlaceEntity storePlaceEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, storePlaceEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfoEntity(Long id) {
        MutableLiveData<Resource<ProductInfoEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> productInfoOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ProductInfoEntity>() {
                    @Override
                    public void onNext(@NonNull ProductInfoEntity productInfoEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, productInfoEntity, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> saveConfig(ConfigurationEntity entity) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    entity1.setCreateTime(System.currentTimeMillis());
                    entity1.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
                    return mConfigOperator.putData(entity1);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long id) {
                        if (id == -1) {
                            liveData.postValue(new Resource<>(Resource.ERROR, id, "保存设置信息失败"));
                        } else {
                            liveData.postValue(new Resource<>(Resource.SUCCESS, id, ""));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "保存设置信息失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> updateConfig(ConfigurationEntity entity) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    entity1.setCreateTime(System.currentTimeMillis());
                    entity1.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
                    mConfigOperator.putData(entity1);
                    return entity1.getId();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long id) {
                        if (id == -1) {
                            liveData.postValue(new Resource<>(Resource.ERROR, id, "保存设置信息失败"));
                        } else {
                            liveData.postValue(new Resource<>(Resource.SUCCESS, id, ""));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "保存设置信息失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> getTaskId(long configId) {
        ArrayList<Long> configIds = new ArrayList<>();
        configIds.add(configId);
        return getTaskIdList(configIds);
    }

    public LiveData<Resource<String>> getTaskIdList(List<Long> configId) {

        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.fromIterable(configId)
                .map(aLong -> {
                    String taskId = null;
                    ConfigurationEntity entity = mConfigOperator.queryDataById(aLong);
                    //noinspection ConstantConditions
                    if (!TextUtils.isEmpty(entity.getTaskId())) {
                        taskId = entity.getTaskId();
                    } else {
                        HttpResult<String> result = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .getTaskId()
                                .blockingSingle();
                        if (result != null && result.state == 200) {

                            entity.setTaskId(result.results);
                            taskId = result.results;
                            mConfigOperator.putData(entity);
                        }
                    }
                    return taskId;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        LogUtils.xswShowLog("taskId=" + s);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        liveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });

        return liveData;
    }
}
