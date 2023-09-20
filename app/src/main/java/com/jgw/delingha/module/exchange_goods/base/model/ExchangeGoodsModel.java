package com.jgw.delingha.module.exchange_goods.base.model;


import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.delingha.bean.ExchangeGoodsPendingConfigurationBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsConfigurationEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsEntity;
import com.jgw.delingha.sql.operator.CustomerOperator;
import com.jgw.delingha.sql.operator.ExchangeGoodsConfigurationOperator;
import com.jgw.delingha.sql.operator.ExchangeGoodsOperator;

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

public class ExchangeGoodsModel {

    private final static int CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;//展示list最大展示量
    private final static int mUploadGroupCount = 500;
    private final CustomerOperator customerOperator;
    private final ExchangeGoodsConfigurationOperator exchangeGoodsConfigurationOperator;
    private final ExchangeGoodsOperator mOperator;

    public ExchangeGoodsModel() {
        exchangeGoodsConfigurationOperator = new ExchangeGoodsConfigurationOperator();
        mOperator = new ExchangeGoodsOperator();
        customerOperator = new CustomerOperator();
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

    public LiveData<Resource<CustomerEntity>> getCallOutCustomer(long id) {
        MutableLiveData<Resource<CustomerEntity>> liveData = new ValueKeeperLiveData<>();
        if (id == -1) {
            liveData.postValue(new Resource<>(Resource.ERROR, null, "获取客户信息失败"));
        }
        Observable.just(id)
                .map(aLong -> customerOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<CustomerEntity>() {
                    @Override
                    public void onNext(CustomerEntity customerEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, customerEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取客户信息失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<CustomerEntity>> getCallInCustomer(long id) {
        MutableLiveData<Resource<CustomerEntity>> liveData = new ValueKeeperLiveData<>();
        if (id == -1) {
            liveData.postValue(new Resource<>(Resource.ERROR, null, "获取客户信息失败"));
        }
        Observable.just(id)
                .map(aLong -> customerOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<CustomerEntity>() {
                    @Override
                    public void onNext(CustomerEntity customerEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, customerEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取客户信息失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> saveConfig(ExchangeGoodsConfigurationEntity entity) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    entity1.setCreateTime(System.currentTimeMillis());
                    entity1.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
                    return exchangeGoodsConfigurationOperator.putData(entity1);
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

    public LiveData<Resource<ExchangeGoodsConfigurationEntity>> getConfig(long id) {
        MutableLiveData<Resource<ExchangeGoodsConfigurationEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> exchangeGoodsConfigurationOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ExchangeGoodsConfigurationEntity>() {
                    @Override
                    public void onNext(ExchangeGoodsConfigurationEntity exchangeGoodsConfigurationEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, exchangeGoodsConfigurationEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取调货配置失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> getCountByConfigId(long configId) {
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
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取数目失败"));
                    }
                });
        return liveData;
    }

    public ExchangeGoodsEntity getExchangeGoodsEntity(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean getExchangeGoodsId(ExchangeGoodsEntity entity) {
        return mOperator.putData(entity) > 0;
    }

    private ValueKeeperLiveData<Resource<String>> mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();

    public LiveData<Resource<String>> getCheckCode(checkCodeParamsBean bean) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("outCustomerId", bean.getOutCustomerId());
        map.put("outerCodeId", bean.getOuterCodeId());
        map.put("checkType", bean.getCheckType());
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkExchangeOuterCodeId(map)
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
                        mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, bean.getOuterCodeId(), e.getMessage()));
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

    public LiveData<Resource<List<ExchangeGoodsEntity>>> getRefreshListData(Long configId) {
        MutableLiveData<Resource<List<ExchangeGoodsEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(configId, 1, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<ExchangeGoodsEntity>>() {
                    @Override
                    public void onNext(List<ExchangeGoodsEntity> goodsEntities) {
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

    //****************************************************上面是主界面涉及到的model************************************//

    public LiveData<Resource<List<ExchangeGoodsPendingConfigurationBean>>> getBeanList() {
        MutableLiveData<Resource<List<ExchangeGoodsPendingConfigurationBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) s -> Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .map(exchangeGoodsConfigurationOperator::queryDataById)
                .map(entity -> {
                    ExchangeGoodsPendingConfigurationBean bean = new ExchangeGoodsPendingConfigurationBean();
                    bean.dataTime = entity.getDataTime();
                    bean.outCustomer = entity.getDisplayOutGoods();
                    bean.inCustomer = entity.getDisplayInGoods();
                    bean.id = entity.getId();
                    return bean;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ExchangeGoodsPendingConfigurationBean>() {
                    final List<ExchangeGoodsPendingConfigurationBean> list = new ArrayList<>();

                    @Override
                    public void onNext(ExchangeGoodsPendingConfigurationBean bean) {
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

    public void deleteDataBySelect(List<ExchangeGoodsPendingConfigurationBean> beanList) {
        for (ExchangeGoodsPendingConfigurationBean bean : beanList) {
            if (bean.isSelect) {
                mOperator.deleteAllByConfigId(bean.id);
                exchangeGoodsConfigurationOperator.removeData(bean.id);
            }
        }
    }

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(Long configurationId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        ExchangeGoodsConfigurationEntity configurationEntity = exchangeGoodsConfigurationOperator.queryDataById(configurationId);


        Flowable.just(configurationId)
                .map((Function<Long, List<Long>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;
                    List<ExchangeGoodsEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigIdV2(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            ExchangeGoodsEntity entity = list.get(list.size() - 1);
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(aLong -> mOperator.queryDataByConfigIdV2(configurationId, aLong, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<ExchangeGoodsEntity>, Publisher<HttpResult<String>>>) list -> {
                    ArrayList<String> codes = new ArrayList<>();
                    for (ExchangeGoodsEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    String taskId = configurationEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("houseList", "");
                    map.put("wareGoodsInCode", configurationEntity.getWareGoodsInCode());
                    map.put("wareGoodsInId", configurationEntity.getWareGoodsInId());
                    map.put("wareGoodsInName", configurationEntity.getWareGoodsInName());
                    map.put("wareGoodsOutCode", configurationEntity.getWareGoodsOutCode());
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("wareGoodsOutId", configurationEntity.getWareGoodsOutId());
                    map.put("wareGoodsOutName", configurationEntity.getWareGoodsOutName());
                    map.put("taskId", taskId);
                    map.put("outerCodeIdList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .uploadExchangeGoods(map).blockingSingle();
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
                .flatMap((Function<Long, Publisher<ExchangeGoodsConfigurationEntity>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;

                    List<ExchangeGoodsEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigIdV2(aLong, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            ExchangeGoodsEntity entity = list.get(list.size() - 1);
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                ExchangeGoodsConfigurationEntity entity = exchangeGoodsConfigurationOperator.queryDataById(aLong);
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryDataByConfigIdV2(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<ExchangeGoodsConfigurationEntity, Publisher<HttpResult<String>>>) configurationEntity -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("houseList", "");
                    map.put("wareGoodsInCode", configurationEntity.getWareGoodsInCode());
                    map.put("wareGoodsInId", configurationEntity.getWareGoodsInId());
                    map.put("wareGoodsInName", configurationEntity.getWareGoodsInName());
                    map.put("wareGoodsOutCode", configurationEntity.getWareGoodsOutCode());
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("wareGoodsOutId", configurationEntity.getWareGoodsOutId());
                    map.put("wareGoodsOutName", configurationEntity.getWareGoodsOutName());

                    List<ExchangeGoodsEntity> list = mOperator.queryDataByConfigIdV2(configurationEntity.getId(), configurationEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (ExchangeGoodsEntity soe : list) {
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
                                .uploadExchangeGoods(map).blockingSingle();
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
                    ExchangeGoodsConfigurationEntity entity = exchangeGoodsConfigurationOperator.queryDataById(aLong);
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
                            exchangeGoodsConfigurationOperator.putData(entity);
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

    public LiveData<Resource<List<ExchangeGoodsEntity>>> loadMoreList(long configId, int page, int pageSize) {
        MutableLiveData<Resource<List<ExchangeGoodsEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<ExchangeGoodsEntity> list = mOperator.queryPageDataByConfigId(aLong, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(id, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<ExchangeGoodsEntity>>() {
                    @Override
                    public void onNext(List<ExchangeGoodsEntity> list) {
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
