package com.jgw.delingha.module.packaging.stock_in_packaged.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.ConfigurationSelectBean;
import com.jgw.delingha.bean.PackageInCheckBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockInPackagedEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.StockInPackagedOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class StockInPackagedModel {

    private final int mUploadGroupCount = 500;
    public int success;
    public int error;
    private final StockInPackagedOperator mOperator = new StockInPackagedOperator();
    private final ConfigurationOperator configurationOperator = new ConfigurationOperator();

    //下面是Setting的数据库操作
    public LiveData<Resource<Boolean>> hasWaitUpload() {
        MutableLiveData<Resource<Boolean>> liveData = new ValueKeeperLiveData<>();
        Observable.just(1)
                .map(integer -> mOperator.queryAllConfigIdList().size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aBoolean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
                    }
                });
        return liveData;
    }

    public StockInPackagedEntity getCodeData(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean putData(StockInPackagedEntity entity) {
        return mOperator.putData(entity)>0;
    }

    public LiveData<Resource<Long>> getCount(long configId) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mOperator::queryCountByConfigId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long aLong) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aLong, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取数目失败"));
                    }
                });
        return liveData;
    }

    private ValueKeeperLiveData<Resource<CodeBean>> mCheckCodeInfoLiveData;

    public LiveData<Resource<CodeBean>> getCheckCode(String code, String productId) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkPackageIn(code)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<PackageInCheckBean>() {
                    @Override
                    public void onNext(@NonNull PackageInCheckBean bean) {
                        if (TextUtils.isEmpty(bean.productId)) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(code), "该码未关联产品，请仔细核对！"));
                        } else if (TextUtils.isEmpty(productId)) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, new CodeBean(code), ""));
                        } else if (!TextUtils.equals(productId, bean.productId)) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(code), "该码产品名称与入库设置不匹配，请仔细核对！"));
                        } else {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, new CodeBean(code), ""));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(code), e.getMessage()));
                            return;
                        }
                        if (e instanceof CustomHttpApiException) {
                            switch (((CustomHttpApiException) e).getApiExceptionCode()) {
                                case 200:
                                    mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(code), "该码未关联产品，请仔细核对！"));
                                    break;
                                case 500:
                                    mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(code), e.getMessage()));
                                    break;
                                default:
                                    super.onError(e);
                            }
                        } else {
                            super.onError(e);
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(code), e.getMessage()));
                        }
                    }
                });
        return mCheckCodeInfoLiveData;
    }

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public void updateCodeStatusByCode(String outerCodeId, int statusCodeSuccess) {
        mOperator.updateCodeStatusByCode(outerCodeId, statusCodeSuccess);
    }


    public LiveData<Resource<List<StockInPackagedEntity>>> getRefreshListData(long configId) {
        MutableLiveData<Resource<List<StockInPackagedEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(aLong, -1, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockInPackagedEntity>>() {
                    @Override
                    public void onNext(@NonNull List<StockInPackagedEntity> stockInPackagedEntities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, stockInPackagedEntities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取扫码数据失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<StockInPackagedEntity>>> loadList(long configId, int page) {
        MutableLiveData<Resource<List<StockInPackagedEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(aLong, page, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockInPackagedEntity>>() {
                    @Override
                    public void onNext(@NonNull List<StockInPackagedEntity> stockInPackagedEntities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, stockInPackagedEntities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public long getVerifyingOrFailDataCount(long configId) {
        return mOperator.queryVerifyingDataByConfigId(configId);
    }


    //*****************************************待执行***************************************************//

    public LiveData<Resource<List<ConfigurationSelectBean>>> getSelectBeanList() {
        MutableLiveData<Resource<List<ConfigurationSelectBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) s -> Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .map(configurationOperator::queryDataById)
                .map(entity -> {
                    ConfigurationSelectBean bean = new ConfigurationSelectBean();
                    bean.configId=entity.getId();
                    bean.dataTime=entity.getDataTime();
                    bean.productName=entity.getProductName();
                    bean.wareHouseName=entity.getWareHouseName();
                    return bean;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ConfigurationSelectBean>() {
                    final List<ConfigurationSelectBean> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull ConfigurationSelectBean bean) {
                        list.add(bean);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public void deleteAllByConfigId(Long configId) {
        mOperator.deleteAllByConfigId(configId);
    }

    public void deleteAll() {
        mOperator.deleteAll();
    }

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        ConfigurationEntity configurationEntity = configurationOperator.queryDataById(configId);


        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;
                    List<StockInPackagedEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(configId1, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockInPackagedEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;

                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(aLong -> mOperator.queryGroupDataByConfigId(configId, aLong, mUploadGroupCount))
                .filter(list -> list.size() > 0)
                .flatMap((Function<List<StockInPackagedEntity>, Publisher<HttpResult<String>>>) list -> {

                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    ArrayList<String> codes = new ArrayList<>();
                    for (StockInPackagedEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    if (!TextUtils.isEmpty(configurationEntity.getProductId())) {
                        map.put("productCode", configurationEntity.getProductCode());
                        map.put("productId", configurationEntity.getProductId());
                        map.put("productName", configurationEntity.getProductName());
                    }
                    map.put("remark", configurationEntity.getRemark());
                    map.put("wareHouseId", configurationEntity.getWareHouseId());
                    map.put("wareHouseName", configurationEntity.getWareHouseName());
                    map.put("wareHouseCode", configurationEntity.getWareHouseCode());
                    map.put("storeHouseId", configurationEntity.getStockHouseId());
                    map.put("storeHouseName", configurationEntity.getStockHouseName());

                    map.put("taskId", taskId);
                    map.put("outerCodeIdList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockInPackagedCodeList(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.removeData(list);
                        } else {
                            error += codes.size();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error += codes.size();
                    }
                    if (stringHttpResult == null) {
                        stringHttpResult = new HttpResult<>();
                        stringHttpResult.state = 200;
                        stringHttpResult.msg = "上传失败";
                    }
                    return Observable.just(stringHttpResult)
                            .toFlowable(BackpressureStrategy.BUFFER);
                }).sequential()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomFlowableSubscriber<HttpResult<String>>() {
                    Subscription subscription;

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        this.subscription = s;
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(HttpResult<String> stringHttpResult) {
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        liveData.postValue(new Resource<>(Resource.ERROR, new UploadResultBean(success, error), ""));
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, new UploadResultBean(success, error), ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<UploadResultBean>> uploadConfigGroupList(List<Long> configIds) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        Flowable.fromIterable(configIds)
                .parallel()
                .runOn(Schedulers.io())
                .flatMap((Function<Long, Publisher<ConfigurationEntity>>) configId -> {
                    boolean next = true;
                    long currentId = 0;

                    List<StockInPackagedEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockInPackagedEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;

                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                //String name1 = Thread.currentThread().getName();
                                LogUtils.xswShowLog("");
                                ConfigurationEntity entity = configurationOperator.queryDataById(configId);
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<ConfigurationEntity, Publisher<HttpResult<String>>>) config -> {

                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = config.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    if (!TextUtils.isEmpty(config.getProductId())) {
                        map.put("productCode", config.getProductCode());
                        map.put("productId", config.getProductId());
                        map.put("productName", config.getProductName());
                    }
                    map.put("remark", config.getRemark());
                    map.put("wareHouseId", config.getWareHouseId());
                    map.put("wareHouseName", config.getWareHouseName());
                    map.put("wareHouseCode", config.getWareHouseCode());
                    map.put("storeHouseId", config.getStockHouseId());
                    map.put("storeHouseName", config.getStockHouseName());

                    List<StockInPackagedEntity> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (StockInPackagedEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockInPackagedCodeList(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.removeData(list);
                        } else {
                            error += codes.size();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error += codes.size();
                    }
                    if (stringHttpResult == null) {
                        stringHttpResult = new HttpResult<>();
                        stringHttpResult.state = 200;
                        stringHttpResult.msg = "上传失败";
                    }
                    return Observable.just(stringHttpResult)
                            .toFlowable(BackpressureStrategy.BUFFER);
                }).sequential()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomFlowableSubscriber<HttpResult<String>>() {
                    Subscription subscription;

                    @Override
                    public void onSubscribe(@NonNull Subscription s) {
                        this.subscription = s;
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(HttpResult<String> stringHttpResult) {
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        liveData.postValue(new Resource<>(Resource.ERROR, new UploadResultBean(success, error), ""));
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, new UploadResultBean(success, error), ""));
                    }
                });
        return liveData;
    }

}
