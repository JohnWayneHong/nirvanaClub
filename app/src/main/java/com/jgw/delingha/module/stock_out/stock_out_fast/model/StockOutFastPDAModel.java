package com.jgw.delingha.module.stock_out.stock_out_fast.model;

import android.text.TextUtils;

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
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.StockOutFastWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockOutFastEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.StockOutFastOperator;

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

public class StockOutFastPDAModel {

    private final StockOutFastOperator mOperator;
    private final ConfigurationOperator mConfigOperator;
    private final int mUploadGroupCount = 500;

    public StockOutFastPDAModel() {
        mOperator = new StockOutFastOperator();
        mConfigOperator = new ConfigurationOperator();
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


    public LiveData<Resource<List<StockOutFastEntity>>> loadMoreList(long configId, int pageSize, int page) {
        MutableLiveData<Resource<List<StockOutFastEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<StockOutFastEntity> list = mOperator.queryPageDataByConfigId(configId, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(id, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockOutFastEntity>>() {
                    @Override
                    public void onNext(List<StockOutFastEntity> list) {
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

    public LiveData<Resource<Map<String, Long>>> getCalculationTotal(long configId) {
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
                    public void onNext(Map<String, Long> size) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, size, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public StockOutFastEntity queryEntityByCode(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean putData(StockOutFastEntity entity) {
        return mOperator.putData(entity)>0;
    }

    private ValueKeeperLiveData<Resource<CodeBean>> mCheckCodeInfoLiveData;

    public LiveData<Resource<CodeBean>> getCheckCodeInfo(String code) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }

        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getCheckStockOutCode(code)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(result -> {
                    JsonObject jb = JsonUtils.parseObject(result);
                    int singleNumber = jb.getInt("singleNumber");
                    String outerCodeId = jb.getString("outerCodeId");
                    CodeBean codeBean = new CodeBean(outerCodeId);
                    codeBean.singleNumber = singleNumber;
                    return codeBean;
                })
                .map(bean1 -> {
                    updateCodeStatus(bean1, CodeBean.STATUS_CODE_SUCCESS);
                    return bean1;
                })
                .subscribe(new CustomObserver<CodeBean>() {
                    @Override
                    public void onNext(CodeBean result) {
                        mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, result, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            updateCodeStatus(new CodeBean(code), CodeBean.STATUS_CODE_FAIL);
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(code), ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(code), e.getMessage()));
                        } else {
                            updateCodeStatus(new CodeBean(code), CodeBean.STATUS_CODE_FAIL);
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(code), ""));
                        }
                    }
                });
        return mCheckCodeInfoLiveData;
    }

    public void updateCodeStatus(CodeBean data, int status) {
        StockOutFastEntity entity = mOperator.queryEntityByCode(data.outerCodeId);
        if (entity == null) {
            return;
        }
        entity.setCodeStatus(status);
        entity.setSingleNumber(data.singleNumber);
        mOperator.putData(entity);
    }

    public LiveData<Resource<List<StockOutFastEntity>>> refreshListByConfigId(Long configId) {
        MutableLiveData<Resource<List<StockOutFastEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(configId, 0, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockOutFastEntity>>() {
                    @Override
                    public void onNext(List<StockOutFastEntity> list) {
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

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    private int success;
    private int error;

    public long queryVerifyingDataByConfigId(long configId) {
        return mOperator.queryVerifyingDataByConfigId(configId);
    }

    public void deleteAllByConfigId(List<Long> configIds) {
        Observable.fromIterable(configIds)
                .map(configId -> {
                    mOperator.deleteAllByConfigId(configId);
                    return "删除成功:configId=" + configId;
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String msg) {
                        LogUtils.xswShowLog(msg);
                    }
                });
    }

    public void deleteAll() {
        mOperator.deleteAll();
    }

    public LiveData<Resource<List<StockOutFastWaitUploadListBean>>> getConfigGroupList() {
        MutableLiveData<Resource<List<StockOutFastWaitUploadListBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) s -> Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .map(mConfigOperator::queryDataById)
                .map(entity -> {
                    StockOutFastWaitUploadListBean bean = new StockOutFastWaitUploadListBean();
                    bean.create_time = entity.getDataTime();
                    bean.product_name = entity.getProductText();
                    bean.receiver = entity.getCustomerText();
                    bean.config_id = entity.getId();
                    return bean;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<StockOutFastWaitUploadListBean>() {

                    final List<StockOutFastWaitUploadListBean> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(StockOutFastWaitUploadListBean StockOutFastWaitUploadListBean) {
                        list.add(StockOutFastWaitUploadListBean);
                    }

                    @Override
                    public void onComplete() {
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

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        LogUtils.showLog("uploadNewCodes:startTime=", System.currentTimeMillis() + "");

        ConfigurationEntity config = mConfigOperator.queryDataById(configId);


        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;
                    List<StockOutFastEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(configId1, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockOutFastEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    LogUtils.xswShowLog("");
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(index -> mOperator.queryGroupDataByConfigId(configId, index, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<StockOutFastEntity>, Publisher<HttpResult<String>>>) list -> {

                    LogUtils.xswShowLog("");
                    ArrayList<String> codes = new ArrayList<>();
                    for (StockOutFastEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    //收货客户
                    map.put("receiveOrganizationCode", config.getCustomerCode());
                    map.put("receiveOrganizationName", config.getCustomerName());
                    map.put("receiveOrganizationId", config.getCustomerId());
                    //商品
                    map.put("productName", config.getProductName());
                    map.put("productId", config.getProductId());
                    map.put("productCode", config.getProductCode());
                    //批次
                    map.put("productBatchId", config.getBatchId());
                    map.put("productBatchName", config.getBatchName());
                    //物流公司
                    map.put("logisticsCompanyName", TextUtils.isEmpty(config.getLogisticsCompanyName()) ? "" : config.getLogisticsCompanyName());
                    map.put("logisticsCompanyCode", TextUtils.isEmpty(config.getLogisticsCompanyCode()) ? "" : config.getLogisticsCompanyCode());
                    //单号
                    map.put("logisticsNumber", TextUtils.isEmpty(config.getLogisticsNumber()) ? "" : config.getLogisticsNumber());
                    //备注
                    map.put("remark", TextUtils.isEmpty(config.getRemark()) ? "" : config.getRemark());

                    //码集合
                    map.put("list", codes);
                    String taskId = config.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockOutFastCode(map).blockingSingle();
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

    public LiveData<Resource<UploadResultBean>> uploadConfigGroupList(List<Long> configs) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        Flowable.fromIterable(configs)
                .parallel()
                .runOn(Schedulers.io())
                .flatMap((Function<Long, Publisher<ConfigurationEntity>>) configId -> {
                    boolean next = true;
                    long currentId = 0;

                    List<StockOutFastEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryGroupDataByConfigId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockOutFastEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;

                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    LogUtils.xswShowLog("");
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                LogUtils.xswShowLog("");
                                ConfigurationEntity entity = mConfigOperator.queryDataById(configId);
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<ConfigurationEntity, Publisher<HttpResult<String>>>) config -> {

                    LogUtils.xswShowLog("");
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = config.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    //收货客户
                    map.put("receiveOrganizationCode", config.getCustomerCode());
                    map.put("receiveOrganizationName", config.getCustomerName());
                    map.put("receiveOrganizationId", config.getCustomerId());
                    //商品
                    map.put("productName", config.getProductName());
                    map.put("productId", config.getProductId());
                    map.put("productCode", config.getProductCode());
                    //批次
                    map.put("productBatchId", config.getBatchId());
                    map.put("productBatchName", config.getBatchName());
                    //物流公司
                    map.put("logisticsCompanyName", TextUtils.isEmpty(config.getLogisticsCompanyName()) ? "" : config.getLogisticsCompanyName());
                    map.put("logisticsCompanyCode", TextUtils.isEmpty(config.getLogisticsCompanyCode()) ? "" : config.getLogisticsCompanyCode());
                    //单号
                    map.put("logisticsNumber", TextUtils.isEmpty(config.getLogisticsNumber()) ? "" : config.getLogisticsNumber());
                    //备注
                    map.put("remark", TextUtils.isEmpty(config.getRemark()) ? "" : config.getRemark());

                    List<StockOutFastEntity> list = mOperator.queryGroupDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (StockOutFastEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    //码集合
                    map.put("list", codes);

                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockOutFastCode(map).blockingSingle();
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
