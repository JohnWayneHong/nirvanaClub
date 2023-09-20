package com.jgw.delingha.module.disassemble.all.model;

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
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.DisassembleAllEntity;
import com.jgw.delingha.sql.operator.DisassembleAllOperator;

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

public class DisassembleAllModel {

    private final DisassembleAllOperator mOperator;
    private final int mUploadGroupCount = 500;

    public DisassembleAllModel() {
        mOperator = new DisassembleAllOperator();
    }

    public LiveData<Resource<Integer>> getCalculationTotal() {
        MutableLiveData<Resource<Integer>> liveData = new MutableLiveData<>();
        Observable.just("whatever")
                .map(s -> {
                    List<DisassembleAllEntity> list = mOperator.queryAll();
                    return list == null ? 0 : list.size();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, integer, ""));
                    }
                });
        return liveData;
    }

    public boolean existVerifyingData() {
        long size = mOperator.queryVerifyingCount();
        return size != 0;
    }

    public boolean putData(DisassembleAllEntity entity) {
        return mOperator.putData(entity)>0;
    }

    private ValueKeeperLiveData<Resource<DisassembleAllEntity>> mCheckGroupCodeInfoLiveData;

    public LiveData<Resource<DisassembleAllEntity>> checkCode(String code) {
        if (mCheckGroupCodeInfoLiveData == null) {
            mCheckGroupCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("outerCode", code);
        HttpUtils.getGatewayApi(ApiService.class)
                .checkDisassembleAllCode(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(string -> {
                    DisassembleAllEntity disassembleAllEntity = mOperator.queryEntityByCode(code);
                    disassembleAllEntity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
                    return disassembleAllEntity;
                })
                .subscribe(new CustomObserver<DisassembleAllEntity>() {
                    @Override
                    public void onNext(DisassembleAllEntity entity) {
                        mCheckGroupCodeInfoLiveData.setValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisassembleAllEntity disassembleAllEntity = new DisassembleAllEntity();
                        disassembleAllEntity.setCode(code);
                        disassembleAllEntity.setCodeStatus(CodeBean.STATUS_CODE_FAIL);
                        if (!NetUtils.iConnected()) {
                            mCheckGroupCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, disassembleAllEntity, ""));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckGroupCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, disassembleAllEntity, e.getMessage()));
                        } else {
                            super.onError(e);
                            mCheckGroupCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, disassembleAllEntity, ""));
                        }
                    }
                });
        return mCheckGroupCodeInfoLiveData;
    }

    public LiveData<Resource<DisassembleAllEntity>> updateCodeInfo(DisassembleAllEntity entity) {
        MutableLiveData<Resource<DisassembleAllEntity>> liveData = new MutableLiveData<>();
        Observable.just(entity)
                .map(bean -> {
                    DisassembleAllEntity allEntity = mOperator.queryEntityByCode(bean.getCode());
                    allEntity.setCodeStatus(bean.getCodeStatus());
                    mOperator.putData(allEntity);
                    return allEntity;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<DisassembleAllEntity>() {
                    @Override
                    public void onNext(DisassembleAllEntity bean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, bean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public void removeEntityByCode(String code) {
        mOperator.removeEntityByCode(code);
    }

    public void removeData(DisassembleAllEntity entity) {
        mOperator.removeData(entity);
    }

    public LiveData<Resource<List<DisassembleAllEntity>>> loadListData() {
        MutableLiveData<Resource<List<DisassembleAllEntity>>> liveData = new MutableLiveData<>();
        Observable.just("whatever")
                .map(s -> mOperator.queryListByPage(1, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<DisassembleAllEntity>>() {
                    @Override
                    public void onNext(List<DisassembleAllEntity> list) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }
                });
        return liveData;
    }

    public boolean isRepeatCode(String code) {
        DisassembleAllEntity data = mOperator.queryEntityByCode(code);
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

    public LiveData<Resource<List<DisassembleAllEntity>>> loadMoreList(int page, int pageSize) {
        MutableLiveData<Resource<List<DisassembleAllEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .filter(aLong -> {
                    List<DisassembleAllEntity> list = mOperator.queryListByPage(page, pageSize);
                    if (list == null) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                    return list != null;
                })
                .map(input -> mOperator.queryListByPage(page, pageSize))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<DisassembleAllEntity>>() {
                    @Override
                    public void onNext(List<DisassembleAllEntity> list) {
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
     * 上传码
     */
    public LiveData<Resource<UploadResultBean>> uploadList() {
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
                        List<DisassembleAllEntity> list = mOperator.queryListByPageV2(currentId, 0, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            DisassembleAllEntity entity = list.get(list.size() - 1);
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
                .map(index -> mOperator.queryListByPageV2(index, 0, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<DisassembleAllEntity>, Publisher<HttpResult<String>>>) list -> {

                    ArrayList<String> codes = new ArrayList<>();
                    for (DisassembleAllEntity e : list) {
                        codes.add(e.getCode());
                    }
                    Map<String, Object> map = new HashMap<>();
//                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("codeList", codes);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiService.class)
                                .uploadDisassembleAll(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.removeData(list);
                            error += JsonUtils.parseObject(stringHttpResult.results).getInt("failTotal");
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

    public LiveData<Resource<String>> getClearDataLiveDate() {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(input -> {
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

    public LiveData<Resource<String>> getClearDataByCurrentUserLiveDate() {
        return getClearDataLiveDate();
    }
}
