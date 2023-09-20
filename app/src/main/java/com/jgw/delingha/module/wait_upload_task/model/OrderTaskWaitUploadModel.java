package com.jgw.delingha.module.wait_upload_task.model;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.delingha.module.order_task.TaskModel;
import com.jgw.delingha.module.scan_back.ui.CommonOrderProductScanBackActivity;
import com.jgw.delingha.sql.entity.BaseOrderEntity;
import com.jgw.delingha.sql.operator.BaseOrderOperator;
import com.jgw.delingha.sql.operator.OrderStockOutOperator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class OrderTaskWaitUploadModel {


    private final TaskModel mTaskModel;
    private ValueKeeperLiveData<Resource<String>> mUploadLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteLiveData;
    private ValueKeeperLiveData<Resource<List<? extends BaseOrderEntity>>> mLocalOrderStockOutListLiveData;

    public OrderTaskWaitUploadModel() {
        mTaskModel = new TaskModel();
    }

    @Nullable
    private BaseOrderOperator<? extends BaseOrderEntity> switchTypeOrderOperator(int operateType) {
        BaseOrderOperator<? extends BaseOrderEntity> mOperator;
        //noinspection SwitchStatementWithTooFewBranches
        switch (operateType) {
            case CommonOrderProductScanBackActivity.TYPE_STOCK_OUT:
                mOperator = new OrderStockOutOperator();
                break;
            default:
                mOperator = null;
        }
        return mOperator;
    }

    public LiveData<Resource<List<? extends BaseOrderEntity>>> getLocalOrderStockOutList(int operateType) {
        if (mLocalOrderStockOutListLiveData == null) {
            mLocalOrderStockOutListLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just("whatever")
                .map((Function<String, List<? extends BaseOrderEntity>>) s -> {
                    BaseOrderOperator<? extends BaseOrderEntity> baseOrderOperator = switchTypeOrderOperator(operateType);
                    //noinspection ConstantConditions
                    return baseOrderOperator.queryAll();
                })
                .subscribe(new CustomObserver<List<? extends BaseOrderEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mLocalOrderStockOutListLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<? extends BaseOrderEntity> data) {
                        mLocalOrderStockOutListLiveData.postValue(new Resource<>(Resource.SUCCESS, data, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        mLocalOrderStockOutListLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                        super.onError(e);
                    }
                });
        return mLocalOrderStockOutListLiveData;
    }

    public LiveData<Resource<String>> deleteOrders(List<? extends BaseOrderEntity> list, int operateType) {
        if (mDeleteLiveData == null) {
            mDeleteLiveData = new ValueKeeperLiveData<>();
        }
        Observable.fromIterable(list)
                .flatMap((Function<BaseOrderEntity, ObservableSource<BaseOrderEntity>>) baseOrderEntity ->
                        mTaskModel.deleteAllOrderProductCode(baseOrderEntity.getTaskId())
                                .map(s -> baseOrderEntity))
                .map(s -> {
                    //noinspection rawtypes
                    BaseOrderOperator baseOrderOperator = switchTypeOrderOperator(operateType);
                    //noinspection unchecked,ConstantConditions
                    baseOrderOperator.deleteAllCodeByOrderEntity(s);
                    return "taskId=" + s.getTaskId();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDeleteLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        LogUtils.xswShowLog(s);
                    }

                    @Override
                    public void onComplete() {
                        mDeleteLiveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mDeleteLiveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return mDeleteLiveData;

    }

    public LiveData<Resource<String>> upload(BaseOrderEntity entity, int taskType, int operateType) {
        if (mUploadLiveData == null) {
            mUploadLiveData = new ValueKeeperLiveData<>();
        }
        mTaskModel.executeTask(entity.getTaskId(), taskType, 1)
                .map(s -> {
                    //noinspection rawtypes
                    BaseOrderOperator baseOrderOperator = switchTypeOrderOperator(operateType);
                    //noinspection ConstantConditions,unchecked
                    baseOrderOperator.deleteAllCodeByOrderEntity(entity);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUploadLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        mUploadLiveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mUploadLiveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return mUploadLiveData;
    }
}
