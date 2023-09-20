package com.jgw.delingha.module.disassemble.base.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.SplitCheckResultBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.operator.BaseDisassembleOperator;
import com.jgw.delingha.sql.operator.GroupDisassembleOperator;
import com.jgw.delingha.sql.operator.SingleDisassembleOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("unchecked")
public class DisassembleModel {

    @SuppressWarnings("rawtypes")
    private BaseDisassembleOperator mOperator;
    private final int mUploadGroupCount = 500;
    private boolean isSingleDisassemble;

    public DisassembleModel() {
    }

    public void switchType(boolean isSingle) {
        isSingleDisassemble = isSingle;
        if (isSingle) {
            mOperator = new SingleDisassembleOperator();
        } else {
            mOperator = new GroupDisassembleOperator();
        }
    }

    public LiveData<Resource<Long>> getCalculationTotal() {
        MutableLiveData<Resource<Long>> liveData = new MutableLiveData<>();
        Observable.just("whatever")
                .map(isSingle -> mOperator.queryCount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long integer) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, integer, ""));
                    }
                });
        return liveData;
    }

    public boolean existVerifyingData() {
        return mOperator.queryUnverifiedCount() != 0;
    }

    public long putData(BaseCodeEntity entity) {
        return mOperator.putData(entity);
    }

    private ValueKeeperLiveData<Resource<SplitCheckResultBean>> mCheckCodeLiveData;

    public LiveData<Resource<SplitCheckResultBean>> checkCode(String code) {
        if (mCheckCodeLiveData == null) {
            mCheckCodeLiveData = new ValueKeeperLiveData<>();
        }
        Map<String, String> map = new HashMap<>();
        map.put("outerCodeId", code);
        Observable<HttpResult<SplitCheckResultBean>> observable;
        if (isSingleDisassemble) {
            observable = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                    .checkSplitSingleCodeRelation(map);
        } else {
            observable = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                    .checkSplitGroupCodeRelation(map);
        }

        observable
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<SplitCheckResultBean>() {
                    @Override
                    public void onNext(SplitCheckResultBean bean) {
                        bean.code = code;
                        bean.status = CodeBean.STATUS_CODE_SUCCESS;
                        mCheckCodeLiveData.setValue(new Resource<>(Resource.SUCCESS, bean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        SplitCheckResultBean bean = new SplitCheckResultBean();
                        bean.code = code;
                        bean.status = CodeBean.STATUS_CODE_FAIL;
                        if (!NetUtils.iConnected()) {
                            mCheckCodeLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, bean, ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckCodeLiveData.postValue(new Resource<>(Resource.ERROR, bean, e.getMessage()));
                        } else {
                            super.onError(e);
                            mCheckCodeLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, bean, ""));
                        }
                    }
                });
        return mCheckCodeLiveData;
    }

    public LiveData<Resource<BaseCodeEntity>> updateCodeInfo(SplitCheckResultBean input) {
        MutableLiveData<Resource<BaseCodeEntity>> liveData = new MutableLiveData<>();
        Observable.just(input)
                .map(bean -> {
                    BaseCodeEntity entity = mOperator.queryEntityByCode(bean.code);
                    entity.setCodeStatus(bean.status);
                    entity.setCodeLevelName(bean.levelName);
                    mOperator.putData(entity);
                    return entity;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<BaseCodeEntity>() {
                    @Override
                    public void onNext(BaseCodeEntity bean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, bean, ""));
                    }
                });
        return liveData;
    }

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public boolean isRepeatCode(String code) {
        BaseCodeEntity data = mOperator.queryEntityByCode(code);
        if (data != null) {
            if (data.getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
            } else {
                ToastUtils.showToast(code + "该码已在库存中!");
            }
            return true;
        }
        return false;
    }

    public LiveData<Resource<Boolean>> hasWaitUpload() {
        MutableLiveData<Resource<Boolean>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(b -> mOperator.queryCount() > 0)
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

    public LiveData<Resource<List<BaseCodeEntity>>> loadList(int page) {
        MutableLiveData<Resource<List<BaseCodeEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(page)
                .map((Function<Integer, List<BaseCodeEntity>>) p -> mOperator.queryListByPage(p, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<BaseCodeEntity>>() {
                    @Override
                    public void onNext(List<BaseCodeEntity> list) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    int success;
    int error;

    /**
     * 上传firstId之后的码
     */
    public LiveData<Resource<UploadResultBean>> upload() {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        Flowable.just("whatever")
                .map((Function<String, List<Long>>) aLong -> {
                    long currentId = 0;
                    boolean next = true;

                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        List<BaseCodeEntity> list = mOperator.queryDataByCurrentId(currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            BaseCodeEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);

                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map((Function<Long, List<BaseCodeEntity>>) index -> mOperator.queryDataByCurrentId(index, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<BaseCodeEntity>, Publisher<HttpResult<String>>>) list -> {
                    ArrayList<String> codes = new ArrayList<>();
                    for (BaseCodeEntity e : list) {
                        codes.add(e.getCode());
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < codes.size(); i++) {
                        sb.append(codes.get(i));
                        if (i != codes.size() - 1) {
                            sb.append(",");
                        }
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("outerCodeIdList", sb.toString());
                    HttpResult<String> stringHttpResult = null;
                    try {
                        Observable<HttpResult<String>> observable;
                        if (isSingleDisassemble) {
                            observable = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                    .splitSingleCodeRelation(map);
                        } else {
                            observable = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                    .splitGroupCodeRelation(map);
                        }
                        stringHttpResult = observable.blockingSingle();
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

    public void removeCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public LiveData<Resource<String>> getClearDataByCurrentUserLiveDate() {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(code1 -> {
                    mOperator.deleteAllByCurrentUser();
                    return "删除成功";
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String code) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }
                });
        return liveData;
    }
}
