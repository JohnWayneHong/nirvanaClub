package com.jgw.delingha.module.scan_back.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.order_task.TaskModel;
import com.jgw.delingha.module.scan_back.ui.CommonOrderProductScanBackActivity;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.BaseOrderProductEntity;
import com.jgw.delingha.sql.entity.BaseOrderScanCodeEntity;
import com.jgw.delingha.sql.operator.BaseOperator;
import com.jgw.delingha.sql.operator.BaseOrderScanCodeOperator;
import com.jgw.delingha.sql.operator.OrderStockOutProductOperator;
import com.jgw.delingha.sql.operator.OrderStockOutScanCodeOperator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xswwg
 * on 2020/12/28
 */
public class CommonOrderProductScanBackModel {

    private final TaskModel mTaskModel;
    private ValueKeeperLiveData<Resource<String>> mDeleteCodeLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteAllByProductIdLiveData;

    public CommonOrderProductScanBackModel() {
        mTaskModel = new TaskModel();
    }

    public LiveData<Resource<Long>> calculationTotalById(long productId, int mType) {
        ValueKeeperLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(productId)
                .map(aLong -> {
                    BaseOrderScanCodeOperator<? extends BaseOrderScanCodeEntity> operator = switchTypeCodeOperator(mType);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    return operator.queryCountByProductId(aLong);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    @Nullable
    private BaseOperator<? extends BaseOrderProductEntity> switchTypeOrderProductOperator(int type) {
        BaseOperator<? extends BaseOrderProductEntity> mOperator;
        switch (type) {
            case CommonOrderProductScanBackActivity.TYPE_STOCK_OUT:
                mOperator = new OrderStockOutProductOperator();
                break;
            default:
                mOperator = null;
        }
        return mOperator;
    }

    @Nullable
    private BaseOrderScanCodeOperator<? extends BaseOrderScanCodeEntity> switchTypeCodeOperator(int type) {
        BaseOrderScanCodeOperator<? extends BaseOrderScanCodeEntity> mOperator;
        switch (type) {
            case CommonOrderProductScanBackActivity.TYPE_STOCK_OUT:
                mOperator = new OrderStockOutScanCodeOperator();
                break;
            default:
                mOperator = null;
        }
        return mOperator;
    }

    public LiveData<Resource<String>> deleteCode(String code, int type, long productId) {
        if (mDeleteCodeLiveData == null) {
            mDeleteCodeLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(code)
                .flatMap((Function<String, ObservableSource<String>>) s -> {
                    BaseOperator<? extends BaseOrderProductEntity> productOperator = switchTypeOrderProductOperator(type);
                    //noinspection ConstantConditions
                    BaseOrderProductEntity baseOrderProductEntity = productOperator.queryDataById(productId);
                    //noinspection ConstantConditions
                    String taskId = baseOrderProductEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        return Observable.just(s);
                    } else {
                        return mTaskModel.deleteOrderProductCode(s, taskId)
                                .map(s1 -> s);
                    }
                })
                .map(code1 -> {
                    BaseOrderScanCodeOperator<? extends BaseOrderScanCodeEntity> operator = switchTypeCodeOperator(type);
                    //noinspection ConstantConditions
                    operator.removeEntityByCode(code1);
                    return code1;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDeleteCodeLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String code) {
                        mDeleteCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, code, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mDeleteCodeLiveData.postValue(new Resource<>(Resource.ERROR, code, e.getMessage()));
                    }
                });
        return mDeleteCodeLiveData;
    }

    public LiveData<Resource<List<? extends BaseCodeEntity>>> getCodeListByProductId(long productId, int page, int type) {
        ValueKeeperLiveData<Resource<List<? extends BaseCodeEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map((Function<String, List<? extends BaseCodeEntity>>) s -> {
                    BaseOrderScanCodeOperator<? extends BaseCodeEntity> operator = switchTypeCodeOperator(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化异常");
                    }
                    List<? extends BaseCodeEntity> list = operator.queryDataByPage(productId, page, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
                    if (list == null) {
                        throw new CustomHttpApiException(500, "数据库查询异常");
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<? extends BaseCodeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<? extends BaseCodeEntity> codeBeans) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, codeBeans, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, e.getMessage()));
                    }
                });
        return liveData;
    }


    public LiveData<Resource<String>> deleteAllByProductId(long productId, int type) {
        if (mDeleteAllByProductIdLiveData == null) {
            mDeleteAllByProductIdLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(productId)
                .flatMap((Function<Long, ObservableSource<Long>>) s -> {
                    BaseOperator<? extends BaseOrderProductEntity> productOperator = switchTypeOrderProductOperator(type);
                    //noinspection ConstantConditions
                    BaseOrderProductEntity baseOrderProductEntity = productOperator.queryDataById(s);
                    //noinspection ConstantConditions
                    String taskId = baseOrderProductEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        return Observable.just(s);
                    } else {
                        return mTaskModel.deleteAllOrderProductCode(taskId)
                                .map(s1 -> s);
                    }
                })
                .map(id -> {
                    BaseOrderScanCodeOperator<? extends BaseOrderScanCodeEntity> operator = switchTypeCodeOperator(type);
                    //noinspection ConstantConditions
                    operator.deleteByProductId(id);
                    return "删除成功";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mDeleteAllByProductIdLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String aLong) {
                        mDeleteAllByProductIdLiveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mDeleteAllByProductIdLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return mDeleteAllByProductIdLiveData;
    }
}
