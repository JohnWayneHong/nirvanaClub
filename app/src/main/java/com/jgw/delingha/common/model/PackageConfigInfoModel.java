package com.jgw.delingha.common.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.InWarehousePackageOperator;
import com.jgw.delingha.sql.operator.PackageConfigOperator;
import com.jgw.delingha.sql.operator.ProductBatchOperator;
import com.jgw.delingha.sql.operator.ProductInfoOperator;
import com.jgw.delingha.sql.operator.ProductPackageOperator;
import com.jgw.delingha.sql.operator.StorePlaceOperator;
import com.jgw.delingha.sql.operator.WareHouseOperator;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取包装模块配置内各种信息的model
 */
public class PackageConfigInfoModel {
    private final ProductInfoOperator mProductInfoOperator;
    private final ProductPackageOperator mProductPackageOperator;
    private final ProductBatchOperator mProductBatchOperator;
    private final PackageConfigOperator mPackageConfigOperator;
    private final InWarehousePackageOperator mInWarehousePackageOperator;
    private final WareHouseOperator mWareHouseOperator;
    private final StorePlaceOperator mStorePlaceOperator;

    public PackageConfigInfoModel() {
        mProductInfoOperator = new ProductInfoOperator();
        mProductPackageOperator = new ProductPackageOperator();
        mProductBatchOperator = new ProductBatchOperator();
        mPackageConfigOperator = new PackageConfigOperator();
        mInWarehousePackageOperator = new InWarehousePackageOperator();
        mWareHouseOperator = new WareHouseOperator();
        mStorePlaceOperator = new StorePlaceOperator();
    }

    public LiveData<Resource<PackageConfigEntity>> getConfigInfo(long configId) {
        MutableLiveData<Resource<PackageConfigEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mPackageConfigOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<PackageConfigEntity>() {
                    @Override
                    public void onNext(PackageConfigEntity configurationEntity) {
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


    public LiveData<Resource<List<Long>>> checkInWarehousePackageWaitUpload() {
        MutableLiveData<Resource<List<Long>>> liveData = new ValueKeeperLiveData<>();

        Observable.just("whatever")
                .map(s -> {
                    List<Long> idList = mInWarehousePackageOperator.queryAllConfigIdList();
                    ArrayList<Long> tempList = new ArrayList<>();
                    for (int i = 0; i < idList.size(); i++) {
                        Long config_id = idList.get(i);
                        long l = mInWarehousePackageOperator.queryCountByConfigId(config_id);
                        if (l < 2) {
                            tempList.add(config_id);
                        }
                    }
                    idList.removeAll(tempList);
                    return idList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<Long>>() {
                    @Override
                    public void onNext(List<Long> list) {
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

    public LiveData<Resource<ProductInfoEntity>> getProductInfo(Long id) {
        MutableLiveData<Resource<ProductInfoEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(mProductInfoOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ProductInfoEntity>() {
                    @Override
                    public void onNext(ProductInfoEntity entity) {
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

    public LiveData<Resource<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>> getProductPackageInfo(String productId) {
        MutableLiveData<Resource<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(productId)
                .map(mProductPackageOperator::queryDataByProductId)
                .map(entity -> {
                    Class<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> clazz = ProductWareHouseBean.ListBean.ProductPackageRatiosBean.class;
                    String productPackageRatios = entity.getProductPackageRatios();
                    return JsonUtils.parseArray(productPackageRatios, clazz);
                })
                .filter(list -> {
                    if (list.isEmpty()) {
                        throw new CustomHttpApiException(500, "");
                    }
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>() {
                    @Override
                    public void onNext(List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "请求产品包装关联级别失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfo(long batchId) {
        MutableLiveData<Resource<ProductBatchEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(batchId)
                .map(mProductBatchOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ProductBatchEntity>() {
                    @Override
                    public void onNext(ProductBatchEntity entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取商品批次信息失败"));
                    }
                });

        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouseInfo(Long wareHouseId) {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(wareHouseId)
                .map(mWareHouseOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<WareHouseEntity>() {
                    @Override
                    public void onNext(WareHouseEntity wareHouseEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, wareHouseEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<StorePlaceEntity>> getStorePlaceInfo(Long storePlaceId) {
        MutableLiveData<Resource<StorePlaceEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(storePlaceId)
                .map(mStorePlaceOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StorePlaceEntity>() {
                    @Override
                    public void onNext(StorePlaceEntity storePlaceEntity) {
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

    public LiveData<Resource<Long>> saveConfig(PackageConfigEntity entity) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    entity1.setCreateTime(System.currentTimeMillis());
                    entity1.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
                    return mPackageConfigOperator.putData(entity1);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long id) {
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
                    PackageConfigEntity entity = mPackageConfigOperator.queryDataById(aLong);
                    String taskId = null;
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
                            mPackageConfigOperator.putData(entity);
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
                    public void onNext(String s) {
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

    public LiveData<Resource<String>> getPackageTaskIdList(long configId) {
        ArrayList<Long> configIds = new ArrayList<>();
        configIds.add(configId);
        return getPackageTaskIdList(configIds);
    }

    public LiveData<Resource<String>> getPackageTaskIdList(List<Long> configId) {

        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.fromIterable(configId)
                .map(aLong -> {
                    PackageConfigEntity entity = mPackageConfigOperator.queryDataById(aLong);
                    String taskId = null;
                    //noinspection ConstantConditions
                    if (!TextUtils.isEmpty(entity.getTaskId())) {
                        taskId = entity.getTaskId();
                    } else {
                        HttpResult<String> result = HttpUtils.getGatewayApi(ApiService.class)
                                .getPackageTaskId("1")
                                .blockingSingle();
                        if (result != null && result.state == 200) {
                            JsonObject jsonObject = JsonUtils.parseObject(result.results);
                            taskId = jsonObject.getString("value");
                            entity.setTaskId(taskId);
                            mPackageConfigOperator.putData(entity);
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
                    public void onNext(String s) {
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
