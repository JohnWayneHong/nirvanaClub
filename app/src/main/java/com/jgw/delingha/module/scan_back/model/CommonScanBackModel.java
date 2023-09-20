package com.jgw.delingha.module.scan_back.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.operator.BaseCodeOperator;
import com.jgw.delingha.sql.operator.BaseConfigCodeOperator;
import com.jgw.delingha.sql.operator.BaseNoConfigCodeOperator;
import com.jgw.delingha.sql.operator.DisassembleAllOperator;
import com.jgw.delingha.sql.operator.ExchangeGoodsOperator;
import com.jgw.delingha.sql.operator.ExchangeWarehouseOperator;
import com.jgw.delingha.sql.operator.GroupDisassembleOperator;
import com.jgw.delingha.sql.operator.LabelEditOperator;
import com.jgw.delingha.sql.operator.SingleDisassembleOperator;
import com.jgw.delingha.sql.operator.StockInOperator;
import com.jgw.delingha.sql.operator.StockInPackagedOperator;
import com.jgw.delingha.sql.operator.StockOutFastOperator;
import com.jgw.delingha.sql.operator.StockOutOperator;
import com.jgw.delingha.sql.operator.StockReturnNoVerificationOperator;
import com.jgw.delingha.sql.operator.StockReturnOperator;
import com.jgw.delingha.sql.operator.WanWeiStockOutOperator;
import com.jgw.delingha.sql.operator.WanWeiStockReturnOperator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xswwg
 * on 2020/12/28
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommonScanBackModel {
    /**
     * 要使用同一个liveData对象 否则连续调用之后 前一个liveData会被Transformations.switchMap给remove掉
     */
    private final ValueKeeperLiveData<Resource<BaseCodeEntity[]>> deleteCodeLiveData = new ValueKeeperLiveData<>();


    public LiveData<Resource<Long>> calculationTotalByList(List<CodeBean> mList) {
        ValueKeeperLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        if (mList != null) {
            long size = mList.size();
            liveData.postValue(new Resource<>(Resource.SUCCESS, size, ""));
        } else {
            liveData.postValue(new Resource<>(Resource.ERROR, null, "list is null"));
        }
        return liveData;
    }

    public LiveData<Resource<Long>> calculationTotal(int mType) {
        ValueKeeperLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(mType)
                .map(integer -> {
                    BaseCodeOperator<?> operator = switchType(mType);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    return operator.queryCount();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long aLong) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aLong, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> calculationTotalByConfigId(long configId, int mType) {
        ValueKeeperLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> {
                    BaseConfigCodeOperator<?> operator = switchTypeWaitUpload(mType);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    return operator.queryCountByConfigId(aLong);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long aLong) {
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
    private BaseConfigCodeOperator<? extends BaseCodeEntity> switchTypeWaitUpload(int type) {
        BaseConfigCodeOperator<? extends BaseCodeEntity> mOperator;
        switch (type) {
            case CommonScanBackActivity.TYPE_IN:
                mOperator = new StockInOperator();
                break;
            case CommonScanBackActivity.TYPE_OUT:
                mOperator = new StockOutOperator();
                break;
            case CommonScanBackActivity.TYPE_RETURN:
                mOperator = new StockReturnOperator();
                break;
            case CommonScanBackActivity.TYPE_IN_PACKAGED:
                mOperator = new StockInPackagedOperator();
                break;
            case CommonScanBackActivity.TYPE_STOCK_OUT_FAST:
                mOperator = new StockOutFastOperator();
                break;
            case CommonScanBackActivity.TYPE_EXCHANGE_WAREHOUSE:
                mOperator = new ExchangeWarehouseOperator();
                break;
            case CommonScanBackActivity.TYPE_EXCHANGE_GOODS:
                mOperator = new ExchangeGoodsOperator();
                break;
            case CommonScanBackActivity.TYPE_LABEL_EDIT:
                mOperator = new LabelEditOperator();
                break;
            case CommonScanBackActivity.TYPE_WANWEI_STOCK_OUT:
                mOperator = new WanWeiStockOutOperator();
                break;
            case CommonScanBackActivity.TYPE_WANWEI_STOCK_RETURN:
                mOperator = new WanWeiStockReturnOperator();
                break;
            case CommonScanBackActivity.TYPE_GANGBEN_STOCK_RETURN:
                mOperator = new StockReturnNoVerificationOperator();
                break;
            default:
                mOperator = null;
        }
        return mOperator;
    }

    @Nullable
    private BaseNoConfigCodeOperator<? extends BaseCodeEntity> switchType(int type) {
        BaseNoConfigCodeOperator<? extends BaseCodeEntity> mOperator;
        switch (type) {
            case CommonScanBackActivity.TYPE_DISASSEMBLE_ALL:
                mOperator = new DisassembleAllOperator();
                break;
            case CommonScanBackActivity.TYPE_DISASSEMBLE_SINGLE:
                mOperator = new SingleDisassembleOperator();
                break;
            case CommonScanBackActivity.TYPE_DISASSEMBLE_GROUP:
                mOperator = new GroupDisassembleOperator();
                break;
            default:
                mOperator = null;
        }
        return mOperator;
    }

    public LiveData<Resource<BaseCodeEntity[]>> deleteCodeAndAddOneByConfigId(long configId, String input, int count, int type, boolean needAdd) {
        BaseCodeEntity[] entities = new BaseCodeEntity[2];
        Observable.just(input)
                .map(s -> {
                    BaseConfigCodeOperator operator = CommonScanBackModel.this.switchTypeWaitUpload(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    if (needAdd) {
                        List<BaseCodeEntity> list = operator.queryPageDataByConfigId(configId, count + 1);
                        if (list == null) {
                            throw new CustomHttpApiException(500, "数据库查询异常");
                        }
                        for (BaseCodeEntity entity : list) {
                            if (TextUtils.equals(entity.getCode(), input)) {
                                entities[0] = entity;
                                break;
                            }
                        }
                        if (list.size() >= count + 1) {
                            entities[1] = list.get(count);
                        }
                    } else {
                        BaseCodeEntity sourceEntity = operator.queryEntityByCode(s);
                        entities[0] = sourceEntity;
                    }
                    operator.removeEntityByCode(input);
                    return entities;
                })
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<BaseCodeEntity[]>() {
                    @Override
                    public void onNext(@NonNull BaseCodeEntity[] entities) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return deleteCodeLiveData;
    }

    public LiveData<Resource<BaseCodeEntity[]>> deleteCodeAndAddOne(String input, int count, int type, boolean needAdd) {
        BaseCodeEntity[] entities = new BaseCodeEntity[2];
        Observable.just(input)
                .map(code -> {
                    BaseNoConfigCodeOperator operator = CommonScanBackModel.this.switchType(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    if (needAdd) {
                        List<BaseCodeEntity> list = operator.queryListByCount(count + 1);
                        if (list == null) {
                            throw new CustomHttpApiException(500, "数据库查询异常");
                        }
                        for (BaseCodeEntity entity : list) {
                            if (TextUtils.equals(entity.getCode(), input)) {
                                entities[0] = entity;
                                break;
                            }
                        }
                        if (list.size() >= count + 1) {
                            entities[1] = list.get(count);
                        }
                    } else {
                        BaseCodeEntity sourceEntity = operator.queryEntityByCode(code);
                        entities[0] = sourceEntity;
                    }
                    operator.removeEntityByCode(input);
                    return entities;
                })
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<BaseCodeEntity[]>() {
                    @Override
                    public void onNext(@NonNull BaseCodeEntity[] entities) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return deleteCodeLiveData;
    }

    public LiveData<Resource<List<BaseCodeEntity>>> getCodeList(int page, int type) {
        ValueKeeperLiveData<Resource<List<BaseCodeEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> {
                    BaseCodeOperator baseCodeOperator = switchType(type);
                    if (baseCodeOperator == null) {
                        throw new CustomHttpApiException(500, "初始化异常");
                    }
                    List<BaseCodeEntity> list = baseCodeOperator.queryListByPage(page, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
                    if (list == null) {
                        throw new CustomHttpApiException(500, "数据库查询异常");
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<BaseCodeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<BaseCodeEntity> codeBeans) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, codeBeans, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<BaseCodeEntity>>> getCodeListByConfigId(long configId, int page, int type) {
        ValueKeeperLiveData<Resource<List<BaseCodeEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> {
                    BaseConfigCodeOperator baseCodeOperator = switchTypeWaitUpload(type);
                    if (baseCodeOperator == null) {
                        throw new CustomHttpApiException(500, "初始化异常");
                    }
                    List<BaseCodeEntity> list = (List<BaseCodeEntity>) baseCodeOperator.queryPageDataByConfigId(configId, page, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
                    if (list == null) {
                        throw new CustomHttpApiException(500, "数据库查询异常");
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<BaseCodeEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<BaseCodeEntity> entities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> deleteAllByCurrentUser(int type) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(type)
                .map(id -> {
                    BaseNoConfigCodeOperator<?> operator = switchType(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    operator.deleteAllByCurrentUser();
                    return "删除成功";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(@NonNull String aLong) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> deleteAllByConfigId(long configId, int type) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> {
                    BaseConfigCodeOperator<?> operator = switchTypeWaitUpload(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    operator.deleteAllByConfigId(id);
                    return "删除成功";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(@NonNull String aLong) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }
}
