package com.jgw.delingha.module.exchange_warehouse.model;

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
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.ExchangeWarehouseConfigurationPendingBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ExchangeWarehouseConfigurationEntity;
import com.jgw.delingha.sql.entity.ExchangeWarehouseEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.ExchangeWarehouseConfigurationOperator;
import com.jgw.delingha.sql.operator.ExchangeWarehouseOperator;
import com.jgw.delingha.sql.operator.StorePlaceOperator;
import com.jgw.delingha.sql.operator.WareHouseOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ExchangeWarehouseModel {
    private final static int CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;//展示list最大展示量
    private final static int mUploadGroupCount = 500;
    private final ExchangeWarehouseConfigurationOperator exchangeWarehouseConfigurationOperator;
    private final ExchangeWarehouseOperator mOperator;
    private final WareHouseOperator wareHouseOperator;
    private final StorePlaceOperator storePlaceOperator;

    public ExchangeWarehouseModel() {
        exchangeWarehouseConfigurationOperator = new ExchangeWarehouseConfigurationOperator();
        mOperator = new ExchangeWarehouseOperator();
        wareHouseOperator = new WareHouseOperator();
        storePlaceOperator = new StorePlaceOperator();
    }

    public LiveData<Resource<Boolean>> hasWaitUpload() {
        MutableLiveData<Resource<Boolean>> liveData = new ValueKeeperLiveData<>();
        Observable.just(0)
                .map(integer -> mOperator.queryAllConfigIdList().size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aBoolean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getCallOutWarehouse(long id) {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
        if (id == -1) {
            liveData.postValue(new Resource<>(Resource.ERROR, null, "调出仓库获取失败"));
        }
        Observable.just(id)
                .map(aLong -> wareHouseOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<WareHouseEntity>() {
                    @Override
                    public void onNext(WareHouseEntity wareHouseEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, wareHouseEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "调出仓库获取失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getCallInWarehouse(long id) {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
        if (id == -1) {
            liveData.postValue(new Resource<>(Resource.ERROR, null, "收货仓库获取失败"));
        }
        Observable.just(id)
                .map(aLong -> wareHouseOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<WareHouseEntity>() {
                    @Override
                    public void onNext(WareHouseEntity wareHouseEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, wareHouseEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "收货仓库获取失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<StorePlaceEntity>> getStoreHouse(Long id) {
        MutableLiveData<Resource<StorePlaceEntity>> liveData = new ValueKeeperLiveData<>();
        if (id == -1) {
            liveData.postValue(new Resource<>(Resource.ERROR, null, "存放库位获取失败"));
        }
        Observable.just(id)
                .map(aLong -> storePlaceOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StorePlaceEntity>() {
                    @Override
                    public void onNext(StorePlaceEntity storePlaceEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, storePlaceEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "存放库位获取失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> saveConfig(ExchangeWarehouseConfigurationEntity entity) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    entity1.setCreateTime(System.currentTimeMillis());
                    entity1.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
                    return exchangeWarehouseConfigurationOperator.putData(entity1);
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
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "保存设置信息失败"));
                    }
                });
        return liveData;
    }

    //*********************************************上面是Setting界面涉及到的Model**********************************************//

    public LiveData<Resource<ExchangeWarehouseConfigurationEntity>> getConfig(long id) {
        MutableLiveData<Resource<ExchangeWarehouseConfigurationEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> exchangeWarehouseConfigurationOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ExchangeWarehouseConfigurationEntity>() {
                    @Override
                    public void onNext(ExchangeWarehouseConfigurationEntity exchangeWarehouseConfigurationEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, exchangeWarehouseConfigurationEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取调仓配置失败"));
                    }
                });
        return liveData;
    }


    public LiveData<Resource<Map<String, Long>>> getCount(Long configId) {
        MutableLiveData<Resource<Map<String, Long>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map((Function<Long, Map<String, Long>>) aLong -> {
                    long codeNumber = mOperator.queryCountByConfigId(aLong);
                    long singleNumber = mOperator.querySingleNumberByConfigId(aLong);
                    HashMap<String, Long> map = new HashMap<>();
                    map.put("codeNumber", codeNumber);
                    map.put("singleNumber", singleNumber);
                    return map;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Map<String, Long>>() {
                    @Override
                    public void onNext(@NonNull Map<String, Long> map) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, map, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public ExchangeWarehouseEntity getExchangeWarehouseEntity(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean getExchangeWarehouseId(ExchangeWarehouseEntity entity) {
        return mOperator.putData(entity) > 0;
    }

    private ValueKeeperLiveData<Resource<CodeBean>> mCheckCodeInfoLiveData;

    public LiveData<Resource<CodeBean>> getCheckCode(checkCodeParamsBean bean) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("wareHouseId", bean.getWareHouseId());
        map.put("outerCodeId", bean.getOuterCodeId());
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getWanWeiCheckStockCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .map(result -> {
                    JsonObject jb = JsonUtils.parseObject(result);
                    int singleNumber = jb.getInt("singleNumber");
                    String outerCodeId = jb.getString("outerCodeId");
                    CodeBean codeBean = new CodeBean(outerCodeId);
                    codeBean.singleNumber = singleNumber;
                    return codeBean;
                })
                .map(bean1 -> {
                    ExchangeWarehouseEntity entity = mOperator.queryEntityByCode(bean1.getCode());
                    entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
                    entity.setSingleNumber(bean1.getSingleNumber());
                    mOperator.putData(entity);
                    return bean1;
                })
                .subscribe(new CustomObserver<CodeBean>() {
                    @Override
                    public void onNext(CodeBean s) {
                        mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(bean.getOuterCodeId()), ""));
                            return;
                        }
                        mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(bean.getOuterCodeId()), e.getMessage()));
                    }
                });
        return mCheckCodeInfoLiveData;
    }

    public void updateCodeStatusByCode(String code, int status) {
        mOperator.updateCodeStatusByCode(code, status);
    }

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public LiveData<Resource<List<ExchangeWarehouseEntity>>> getRefreshListData(Long configId) {
        MutableLiveData<Resource<List<ExchangeWarehouseEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(configId, CAPACITY))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<ExchangeWarehouseEntity>>() {
                    @Override
                    public void onNext(List<ExchangeWarehouseEntity> goodsEntities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, goodsEntities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "反扫更新失败"));
                    }
                });
        return liveData;
    }

    public int success;
    public int error;

    //*********************************************上面是主界面涉及到的Model**********************************************//

    public LiveData<Resource<List<ExchangeWarehouseConfigurationPendingBean>>> getBeanList() {
        MutableLiveData<Resource<List<ExchangeWarehouseConfigurationPendingBean>>> liveData = new ValueKeeperLiveData<>();

        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) s -> Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .map(exchangeWarehouseConfigurationOperator::queryDataById)
                .map(entity -> {
                    ExchangeWarehouseConfigurationPendingBean bean = new ExchangeWarehouseConfigurationPendingBean();
                    bean.dataTime = entity.getDataTime();
                    bean.outWarehouse = entity.getDisplayOutWarehouse();
                    bean.inWarehouse = entity.getDisplayInWarehouse();
                    bean.id = entity.getId();
                    return bean;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ExchangeWarehouseConfigurationPendingBean>() {
                    final List<ExchangeWarehouseConfigurationPendingBean> list = new ArrayList<>();

                    @Override
                    public void onNext(ExchangeWarehouseConfigurationPendingBean bean) {
                        list.add(bean);
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
                    }
                });
        return liveData;
    }

    public void deleteDataBySelect(List<ExchangeWarehouseConfigurationPendingBean> mList) {
        for (ExchangeWarehouseConfigurationPendingBean bean : mList) {
            if (bean.isSelect) {
                mOperator.deleteAllByConfigId(bean.id);
                exchangeWarehouseConfigurationOperator.removeData(bean.id);
            }
        }
    }

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(Long configurationId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        ExchangeWarehouseConfigurationEntity configurationEntity = exchangeWarehouseConfigurationOperator.queryDataById(configurationId);


        Flowable.just(configurationId)
                .map((Function<Long, List<Long>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;
                    List<ExchangeWarehouseEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigId(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            ExchangeWarehouseEntity entity = list.get(list.size() - 1);
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(aLong -> mOperator.queryDataByConfigId(configurationId, aLong, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<ExchangeWarehouseEntity>, Publisher<HttpResult<String>>>) list -> {
                    ArrayList<String> codes = new ArrayList<>();
                    for (ExchangeWarehouseEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("houseList", "");
                    map.put("inStoreHouseId", configurationEntity.getInStoreHouseId());
                    map.put("inStoreHouseName", configurationEntity.getInStoreHouseName());
                    map.put("inWareHouseId", configurationEntity.getInWareHouseId());
                    map.put("inWareHouseName", configurationEntity.getInWareHouseName());
                    map.put("inWareHouseCode", configurationEntity.getInWareHouseCode());
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("outWareHouseId", configurationEntity.getOutWareHouseId());
                    map.put("outWareHouseName", configurationEntity.getOutWareHouseName());
                    map.put("outWareHouseCode", configurationEntity.getOutWareHouseCode());
                    map.put("outerCodeIdList", codes);
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .uploadExchangeWarehouse(map).blockingSingle();
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
                    public void onSubscribe(Subscription s) {
                        this.subscription = s;
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(HttpResult<String> stringHttpResult) {
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
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
                .flatMap((Function<Long, Publisher<ExchangeWarehouseConfigurationEntity>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;

                    List<ExchangeWarehouseEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigId(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            ExchangeWarehouseEntity entity = list.get(list.size() - 1);
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                ExchangeWarehouseConfigurationEntity entity = exchangeWarehouseConfigurationOperator.queryDataById(aLong);
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<ExchangeWarehouseConfigurationEntity, Publisher<HttpResult<String>>>) configurationEntity -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("houseList", "");
                    map.put("inStoreHouseId", configurationEntity.getInStoreHouseId());
                    map.put("inStoreHouseName", configurationEntity.getInStoreHouseName());
                    map.put("inWareHouseId", configurationEntity.getInWareHouseId());
                    map.put("inWareHouseName", configurationEntity.getInWareHouseName());
                    map.put("inWareHouseCode", configurationEntity.getInWareHouseCode());
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("outWareHouseId", configurationEntity.getOutWareHouseId());
                    map.put("outWareHouseName", configurationEntity.getOutWareHouseName());
                    map.put("outWareHouseCode", configurationEntity.getOutWareHouseCode());

                    List<ExchangeWarehouseEntity> list = mOperator.queryDataByConfigId(configurationEntity.getId(), configurationEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (ExchangeWarehouseEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .uploadExchangeWarehouse(map).blockingSingle();
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
                    public void onSubscribe(Subscription s) {
                        this.subscription = s;
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(HttpResult<String> stringHttpResult) {
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, new UploadResultBean(success, error), ""));
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, new UploadResultBean(success, error), ""));
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
                    ExchangeWarehouseConfigurationEntity entity = exchangeWarehouseConfigurationOperator.queryDataById(aLong);
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
                            exchangeWarehouseConfigurationOperator.putData(entity);
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

    public LiveData<Resource<List<ExchangeWarehouseEntity>>> loadMoreList(long configId, int page, int pageSize) {
        MutableLiveData<Resource<List<ExchangeWarehouseEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<ExchangeWarehouseEntity> list = mOperator.queryPageDataByConfigId(aLong, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(id, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<ExchangeWarehouseEntity>>() {
                    @Override
                    public void onNext(List<ExchangeWarehouseEntity> list) {
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
