package com.jgw.delingha.module.packaging.association.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomFlowableSubscriber;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.delingha.bean.PackageAssociationUploadParamBean;
import com.jgw.delingha.bean.PackageCheckCodeRequestParamBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.PackagingAssociationEntity;
import com.jgw.delingha.sql.operator.PackageConfigOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationOperator;
import com.jgw.delingha.utils.CodeTypeUtils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Cxz
 * data : 2019/12/17
 * description :
 */
public class PackagingAssociationModel {

    private final PackagingAssociationOperator mAssociationOperator;
    private final PackageConfigOperator mConfigOperator;

    public int success;
    public int error;
    private final int mUploadGroupCount = 50;

    public PackagingAssociationModel() {
        mAssociationOperator = new PackagingAssociationOperator();
        mConfigOperator = new PackageConfigOperator();
    }

    public String getCodeTypeId(String code) {
        int codeTypeId = 0;
        if (TextUtils.isEmpty(code)) {
            throw new IllegalStateException("码" + code + "不存在");
        }
        int codeLength = code.length();

        switch (codeLength) {
            //20位物流,21位防伪
            case 21:
            case 20:
                codeTypeId = codeLength;
                break;
            case 18:
                //18位溯源
                codeTypeId = 15;
                break;
            case 17:
                //17位营销
                codeTypeId = 12;
                break;
            //16顺序
            case 16:
                codeTypeId = 14;
                break;
            case 19:
                codeTypeId = 13;
                break;
            default:
                throw new IllegalStateException("该码不存在，请仔细核对！");
        }
        return String.valueOf(codeTypeId);
    }


    public LiveData<Resource<Integer>> getBoxCodeCount(Long configId) {
        MutableLiveData<Resource<Integer>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(mAssociationOperator::queryBoxCodeSize)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void onNext(Integer count) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, count, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });

        return liveData;
    }

    private ValueKeeperLiveData<Resource<String>> mCheckParentCodeInfoLiveData;

    public LiveData<Resource<String>> checkBoxCode(PackageCheckCodeRequestParamBean param) {
        if (mCheckParentCodeInfoLiveData == null) {
            mCheckParentCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("productId", param.productId);
        if (!TextUtils.isEmpty(param.productBatchId)) {
            map.put("productBatchId", param.productBatchId);
        }
        final String code = param.code;
        map.put("parentCode", code);
        map.put("parentCodeTypeId", CodeTypeUtils.getCodeTypeId(code));
        //包装场景类型 0- 是生产包装 1- 是仓储包装 2-混合包装 3-补码入箱
        map.put("packageSceneType", CodeTypeUtils.PackageAssociationType);
        map.put("packageLevel", param.packageLevel);
        HttpUtils.getGatewayApi(ApiService.class)
                .checkPackagingParentCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.LOADING, code, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, code, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, code, e.getMessage()));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, code, e.getMessage()));
                        } else {
                            super.onError(e);
                            mCheckParentCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, code, e.getMessage()));
                        }
                    }
                });
        return mCheckParentCodeInfoLiveData;
    }

    public void updateCodeStatus(String code, int status) {
        PackagingAssociationEntity entity = mAssociationOperator.queryEntityByCode(code);
        if (entity == null) {
            return;
        }
        entity.setCodeStatus(status);
        mAssociationOperator.putData(entity);
    }

    /**
     * 删除父码及关联的子码
     *
     * @param code 父码
     */
    public void removeAllByBoxCode(String code) {
        mAssociationOperator.removeEntityByCode(code);
        mAssociationOperator.deleteAllSonByPrentCode(code);
    }

    /**
     * 删除子码 更新父码非满箱
     *
     * @param code 子码
     */
    public void removeCode(String code) {
        //删除子码时 父码满箱状态要更新
        PackagingAssociationEntity entity = mAssociationOperator.queryEntityByCode(code);
        if (entity==null){
            return;
        }
        String boxCode = entity.getParentOuterCodeId();
        if (!TextUtils.isEmpty(boxCode)) {
            //本身为父码情况 无上级码信息
            updateFullBoxCode(boxCode, false);
        }
        mAssociationOperator.deleteCodeAndCheckParent(entity);
    }

    public void updateCodeListByNewBoxCode(String code, String codeTypeId, List<PackagingAssociationEntity> codeList) {
        mAssociationOperator.updateCodeParentCode(code, codeTypeId, codeList);
    }

    public boolean putData(PackagingAssociationEntity entity) {
        return mAssociationOperator.putData(entity)>0;
    }

    public void updateFullBoxCode(String code, boolean isFull) {
        mAssociationOperator.updateParentCodeIsFull(code, isFull);
    }

    private ValueKeeperLiveData<Resource<PackagingAssociationEntity>> mCheckSonCodeInfoLiveData;
    public LiveData<Resource<PackagingAssociationEntity>> checkCode(PackageCheckCodeRequestParamBean param) {
        if (mCheckSonCodeInfoLiveData == null) {
            mCheckSonCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("productId", param.productId);
        if (!TextUtils.isEmpty(param.productBatchId)) {
            map.put("productBatchId", param.productBatchId);
        }
        //此空值不可删除
        map.put("relationTypeCode", "");
        String code = param.code;
        map.put("outerCode", code);
        map.put("codeTypeId", CodeTypeUtils.getCodeTypeId(code));
        map.put("packageSceneType", param.packageSceneType);
        String boxCode = param.boxCode;
        map.put("parentCode", boxCode);
        map.put("parentCodeTypeId", CodeTypeUtils.getCodeTypeId(boxCode));
        map.put("packageLevel", param.packageLevel);
        //记录当前校验的码与其父码信息
        PackagingAssociationEntity entity = new PackagingAssociationEntity();
        entity.setOuterCode(code);
        entity.setParentOuterCodeId(boxCode);
        HttpUtils.getGatewayApi(ApiService.class)
                .checkPackagingCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.LOADING, entity, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetUtils.iConnected()) {
                            mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, entity, e.getMessage()));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.ERROR, entity, e.getMessage()));
                        } else {
                            super.onError(e);
                            mCheckSonCodeInfoLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, entity, e.getMessage()));
                        }
                    }
                });

        return mCheckSonCodeInfoLiveData;
    }

    public PackagingAssociationEntity queryEntityByCode(String code) {
        return mAssociationOperator.queryEntityByCode(code);
    }

    public boolean isRepeatBoxCode(String code) {
        return mAssociationOperator.isRepeatBoxCode(code);
    }

    public LiveData<Resource<PackagingAssociationEntity>> showBoxList(PackagingAssociationEntity entity) {
        MutableLiveData<Resource<PackagingAssociationEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    List<PackagingAssociationEntity> list = mAssociationOperator.querySonListByBoxCode(entity1.getOuterCode());
                    entity1.setSonList(list);
                    return entity1;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<PackagingAssociationEntity>() {
                    @Override
                    public void onNext(PackagingAssociationEntity entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<PackagingAssociationEntity>>> querySonListByBoxCode(String boxCode) {
        MutableLiveData<Resource<List<PackagingAssociationEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(boxCode)
                .map(mAssociationOperator::querySonListByBoxCode)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<PackagingAssociationEntity>>() {
                    @Override
                    public void onNext(List<PackagingAssociationEntity> list) {
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

    public boolean hasWaitUpload(long configId) {
        return mAssociationOperator.queryBoxCodeSize(configId) > 0;
    }

    public LiveData<Resource<String>> checkNotFullBoxCode(Long configId) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> {
                    String code = "";
                    for (PackagingAssociationEntity entity : mAssociationOperator.queryBoxListByConfigurationId(id)) {
                        if (!entity.getIsFull()) {
                            code = entity.getOuterCode();
                            break;
                        }
                    }
                    return code;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> getRemoveAllByBoxCodeLiveDate(String boxCode) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(boxCode)
                .map(code -> {
                    removeAllByBoxCode(code);
                    return code;
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String boxCode) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, boxCode, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> getRemoveCodeLiveDate(String code) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(code)
                .map(code1 -> {
                    removeCode(code1);
                    return code1;
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String code) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, code, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> getClearDataLiveDate(long configId) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(code1 -> {
                    mAssociationOperator.deleteAllByConfigId(configId);
                    return "删除成功";
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String code) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<PackageConfigEntity>>> getAllConfigCodeList(boolean isMix) {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();
        MutableLiveData<Resource<List<PackageConfigEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(isMix)
                .flatMap((Function<Boolean, ObservableSource<Long>>) whatever ->
                        Observable.fromIterable(mAssociationOperator.queryAllConfigIdList()))
                .filter(configId -> mAssociationOperator.queryCountByConfigId(configId) > 1)//过滤空箱码
                .map(configurationOperator::queryDataById)
                .filter(packageConfigEntity -> {
                    boolean b = isMix ? packageConfigEntity.getPackageSceneType() == CodeTypeUtils.MixPackageType :
                            packageConfigEntity.getPackageSceneType() == CodeTypeUtils.PackageAssociationType;
                    return b;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<PackageConfigEntity>() {
                    private List<PackageConfigEntity> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(PackageConfigEntity packagingConfigurationEntity) {
                        list.add(packagingConfigurationEntity);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
                    }
                });

        return liveData;
    }

    public LiveData<Resource<List<PackagingAssociationEntity>>> getConfigCodeList(long configId) {
        MutableLiveData<Resource<List<PackagingAssociationEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map((Function<Long, List<PackagingAssociationEntity>>) aLong -> {
                    ArrayList<PackagingAssociationEntity> list = new ArrayList<>();
                    for (PackagingAssociationEntity boxBean : mAssociationOperator.queryBoxListByConfigurationId(aLong)) {
                        list.add(boxBean);
                        List<PackagingAssociationEntity> sonList = mAssociationOperator.querySonListByBoxCode(boxBean.getOuterCode());
                        list.addAll(sonList);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<PackagingAssociationEntity>>() {

                    @Override
                    public void onNext(List<PackagingAssociationEntity> list) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
                    }
                });

        return liveData;
    }

    public LiveData<Resource<String>> removeAllByConfigIds(List<Long> input) {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();

        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.fromIterable(input)
                .map(configId -> {
                    mAssociationOperator.deleteAllByConfigId(configId);
                    configurationOperator.removeData(configId);
                    return "删除成功";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<String>() {

                    @Override
                    public void onNext(String str) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, str, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "删除失败"));
                    }
                });

        return liveData;
    }

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        LogUtils.showLog("uploadNewCodes:startTime=", System.currentTimeMillis() + "");
        PackageConfigOperator configurationOperator = new PackageConfigOperator();

        PackageConfigEntity configEntity = configurationOperator.queryDataById(configId);

        PackageAssociationUploadParamBean param = new PackageAssociationUploadParamBean();
        //noinspection ConstantConditions
        param.firstNumberCode = configEntity.getFirstNumberCode();
        param.lastNumberCode = configEntity.getLastNumberCode();
        param.packageSpecification = configEntity.getPackageSpecification();
        param.packageLevel = configEntity.getPackageLevel();
        param.productId = configEntity.getProductId();
        param.productName = configEntity.getProductName();
        param.productCode = configEntity.getProductCode();
        param.productBatchId = configEntity.getProductBatchId();
        param.productBatchName = configEntity.getProductBatchName();
        param.remark = configEntity.getRemark();
        param.packageSceneType = configEntity.getPackageSceneType();

        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;

                    List<PackagingAssociationEntity> list1;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list1 = mAssociationOperator.queryBoxListByConfigurationId(configId, currentId, mUploadGroupCount);
                        if (list1 == null || list1.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            PackagingAssociationEntity entity = list1.get(list1.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;
                        }
                        success += list1 == null ? 0 : list1.size();
                    } while (next);
                    String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(index -> mAssociationOperator.queryBoxListByConfigurationId(configId, index, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<PackagingAssociationEntity>, Publisher<HttpResult<String>>>) boxCodeList -> {

                    String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    ArrayList<String> boxCodeStrList = new ArrayList<>();
                    ArrayList<PackageAssociationUploadParamBean> tempList = new ArrayList<>();

                    for (int i = 0; i < boxCodeList.size(); i++) {
                        PackagingAssociationEntity entity = boxCodeList.get(i);
                        boxCodeStrList.add(entity.getOuterCode());
                        PackageAssociationUploadParamBean clone = (PackageAssociationUploadParamBean) param.clone();
                        clone.notFullBox = entity.getIsFull() ? 0 : 1;
                        clone.packageCode = entity.getOuterCode();
                        ArrayList<String> codeList = new ArrayList<>();
                        for (PackagingAssociationEntity e : mAssociationOperator.querySonListByBoxCode(entity.getOuterCode())) {
                            codeList.add(e.getOuterCode());
                        }
                        clone.codeList = codeList;
                        tempList.add(clone);
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = configEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskNumber", taskId);
                    map.put("list", tempList);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiService.class)
                                .uploadPackagingAssociation(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mAssociationOperator.deleteAllSonByPrentCodes(boxCodeStrList);
                        } else {
                            error += boxCodeList.size();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error += boxCodeList.size();
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
        PackageConfigOperator configurationOperator = new PackageConfigOperator();

        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        Flowable.fromIterable(configIds)
                .parallel()
                .runOn(Schedulers.io())
                .flatMap((Function<Long, Publisher<PackageConfigEntity>>) configId -> {
                    boolean next = true;
                    long currentId = 0;

                    List<PackagingAssociationEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mAssociationOperator.queryBoxListByConfigurationId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            PackagingAssociationEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                String name1 = Thread.currentThread().getName();
                                LogUtils.xswShowLog("");
                                PackageConfigEntity entity = configurationOperator.queryDataById(configId);
                                //noinspection ConstantConditions
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mAssociationOperator.queryBoxListByConfigurationId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<PackageConfigEntity, Publisher<HttpResult<String>>>) mConfigEntity -> {

                    String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    ArrayList<PackageAssociationUploadParamBean> list = new ArrayList<>();
                    PackageAssociationUploadParamBean param = new PackageAssociationUploadParamBean();
                    param.firstNumberCode = mConfigEntity.getFirstNumberCode();
                    param.lastNumberCode = mConfigEntity.getLastNumberCode();
                    param.packageSpecification = mConfigEntity.getPackageSpecification();
                    param.packageLevel = mConfigEntity.getPackageLevel();
                    param.productId = mConfigEntity.getProductId();
                    param.productName = mConfigEntity.getProductName();
                    param.productCode = mConfigEntity.getProductCode();
                    param.productBatchId = mConfigEntity.getProductBatchId();
                    param.productBatchName = mConfigEntity.getProductBatchName();
                    param.remark = mConfigEntity.getRemark();
                    param.packageSceneType = CodeTypeUtils.PackageAssociationType;
                    List<PackagingAssociationEntity> boxCodeList = mAssociationOperator.queryBoxListByConfigurationId(mConfigEntity.getId(), mConfigEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> boxCodeStrList = new ArrayList<>();
                    for (int i = 0; i < boxCodeList.size(); i++) {
                        PackagingAssociationEntity entity = boxCodeList.get(i);
                        boxCodeStrList.add(entity.getOuterCode());
                        PackageAssociationUploadParamBean clone = (PackageAssociationUploadParamBean) param.clone();
                        clone.notFullBox = entity.getIsFull() ? 0 : 1;
                        clone.packageCode = entity.getOuterCode();
                        ArrayList<String> codeList = new ArrayList<>();
                        for (PackagingAssociationEntity e : mAssociationOperator.querySonListByBoxCode(entity.getOuterCode())) {
                            codeList.add(e.getOuterCode());
                        }
                        clone.codeList = codeList;
                        list.add(clone);
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = mConfigEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskNumber", taskId);
                    map.put("list", list);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiService.class)
                                .uploadPackagingAssociation(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mAssociationOperator.deleteAllSonByPrentCodes(boxCodeStrList);
                        } else {
                            error += boxCodeList.size();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error += boxCodeList.size();
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

    public LiveData<Resource<Boolean>> checkWaitUpload(boolean isMix) {
        MutableLiveData<Resource<Boolean>> liveData = new ValueKeeperLiveData<>();

        Observable.just("whatever")
                .flatMap(s -> {
                    List<Long> idList = mAssociationOperator.queryAllConfigIdList();
                    ArrayList<Long> tempList = new ArrayList<>();
                    for (int i = 0; i < idList.size(); i++) {
                        Long config_id = idList.get(i);
                        long l = mAssociationOperator.queryCountByConfigId(config_id);
                        if (l < 2) {
                            tempList.add(config_id);
                        }
                    }
                    idList.removeAll(tempList);
                    return Observable.fromIterable(idList);
                })
                .map(mConfigOperator::queryDataById)
                .filter(packageConfigEntity -> {
                    boolean b = isMix ? packageConfigEntity.getPackageSceneType() == CodeTypeUtils.MixPackageType :
                            packageConfigEntity.getPackageSceneType() == CodeTypeUtils.PackageAssociationType;
                    return b;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<PackageConfigEntity>() {
                    boolean hasWaitUpload=false;
                    @Override
                    public void onNext(PackageConfigEntity list) {
                        hasWaitUpload=true;
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, hasWaitUpload, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });

        return liveData;
    }

}
