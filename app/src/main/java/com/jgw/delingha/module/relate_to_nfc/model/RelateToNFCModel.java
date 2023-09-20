package com.jgw.delingha.module.relate_to_nfc.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.NFCTaskBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.RelateToNFCEntity;
import com.jgw.delingha.sql.operator.RelateToNFCOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : J-T
 * @date : 2022/6/8 16:02
 * description :关联NFC model
 */
public class RelateToNFCModel {

    private final int mUploadGroupCount = 500;
    private final RelateToNFCOperator mNFCOperator = new RelateToNFCOperator();
    private static final int PAGE_CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;

    private int success = 0;
    private int error = 0;

    public boolean checkIsRepeatQrCode(String qrCode) {
        return mNFCOperator.checkIsRepeatQrCode(qrCode);
    }

    public boolean checkIsRepeatNFCCode(String nfcCode) {
        return mNFCOperator.checkIsRepeatNFCCode(nfcCode);
    }

    public boolean putData(RelateToNFCEntity entity) {
        long userId = LocalUserUtils.getCurrentUserId();
        entity.getUserEntity().setTargetId(userId);
        return mNFCOperator.putData(entity)>0;
    }

    public LiveData<Resource<Long>> getCount() {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just("")
                .map(s -> mNFCOperator.queryCount())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(@NonNull Long integer) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, integer, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取数目失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<RelateToNFCEntity>>> loadList(int mCurrentPage) {
        MutableLiveData<Resource<List<RelateToNFCEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("")
                .map(id -> mNFCOperator.queryListByPage(mCurrentPage, PAGE_CAPACITY))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<RelateToNFCEntity>>() {
                    @Override
                    public void onNext(@NonNull List<RelateToNFCEntity> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "加载更多失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> getTaskId() {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .getPackageTaskId("17")
                .compose(HttpUtils.applyMainSchedulers())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    return jsonObject.getString("value");
                })
                .subscribe(new CustomObserver<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<UploadResultBean>> uploadList(String taskId) {
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        Flowable.just("")
                .map((Function<String, List<Long>>) s -> {
                    boolean next = true;
                    long currentId = 0;
                    List<RelateToNFCEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mNFCOperator.queryGroupDataByUserId(currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            RelateToNFCEntity entity = list.get(list.size() - 1);
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
                .map(aLong -> mNFCOperator.queryGroupDataByUserId(aLong, mUploadGroupCount))
                .filter(list -> list.size() > 0)
                .flatMap((Function<List<RelateToNFCEntity>, Publisher<HttpResult<String>>>) relateToNFCEntities -> {
                    NFCTaskBean nfcTaskBean = new NFCTaskBean();
                    nfcTaskBean.taskNumber = taskId;
                    NFCTaskBean.ListBean listBean;
                    for (RelateToNFCEntity entity : relateToNFCEntities) {
                        listBean = new NFCTaskBean.ListBean();
                        listBean.sequenceCode = entity.getQRCode();
                        listBean.extendCode = entity.getNFCCode();
                        nfcTaskBean.list.add(listBean);
                    }
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiService.class)
                                .uploadNFCTask(nfcTaskBean).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mNFCOperator.removeData(relateToNFCEntities);
                        } else {
                            error += nfcTaskBean.list.size();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error += nfcTaskBean.list.size();
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
                    public void onSubscribe(@NonNull Subscription s) {
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
