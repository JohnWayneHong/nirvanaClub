package com.jgw.delingha.custom_module.wanwei.stock_return.model;

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
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.StockReturnPendingConfigurationBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WanWeiStockReturnEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.CustomerOperator;
import com.jgw.delingha.sql.operator.StorePlaceOperator;
import com.jgw.delingha.sql.operator.WanWeiStockReturnOperator;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Cxz
 * data : 2019/11/25
 * description :
 */
public class WanWeiStockReturnV3CodeModel {

    private final int mUploadGroupCount = 500;

    private final CustomerOperator customerOperator;
    private final WanWeiStockReturnOperator mOperator;
    private final WareHouseOperator wareHouseOperator;
    private final StorePlaceOperator storePlaceOperator;
    private final ConfigurationOperator configurationOperator;

    public WanWeiStockReturnV3CodeModel() {
        configurationOperator = new ConfigurationOperator();
        customerOperator = new CustomerOperator();
        mOperator = new WanWeiStockReturnOperator();
        wareHouseOperator = new WareHouseOperator();
        storePlaceOperator = new StorePlaceOperator();
    }

    public int success;
    public int error;

    private ValueKeeperLiveData<Resource<CodeBean>> mCheckCodeInfoLiveData;

    public LiveData<Resource<CodeBean>> getWanWeiStockReturnCode(checkCodeParamsBean bean) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("outerCodeId", bean.getOuterCodeId());
        map.put("checkType", bean.getCheckType());
        if (!TextUtils.isEmpty(bean.getOutCustomerId())) {
            map.put("customerId", bean.getOutCustomerId());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkWanWeiStockReturnCode(map)
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
                    WanWeiStockReturnEntity entity = mOperator.queryEntityByCode(bean1.getCode());
                    if (entity != null) {
                        entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
                        entity.setSingleNumber(bean1.getSingleNumber());
                        mOperator.putData(entity);
                    }
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
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(bean.getOuterCodeId()), e.getMessage()));
                        } else {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(bean.getOuterCodeId()), ""));
                        }
                    }
                });
        return mCheckCodeInfoLiveData;
    }

    public LiveData<Resource<ConfigurationEntity>> getConfigurationEntityData(Long configId) {
        MutableLiveData<Resource<ConfigurationEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> configurationOperator.queryDataById(configId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ConfigurationEntity>() {
                    @Override
                    public void onNext(ConfigurationEntity configurationEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, configurationEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Boolean>> hasWaitUpload() {
        MutableLiveData<Resource<Boolean>> liveData = new ValueKeeperLiveData<>();
        Observable.just(1)
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
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<CustomerEntity>> getCustomerInfo(Long customerId) {
        MutableLiveData<Resource<CustomerEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(customerId)
                .map(aLong -> customerOperator.queryDataById(customerId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<CustomerEntity>() {
                    @Override
                    public void onNext(CustomerEntity customerEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, customerEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取客户信息失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouseData(Long id) {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
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
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "收货仓库获取失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<StorePlaceEntity>> getStorePlaceData(Long id) {
        MutableLiveData<Resource<StorePlaceEntity>> liveData = new ValueKeeperLiveData<>();
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
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "库位名称获取失败"));
                    }
                });
        return liveData;
    }


    public long configId(ConfigurationEntity entity) {
        return configurationOperator.putData(entity);
    }

    public LiveData<Resource<Map<String, Long>>> getCountTotal(long configId) {
        MutableLiveData<Resource<Map<String, Long>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(id -> {
                    if (id == -1) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, new HashMap<>(), ""));
                    }
                    return id != -1;
                })
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

    public WanWeiStockReturnEntity getWanWeiStockReturnEntity(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public long getWanWeiStockReturnId(WanWeiStockReturnEntity entity) {
        return mOperator.putData(entity);
    }


    public void updateCodeStatus(String code, int status) {
        mOperator.updateCodeStatusByCode(code, status);
    }


    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public LiveData<Resource<List<WanWeiStockReturnEntity>>> getRefreshListData(Long configId) {
        MutableLiveData<Resource<List<WanWeiStockReturnEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(aLong, 1, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<WanWeiStockReturnEntity>>() {
                    @Override
                    public void onNext(List<WanWeiStockReturnEntity> codeBeans) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, codeBeans, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取扫码数据失败"));
                    }
                });
        return liveData;
    }

    public void deleteData(long configId) {
        mOperator.deleteAllByConfigId(configId);
    }

    public boolean getVerifyingId(Long configId) {
        long count = mOperator.queryVerifyingDataByConfigId(configId);
        return count > 0;
    }

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configurationId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        ConfigurationEntity configurationEntity = configurationOperator.queryDataById(configurationId);


        Flowable.just(configurationId)
                .map((Function<Long, List<Long>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;
                    List<WanWeiStockReturnEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            WanWeiStockReturnEntity entity = list.get(list.size() - 1);
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(aLong -> mOperator.queryGroupDataByConfigId(configurationId, aLong, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<WanWeiStockReturnEntity>, Publisher<HttpResult<String>>>) list -> {
                    ArrayList<String> codes = new ArrayList<>();
                    for (WanWeiStockReturnEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    //noinspection ConstantConditions
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("inGoodsId", "");
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("inWareHouseId", configurationEntity.getWareHouseId());
                    map.put("inWareHouseName", configurationEntity.getWareHouseName());
                    map.put("inWareHouseCode", configurationEntity.getWareHouseCode());
                    String customerCode = configurationEntity.getCustomerCode();
                    if (!TextUtils.isEmpty(customerCode)) {
                        map.put("returnCustomerCode", customerCode);
                    }
                    String customerId = configurationEntity.getCustomerId();
                    if (!TextUtils.isEmpty(customerId)) {
                        map.put("returnCustomerId", customerId);
                    }
                    String customerName = configurationEntity.getCustomerName();
                    if (!TextUtils.isEmpty(customerName)) {
                        map.put("returnCustomerName", customerName);
                    }
                    map.put("houseList", "");
                    if (!TextUtils.isEmpty(configurationEntity.getStockHouseId())) {
                        map.put("inStoreHouseId", configurationEntity.getStockHouseId());
                    }
                    if (!TextUtils.isEmpty(configurationEntity.getStockHouseName())) {
                        map.put("inStoreHouseName", configurationEntity.getStockHouseName());
                    }
                    map.put("taskId", taskId);
                    map.put("outerCodeIdList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postWanWeiStockReturnCodeV3List(map).blockingSingle();
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
                .flatMap((Function<Long, Publisher<ConfigurationEntity>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;

                    List<WanWeiStockReturnEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            WanWeiStockReturnEntity entity = list.get(list.size() - 1);
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                ConfigurationEntity entity = configurationOperator.queryDataById(aLong);
                                //noinspection ConstantConditions
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<ConfigurationEntity, Publisher<HttpResult<String>>>) configurationEntity -> {
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("inGoodsId", "");
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("inWareHouseId", configurationEntity.getWareHouseId());
                    map.put("inWareHouseName", configurationEntity.getWareHouseName());
                    map.put("inWareHouseCode", configurationEntity.getWareHouseCode());
                    String customerCode = configurationEntity.getCustomerCode();
                    if (!TextUtils.isEmpty(customerCode)) {
                        map.put("returnCustomerCode", customerCode);
                    }
                    String customerId = configurationEntity.getCustomerId();
                    if (!TextUtils.isEmpty(customerId)) {
                        map.put("returnCustomerId", customerId);
                    }
                    String customerName = configurationEntity.getCustomerName();
                    if (!TextUtils.isEmpty(customerName)) {
                        map.put("returnCustomerName", customerName);
                    }
                    map.put("houseList", "");
                    if (!TextUtils.isEmpty(configurationEntity.getStockHouseId())) {
                        map.put("inStoreHouseId", configurationEntity.getStockHouseId());
                    }
                    if (!TextUtils.isEmpty(configurationEntity.getStockHouseName())) {
                        map.put("inStoreHouseName", configurationEntity.getStockHouseName());
                    }
                    map.put("secondCodeFailures", JsonUtils.toJsonString(new ArrayList<>()));

                    List<WanWeiStockReturnEntity> list = mOperator.queryGroupDataByConfigId(configurationEntity.getId(), configurationEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (WanWeiStockReturnEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);

                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postWanWeiStockReturnCodeV3List(map).blockingSingle();
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

    public LiveData<Resource<List<StockReturnPendingConfigurationBean>>> getLoadList() {
        MutableLiveData<Resource<List<StockReturnPendingConfigurationBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) s -> Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .map(configurationOperator::queryDataById)
                .map(entity -> {
                    StockReturnPendingConfigurationBean bean = new StockReturnPendingConfigurationBean();
                    bean.dataTime = entity.getDataTime();
                    bean.warehouse = entity.getWareHouseName();
                    bean.customer = entity.getCustomerName();
                    bean.id = entity.getId();
                    return bean;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StockReturnPendingConfigurationBean>() {
                    final List<StockReturnPendingConfigurationBean> list = new ArrayList<>();

                    @Override
                    public void onNext(StockReturnPendingConfigurationBean bean) {
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

    public void deleteDataBySelect(List<StockReturnPendingConfigurationBean> beanList) {
        for (StockReturnPendingConfigurationBean bean : beanList) {
            if (bean.isSelect) {
                deleteData(bean.id);
                configurationOperator.removeData(bean.id);
            }
        }
    }


    public LiveData<Resource<List<WanWeiStockReturnEntity>>> loadMoreList(long configId, int pageSize, int page) {
        MutableLiveData<Resource<List<WanWeiStockReturnEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<WanWeiStockReturnEntity> list = mOperator.queryPageDataByConfigId(configId, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(id, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<WanWeiStockReturnEntity>>() {
                    @Override
                    public void onNext(List<WanWeiStockReturnEntity> list) {
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
