package com.jgw.delingha.module.stock_in.base.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.delingha.bean.ConfigurationSelectBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockInEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.StockInOperator;

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

public class StockInCodeModel {

    private final StockInOperator mOperator;
    private final ConfigurationOperator configurationOperator;
    private final int mUploadGroupCount = 500;
    private static final int PAGE_CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;

    public StockInCodeModel() {
        mOperator = new StockInOperator();
        configurationOperator = new ConfigurationOperator();
    }

    public int success;
    public int error;

    public LiveData<Resource<Integer>> getCount(long configId) {
        MutableLiveData<Resource<Integer>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> (int) mOperator.queryCountByConfigId(aLong))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void onNext(@NonNull Integer integer) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, integer, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取数目失败"));
                    }
                });
        return liveData;
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
                    List<StockInEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(configId1, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockInEntity entity = list.get(list.size() - 1);
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
                .flatMap((Function<List<StockInEntity>, Publisher<HttpResult<String>>>) list -> {

                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    ArrayList<String> codes = new ArrayList<>();
                    for (StockInEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    HashMap<String, Object> map = new HashMap<>();

                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("productBatchId", configurationEntity.getBatchId());
                    map.put("productBatch", configurationEntity.getBatchName());
                    map.put("productCode", configurationEntity.getProductCode());
                    map.put("productId", configurationEntity.getProductId());
                    map.put("productClassifyId", configurationEntity.getProductClassifyId());
                    map.put("productClassifyName", configurationEntity.getProductClassifyName());
                    map.put("productName", configurationEntity.getProductName());
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
                                .postStockInCodeV3List(map).blockingSingle();
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

                    List<StockInEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockInEntity entity = list.get(list.size() - 1);
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
                    List<StockInEntity> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
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
                    map.put("productBatchId", config.getBatchId());
                    map.put("productBatch", config.getBatchName());
                    map.put("productCode", config.getProductCode());
                    map.put("productId", config.getProductId());
                    map.put("productClassifyId", config.getProductClassifyId());
                    map.put("productClassifyName", config.getProductClassifyName());
                    map.put("productName", config.getProductName());
                    map.put("remark", config.getRemark());
                    map.put("wareHouseId", config.getWareHouseId());
                    map.put("wareHouseName", config.getWareHouseName());
                    map.put("wareHouseCode", config.getWareHouseCode());
                    map.put("storeHouseId", config.getStockHouseId());
                    map.put("storeHouseName", config.getStockHouseName());

                    List<StockInEntity> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (StockInEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockInCodeV3List(map).blockingSingle();
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

    public LiveData<Resource<List<StockInEntity>>> getRefreshListData(Long configId) {
        return loadList(configId,1);
    }

    public LiveData<Resource<List<StockInEntity>>> loadList(Long configId, int mCurrentPage) {
        MutableLiveData<Resource<List<StockInEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> mOperator.queryPageDataByConfigId(id, mCurrentPage, PAGE_CAPACITY))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockInEntity>>() {
                    @Override
                    public void onNext(@NonNull List<StockInEntity> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "加载更多失败"));
                    }
                });
        return liveData;
    }

    public StockInEntity getCodeData(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public void deleteAllByConfigId(Long configId) {
        mOperator.deleteAllByConfigId(configId);
    }

    public void deleteAll() {
        mOperator.deleteAll();
    }

    public boolean putData(StockInEntity entity) {
        return mOperator.putData(entity) > 0;
    }

    public long getVerifyingId(Long configId) {
        return mOperator.queryVerifyingDataByConfigId(configId);
    }

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

}
