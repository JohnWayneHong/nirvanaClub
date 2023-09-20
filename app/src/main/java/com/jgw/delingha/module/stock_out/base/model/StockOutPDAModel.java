package com.jgw.delingha.module.stock_out.base.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.StockOutWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.CodeEntity;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockOutEntity;
import com.jgw.delingha.sql.operator.CodeOperator;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.StockOutOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

public class StockOutPDAModel {
    private final StockOutOperator mOperator;
    private final CodeOperator mCodeOperator;
    private final ConfigurationOperator mConfigOperator;
    private final int mUploadGroupCount = 500;

    public StockOutPDAModel() {
        mOperator = new StockOutOperator();
        mCodeOperator = new CodeOperator();
        mConfigOperator = new ConfigurationOperator();
    }

    public LiveData<Resource<List<CodeEntity>>> getFormatList(long configId) {
        MutableLiveData<Resource<List<CodeEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mOperator::querySuccessDataByConfigId)
                .map(stockOutEntities -> {
                    ArrayList<CodeEntity> list = new ArrayList<>();
                    for (StockOutEntity soe : stockOutEntities) {
                        CodeEntity codeEntity = mCodeOperator.queryEntityByCode(soe.getCode());
                        list.add(codeEntity);
                    }
                    return list;
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Function<ArrayList<CodeEntity>, ObservableSource<CodeEntity>>) Observable::fromIterable)
                .groupBy(CodeEntity::getProductId)
                .flatMapSingle((Function<GroupedObservable<String, CodeEntity>, SingleSource<List<CodeEntity>>>) Observable::toList)
                .map(groupList -> {
                    CodeEntity stockOutEntity = null;
                    for (int i = 0; i < groupList.size(); i++) {
                        if (i == 0) {
                            stockOutEntity = groupList.get(0);
                        }
                        switch (groupList.get(i).getCodeLevel()) {
                            case 1:
                                stockOutEntity.setFirstNumberUnitCount(stockOutEntity.getFirstNumberUnitCount()+1);
                                break;
                            case 2:
                                stockOutEntity.setSecondNumberUnitCount(stockOutEntity.getSecondNumberUnitCount()+1);
                                break;
                            case 3:
                                stockOutEntity.setThirdNumberUnitCount(stockOutEntity.getThirdNumberUnitCount()+1);
                                break;
                        }
                    }
                    return stockOutEntity;
                })
                .subscribe(new CustomObserver<CodeEntity>() {
                    private final List<CodeEntity> list = new ArrayList<>();

                    @Override
                    public void onNext(CodeEntity codeEntities) {
                        list.add(codeEntities);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
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

    public int success;
    public int error;

    public void clearCache() {
//        mCodeOperator.clearCache();
    }

    public LiveData<Resource<List<StockOutWaitUploadListBean>>> getConfigGroupList() {
        MutableLiveData<Resource<List<StockOutWaitUploadListBean>>> liveData = new ValueKeeperLiveData<>();

        List<Long> longs = mOperator.queryAllConfigIdList();

        Observable.fromIterable(longs)
                .map(aLong -> {
                    StockOutWaitUploadListBean bean = new StockOutWaitUploadListBean();
                    ConfigurationEntity entity = mConfigOperator.queryDataById(aLong);
//                    UserEntity userInfo = entity.getUserInfo();
                    bean.create_time = FormatUtils.formatTime(entity.getCreateTime());
                    bean.receiver = entity.getCustomerText();
                    bean.config_id = entity.getId();
                    return bean;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<StockOutWaitUploadListBean>() {

                    private final List<StockOutWaitUploadListBean> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(StockOutWaitUploadListBean stockOutWaitUploadListBean) {
                        list.add(stockOutWaitUploadListBean);
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
        return uploadListByConfigId(configId, 0);
    }

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configId, int filterStatus) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        LogUtils.showLog("uploadNewCodes:startTime=", System.currentTimeMillis() + "");

        ConfigurationEntity configurationEntity = mConfigOperator.queryDataById(configId);


        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;
                    List<StockOutEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigId(configId1, currentId, mUploadGroupCount, filterStatus);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockOutEntity entity = list.get(list.size() - 1);
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
                .map(index -> mOperator.queryDataByConfigId(configId, index, mUploadGroupCount, filterStatus))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<StockOutEntity>, Publisher<HttpResult<String>>>) list -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("receiveOrganizationCode", configurationEntity.getCustomerCode());
                    map.put("receiveOrganizationName", configurationEntity.getCustomerName());
                    map.put("receiveOrganizationId", configurationEntity.getCustomerId());
                    map.put("logisticsCompanyName", configurationEntity.getLogisticsCompanyName());
                    map.put("logisticsCompanyCode", configurationEntity.getLogisticsCompanyCode());
                    map.put("logisticsNumber", configurationEntity.getLogisticsNumber());
                    map.put("linkName", configurationEntity.getContacts());//联系人
                    map.put("remark", configurationEntity.getRemark());
                    LogUtils.xswShowLog("");
                    ArrayList<String> codes = new ArrayList<>();
                    for (StockOutEntity soe : list) {
                        codes.add(soe.getCode());
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
                                .postStockOutCodeV3List(map).blockingSingle();
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

                    List<StockOutEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            StockOutEntity entity = list.get(list.size() - 1);
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
                    List<?> list = mOperator.queryDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
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
                    map.put("receiveOrganizationCode", config.getCustomerCode());
                    map.put("receiveOrganizationName", config.getCustomerName());
                    map.put("receiveOrganizationId", config.getCustomerId());
                    map.put("logisticsCompanyName", config.getLogisticsCompanyName());
                    map.put("logisticsCompanyCode", config.getLogisticsCompanyCode());
                    map.put("logisticsNumber", config.getLogisticsNumber());
                    map.put("remark", config.getRemark());

                    List<StockOutEntity> list = mOperator.queryDataByConfigId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (StockOutEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postStockOutCodeV3List(map).blockingSingle();
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


    private ValueKeeperLiveData<Resource<CodeBean>> mCheckCodeInfoLiveData;

    public LiveData<Resource<CodeBean>> getCheckCodeInfo(String code) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(code)
                .flatMap((Function<String, ObservableSource<CodeBean>>) code1 ->
                        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .getCodeInfo(code1)
                                .compose(HttpUtils.applyIOSchedulers()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<CodeBean>() {
                    @Override
                    public void onNext(CodeBean codeBean) {
                        codeBean.outerCodeId = code;
                        mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, codeBean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(code), ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, new CodeBean(code), e.getMessage()));
                        } else {
                            super.onError(e);
                            mCheckCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, new CodeBean(code), ""));
                        }
                    }
                });
        return mCheckCodeInfoLiveData;
    }

    private ValueKeeperLiveData<Resource<StockOutEntity>> mUpdateCodeInfoLiveData;

    public LiveData<Resource<StockOutEntity>> updateCodeInfo(CodeBean bean, long configId) {
        if (mUpdateCodeInfoLiveData == null) {
            mUpdateCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(bean)
                .filter(codeBean -> {
                    StockOutEntity entity = mOperator.queryEntityByCode(bean.outerCodeId);
                    if (entity == null) {
                        mUpdateCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, null, "更新码状态失败,请重试"));
                    }
                    return entity != null;
                })
                .map(codeBean -> {
                    StockOutEntity entity = mOperator.queryEntityByCode(bean.outerCodeId);
                    entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
                    //更新码表信息
                    entity.getConfigEntity().setTargetId(configId);
                    CodeEntity codeEntity = mCodeOperator.queryEntityByCode(bean.outerCodeId);
                    if (codeEntity == null) {
                        codeEntity = new CodeEntity();
                    }
                    codeEntity.setCode(bean.outerCodeId);
                    codeEntity.setProductId(bean.productId);
                    codeEntity.setProductName(bean.productName);
                    codeEntity.setProductCode(bean.productCode);
                    codeEntity.setThirdNumberUnitName(bean.thirdNumberUnitName);
                    codeEntity.setSecondNumberUnitName(bean.secondNumberUnitName);
                    codeEntity.setFirstNumberUnitName(bean.firstNumberUnitName);
                    codeEntity.setFirstNumberUnitCount(0);
                    codeEntity.setSecondNumberUnitCount(0);
                    codeEntity.setThirdNumberUnitCount(0);
                    codeEntity.setCodeLevel(bean.sweepLevel);
                    mCodeOperator.putData(codeEntity);
                    mOperator.putData(entity);
                    return entity;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StockOutEntity>() {
                    @Override
                    public void onNext(StockOutEntity stockOutEntity) {
                        mUpdateCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, stockOutEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mUpdateCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return mUpdateCodeInfoLiveData;
    }

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public LiveData<Resource<Long>> getCalculationTotal(long configId) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryCountByConfigId(configId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long size) {
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

    public LiveData<Resource<StockOutEntity>> updateErrorCodeStatus(String code) {

        MutableLiveData<Resource<StockOutEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(code)
                .map(code1 -> {
                    StockOutEntity entity = mOperator.queryEntityByCode(code1);
                    entity.setCodeStatus(CodeBean.STATUS_CODE_FAIL);
                    mOperator.putData(entity);
                    return entity;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StockOutEntity>() {
                    @Override
                    public void onNext(StockOutEntity entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "更新码状态失败,请重试"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<StockOutEntity>>> refreshListByConfigId(Long configId) {
        MutableLiveData<Resource<List<StockOutEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(aLong, 1, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockOutEntity>>() {
                    @Override
                    public void onNext(List<StockOutEntity> list) {
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

    public LiveData<Resource<List<StockOutEntity>>> loadMoreList(long configId, int page, int pageSize) {
        MutableLiveData<Resource<List<StockOutEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<StockOutEntity> list = mOperator.queryPageDataByConfigId(aLong, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(id, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<StockOutEntity>>() {
                    @Override
                    public void onNext(List<StockOutEntity> list) {
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

    public long queryErrorDataConfigId(long configId) {
        return mOperator.queryErrorDataByConfigId(configId);
    }

    public List<StockOutEntity> querySuccessDataByKey(long configId) {
        return mOperator.querySuccessDataByConfigId(configId);
    }

    public StockOutEntity queryEntityByCode(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean putData(StockOutEntity entity) {
        return mOperator.putData(entity)>0;
    }

    public boolean isRepeatCode(String code) {
        StockOutEntity data = queryEntityByCode(code);
        if (data==null){
            return false;
        }
        String msg;
        if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
            msg = code + "该码被其他用户录入,请切换账号或清除离线数据";
        } else {
            msg = code + "该码已在库存中!";
        }
        ToastUtils.showToast(msg);
        return true;
    }

    public LiveData<Resource<Boolean>> hasWaitUpload() {
        MutableLiveData<Resource<Boolean>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> mOperator.queryAllConfigIdList().size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aBoolean, "获取待执行数据失败"));
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
