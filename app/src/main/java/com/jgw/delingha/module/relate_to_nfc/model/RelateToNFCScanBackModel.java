package com.jgw.delingha.module.relate_to_nfc.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.sql.entity.RelateToNFCEntity;
import com.jgw.delingha.sql.operator.RelateToNFCOperator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xswwg
 * on 2020/12/28
 */

public class RelateToNFCScanBackModel {

    private final RelateToNFCOperator mNFCOperator = new RelateToNFCOperator();
    /**
     * 要使用同一个liveData对象 否则连续调用之后 前一个liveData会被Transformations.switchMap给remove掉
     */
    private final ValueKeeperLiveData<Resource<RelateToNFCEntity[]>> deleteCodeLiveData = new ValueKeeperLiveData<>();

    public LiveData<Resource<Long>> calculationTotal() {
        ValueKeeperLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just("")
                .map(integer ->  mNFCOperator.queryCount())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long count) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, count, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    /**
     * 根据反扫二维码删除
     */
    public LiveData<Resource<RelateToNFCEntity[]>> deleteByQRCodeAndAddOne(String input, int count, boolean needAdd) {
        RelateToNFCEntity[] entities = new RelateToNFCEntity[2];
        Observable.just(input)
                .map(code -> {
                    if (needAdd) {
                        List<RelateToNFCEntity> list = mNFCOperator.queryListByCount(count + 1);
                        if (list == null) {
                            throw new CustomHttpApiException(500, "数据库查询异常");
                        }
                        for (RelateToNFCEntity entity : list) {
                            if (TextUtils.equals(entity.getQRCode(), input)) {
                                entities[0] = entity;
                                break;
                            }
                        }
                        if (list.size() >= count + 1) {
                            entities[1] = list.get(count);
                        }
                    } else {
                        RelateToNFCEntity entity = mNFCOperator.searchEntityByQRCode(code);
                        entities[0] = entity;
                    }
                    mNFCOperator.deleteByQRCode(input);
                    return entities;
                })
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<RelateToNFCEntity[]>() {
                    @Override
                    public void onNext(@NonNull RelateToNFCEntity[] entities) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return deleteCodeLiveData;
    }

    /**
     * 根据反扫二维码删除
     */
    public LiveData<Resource<RelateToNFCEntity[]>> deleteByNFCCodeAndAddOne(String input, int count, boolean needAdd) {
        RelateToNFCEntity[] entities = new RelateToNFCEntity[2];
        Observable.just(input)
                .map(code -> {
                    if (needAdd) {
                        List<RelateToNFCEntity> list = mNFCOperator.queryListByCount(count + 1);
                        if (list == null) {
                            throw new CustomHttpApiException(500, "数据库查询异常");
                        }
                        for (RelateToNFCEntity entity : list) {
                            if (TextUtils.equals(entity.getNFCCode(), input)) {
                                entities[0] = entity;
                                break;
                            }
                        }
                        if (list.size() >= count + 1) {
                            entities[1] = list.get(count);
                        }
                    } else {
                        RelateToNFCEntity entity = mNFCOperator.searchEntityByNFCCode(code);
                        entities[0] = entity;
                    }
                    mNFCOperator.deleteByNFCCode(input);
                    return entities;
                })
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<RelateToNFCEntity[]>() {
                    @Override
                    public void onNext(@NonNull RelateToNFCEntity[] entities) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        deleteCodeLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return deleteCodeLiveData;
    }

    public LiveData<Resource<List<RelateToNFCEntity>>> getCodeList(int page) {
        ValueKeeperLiveData<Resource<List<RelateToNFCEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> {
                    List<RelateToNFCEntity> list = mNFCOperator.queryListByPage(page, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
                    if (list == null) {
                        throw new CustomHttpApiException(500, "数据库查询异常");
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<RelateToNFCEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull List<RelateToNFCEntity> entities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> deleteAllByCurrentUser() {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just("")
                .map(id -> {
                    mNFCOperator.deleteAllByCurrentUser();
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
