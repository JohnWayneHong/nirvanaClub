package com.jgw.delingha.module.stock_return.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.StockReturnPendingConfigurationBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockReturnEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.StockReturnOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Cxz
 * data : 2019/11/25
 * description :
 */
public class StockReturnV3CodeModel {

    private final int mUploadGroupCount = 500;

    private final StockReturnOperator mOperator;
    private final ConfigurationOperator configurationOperator;

    public StockReturnV3CodeModel() {
        configurationOperator = new ConfigurationOperator();
        mOperator = new StockReturnOperator();
    }

    public int success;
    public int error;

    private MutableLiveData<Resource<String>> mCheckCodeInfoLiveData;

    public LiveData<Resource<String>> getStockReturnCode(checkCodeParamsBean bean) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("outerCodeId", bean.getOuterCodeId());
        if (!TextUtils.isEmpty(bean.getOutCustomerId())) {
            map.put("customerId", bean.getOutCustomerId());
        }
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkStockReturnCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, bean.getOuterCodeId(), ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, bean.getOuterCodeId(), ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, bean.getOuterCodeId(), e.getMessage()));
                        } else {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, bean.getOuterCodeId(), e.getMessage()));
                            super.onError(e);
                        }

                    }
                });
        return mCheckCodeInfoLiveData;
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

    public LiveData<Resource<Long>> getCountTotal(Long configId) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mOperator::queryCountByConfigId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aLong, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public StockReturnEntity getStockReturnEntity(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean getStockReturnId(StockReturnEntity entity) {
        return mOperator.putData(entity)>0;
    }

    public void updateCodeStatus(String code, int status) {
        mOperator.updateCodeStatusByCode(code, status);
    }

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public LiveData<Resource<List<StockReturnEntity>>> getRefreshListData(Long configId) {
        return loadList(configId, 1, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
    }

    public void deleteData(long configId) {
        mOperator.deleteAllByConfigId(configId);
    }

    public boolean hasVerifyingCode(Long configId) {
        long count = mOperator.queryVerifyingDataByConfigId(configId);
        return count>0;
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
                    List<StockReturnEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockReturnEntity entity = list.get(list.size() - 1);
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
                .flatMap((Function<List<StockReturnEntity>, Publisher<HttpResult<String>>>) list -> {
                    ArrayList<String> codes = new ArrayList<>();
                    for (StockReturnEntity soe : list) {
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
                                .postStockReturnCodeV3List(map).blockingSingle();
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

                    List<StockReturnEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockReturnEntity entity = list.get(list.size() - 1);
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

                    List<StockReturnEntity> list = mOperator.queryGroupDataByConfigId(configurationEntity.getId(), configurationEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (StockReturnEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);

                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockReturnCodeV3List(map).blockingSingle();
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
        List<Long> configurationIdList = mOperator.queryAllConfigIdList();
        Observable.just(configurationIdList)
                .map(list -> {
                    long userId = LocalUserUtils.getCurrentUserId();
                    List<StockReturnPendingConfigurationBean> beanList = new ArrayList<>();
                    for (Long id : configurationIdList) {
                        ConfigurationEntity entity = configurationOperator.queryDataById(id);
                        if (entity != null && entity.getUserEntity().getTargetId() == userId) {
                            StockReturnPendingConfigurationBean bean = new StockReturnPendingConfigurationBean();
                            bean.dataTime = entity.getDataTime();
                            bean.warehouse = entity.getWareHouseName();
                            bean.customer = entity.getCustomerName();
                            bean.id = entity.getId();
                            beanList.add(bean);
                        }
                    }
                    return beanList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockReturnPendingConfigurationBean>>() {
                    @Override
                    public void onNext(List<StockReturnPendingConfigurationBean> stockReturnPendingConfigurationBeans) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, stockReturnPendingConfigurationBeans, ""));
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
                mOperator.deleteAllByConfigId(bean.id);
                configurationOperator.removeData(bean.id);
            }
        }
    }

    public LiveData<Resource<List<StockReturnEntity>>> loadList(long configId, int page, int pageSize) {
        MutableLiveData<Resource<List<StockReturnEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<StockReturnEntity> list = mOperator.queryPageDataByConfigId(aLong, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(id, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockReturnEntity>>() {
                    @Override
                    public void onNext(List<StockReturnEntity> list) {
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
