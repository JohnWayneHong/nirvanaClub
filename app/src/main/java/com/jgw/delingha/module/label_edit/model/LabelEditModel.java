package com.jgw.delingha.module.label_edit.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.LabelEditWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.LabelEditEntity;
import com.jgw.delingha.sql.operator.ConfigurationOperator;
import com.jgw.delingha.sql.operator.LabelEditOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Cxz
 * data : 2019/12/17
 * description :
 */
public class LabelEditModel {

    private final LabelEditOperator mOperator;
    private final ConfigurationOperator mConfigOperator;

    public int success;
    public int error;
    private final int mUploadGroupCount = 500;

    public LabelEditModel() {
        mOperator = new LabelEditOperator();
        mConfigOperator = new ConfigurationOperator();
    }

    public LiveData<Resource<LabelEditEntity>> updateCodeStatus(CodeBean bean) {
        MutableLiveData<Resource<LabelEditEntity>> liveData = new MutableLiveData<>();

        Observable.just(bean)
                .filter(codeBean -> {
                    boolean isExist = mOperator.queryEntityByCode(bean.outerCodeId) != null;
                    if (!isExist) {
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return isExist;
                })
                .map(codeBean -> {
                    LabelEditEntity entity = mOperator.queryEntityByCode(bean.outerCodeId);
                    entity.setCodeStatus(bean.codeStatus);
                    mOperator.putData(entity);
                    return entity;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<LabelEditEntity>() {
                    @Override
                    public void onNext(LabelEditEntity labelEditEntity) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, labelEditEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;

    }

    /**
     * 删除子码 更新父码非满箱
     *
     * @param code 子码
     */
    public void removeCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public boolean putData(LabelEditEntity entity) {
        return mOperator.putData(entity)>0;
    }

    private MutableLiveData<Resource<CodeBean>> mCheckCodeInfoLiveData;

    public LiveData<Resource<CodeBean>> checkCode(String code) {
        if (mCheckCodeInfoLiveData == null) {
            mCheckCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        CodeBean codeBean = new CodeBean();
        codeBean.outerCodeId = code;
        codeBean.codeStatus = CodeBean.STATUS_CODE_SUCCESS;
        mCheckCodeInfoLiveData.setValue(new Resource<>(Resource.SUCCESS, codeBean, ""));
        return mCheckCodeInfoLiveData;
    }

    public LabelEditEntity queryEntityByCode(String code) {
        return mOperator.queryEntityByCode(code);
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

    public LiveData<Resource<List<LabelEditWaitUploadListBean>>> getConfigGroupList() {
        MutableLiveData<Resource<List<LabelEditWaitUploadListBean>>> liveData = new ValueKeeperLiveData<>();

        List<Long> longs = mOperator.queryAllConfigIdList();

        Observable.fromIterable(longs)
                .map(aLong -> {
                    LabelEditWaitUploadListBean bean = new LabelEditWaitUploadListBean();
                    ConfigurationEntity entity = mConfigOperator.queryDataById(aLong);
//                    UserEntity userInfo = entity.getUserInfo();
                    bean.create_time = FormatUtils.formatTime(entity.getCreateTime());
                    bean.productName = entity.getProductName();
                    bean.batchName = entity.getBatchName();
                    bean.config_id = entity.getId();
                    return bean;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<LabelEditWaitUploadListBean>() {

                    private final List<LabelEditWaitUploadListBean> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(LabelEditWaitUploadListBean LabelEditWaitUploadListBean) {
                        list.add(LabelEditWaitUploadListBean);
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

    public LiveData<Resource<List<LabelEditEntity>>> refreshListByConfigId(Long configId) {
        MutableLiveData<Resource<List<LabelEditEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> mOperator.queryPageDataByConfigId(configId, 0, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<LabelEditEntity>>() {
                    @Override
                    public void onNext(List<LabelEditEntity> list) {
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

    public LiveData<Resource<List<LabelEditEntity>>> loadMoreList(long configId, int pageSize, int page) {
        MutableLiveData<Resource<List<LabelEditEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .filter(aLong -> {
                    List<LabelEditEntity> list = mOperator.queryPageDataByConfigId(configId, page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(id -> mOperator.queryPageDataByConfigId(configId, page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<LabelEditEntity>>() {
                    @Override
                    public void onNext(List<LabelEditEntity> list) {
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

    public LiveData<Resource<Long>> getCalculationTotal(long configId) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mOperator::queryCountByConfigId)
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


    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();

        ConfigurationEntity config = mConfigOperator.queryDataById(configId);

        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;
                    List<LabelEditEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigIdV2(configId1, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            LabelEditEntity entity = list.get(list.size() - 1);
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
                .map(index -> mOperator.queryDataByConfigIdV2(configId, index, mUploadGroupCount))
                .flatMap((Function<List<LabelEditEntity>, Publisher<HttpResult<String>>>) list -> {

                    LogUtils.xswShowLog("");
                    ArrayList<String> codes = new ArrayList<>();
                    for (LabelEditEntity soe : list) {
                        codes.add(soe.getCode());
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    //商品
                    map.put("productName", config.getProductName());
                    map.put("productId", config.getProductId());
                    map.put("productCode", config.getProductCode());
                    //批次
                    map.put("productBatchId", config.getBatchId());
                    map.put("productBatch", config.getBatchName());
                    //码集合
                    map.put("outerCodeIdList", codes);
                    String taskId = config.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postLabelEditCodeList(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.deleteData(list);
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

                    List<LabelEditEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryDataByConfigIdV2(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            LabelEditEntity entity = list.get(list.size() - 1);
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
                .flatMap((Function<ConfigurationEntity, Publisher<HttpResult<String>>>) config -> {

                    LogUtils.xswShowLog("");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    //商品
                    map.put("productName", config.getProductName());
                    map.put("productId", config.getProductId());
                    map.put("productCode", config.getProductCode());
                    //批次
                    map.put("productBatchId", config.getBatchId());
                    map.put("productBatch", config.getBatchName());

                    List<LabelEditEntity> list = mOperator.queryDataByConfigIdV2(config.getId(), config.getIndexId(), mUploadGroupCount);
                    ArrayList<String> codes = new ArrayList<>();
                    if (list != null) {
                        for (LabelEditEntity soe : list) {
                            codes.add(soe.getCode());
                        }
                    }
                    map.put("outerCodeIdList", codes);
                    String taskId = config.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .postLabelEditCodeList(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.deleteData(list);
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

    public void removeAllByConfigIds(List<Long> input) {
        Observable.fromIterable(input)
                .map(configId -> {
                    mOperator.deleteAllByConfigId(configId);
                    mConfigOperator.removeData(configId);
                    return "删除成功";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<String>() {

                    @Override
                    public void onNext(String str) {
                    }
                });

    }

    public void removeAll() {
        Observable.just("whatever")
                .map(configId -> {
                    mOperator.deleteAll();
                    return "删除成功";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<String>() {

                    @Override
                    public void onNext(String str) {
                    }
                });
    }
}
