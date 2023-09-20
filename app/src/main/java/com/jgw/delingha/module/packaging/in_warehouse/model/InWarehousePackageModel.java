package com.jgw.delingha.module.packaging.in_warehouse.model;

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
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.delingha.bean.PackageCheckCodeRequestParamBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.operator.InWarehousePackageOperator;
import com.jgw.delingha.sql.operator.PackageConfigOperator;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class InWarehousePackageModel {

    private final InWarehousePackageOperator mOperator;

    public int success;
    public int error;
    private final int mUploadGroupCount = 50;

    public InWarehousePackageModel() {
        mOperator = new InWarehousePackageOperator();
    }

    public String getCodeTypeId(String code) {
        int codeTypeId;
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


    public LiveData<Resource<Long>> getBoxCodeCount(Long configId) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> (long) mOperator.queryBoxCodeSize(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long count) {
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
        String code = param.code;
        map.put("outerCodeId", code);
        map.put("packageLevel", param.packageLevel);
        map.put("productId", param.productId);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkInWareHouseParentCode(map)
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
        mOperator.updateCodeStatusByCode(code,status);
    }

    /**
     * 删除父码及关联的子码
     *
     * @param code 父码
     */
    public void removeAllByBoxCode(String code) {
        mOperator.removeEntityByCode(code);
        mOperator.deleteAllSonByPrentCode(code);
    }

    /**
     * 删除子码 更新父码非满箱
     *
     * @param code 子码
     */
    public void removeCode(String code) {
        //删除子码时 父码满箱状态要更新
        InWarehousePackageEntity entity = mOperator.queryEntityByCode(code);
        if (entity == null) {
            return;
        }
        String boxCode = entity.getParentOuterCodeId();
        if (!TextUtils.isEmpty(boxCode)) {
            //本身为父码情况 无上级码信息
            updateFullBoxCode(boxCode, false);
        }
        mOperator.deleteCodeAndCheckParent(entity);
    }

    public void updateCodeListByNewBoxCode(String code, String codeTypeId, List<InWarehousePackageEntity> codeList) {
        mOperator.updateCodeParentCode(code, codeTypeId, codeList);
    }

    public long putData(InWarehousePackageEntity entity) {
        return mOperator.putData(entity);
    }

    public void updateFullBoxCode(String code, boolean isFull) {
        mOperator.updateParentCodeIsFull(code, isFull);
    }

    private ValueKeeperLiveData<Resource<InWarehousePackageEntity>> mCheckSonCodeInfoLiveData;

    public LiveData<Resource<InWarehousePackageEntity>> checkCode(PackageCheckCodeRequestParamBean param) {
        if (mCheckSonCodeInfoLiveData == null) {
            mCheckSonCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        String code = param.code;
        map.put("outerCodeId", code);
        map.put("packageLevel", param.packageLevel);
        map.put("productId", param.productId);
        //记录当前校验的码与其父码信息
        InWarehousePackageEntity entity = new InWarehousePackageEntity();
        entity.setOuterCode(code);
        entity.setParentOuterCodeId(param.boxCode);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkInWareHouseChildCode(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {

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

    public InWarehousePackageEntity queryEntityByCode(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean isRepeatBoxCode(String code) {
        return mOperator.isRepeatBoxCode(code);
    }

    public LiveData<Resource<InWarehousePackageEntity>> showBoxList(InWarehousePackageEntity entity) {
        MutableLiveData<Resource<InWarehousePackageEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    List<InWarehousePackageEntity> list = mOperator.querySonListByBoxCode(entity1.getOuterCode());
                    entity1.setSonList(list);
                    return entity1;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<InWarehousePackageEntity>() {
                    @Override
                    public void onNext(InWarehousePackageEntity entity) {
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

    public LiveData<Resource<List<InWarehousePackageEntity>>> querySonListByBoxCode(String boxCode) {
        MutableLiveData<Resource<List<InWarehousePackageEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(boxCode)
                .map(mOperator::querySonListByBoxCode)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<InWarehousePackageEntity>>() {
                    @Override
                    public void onNext(List<InWarehousePackageEntity> list) {
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
        return configId != -1 && mOperator.queryBoxCodeSize(configId) > 0;
    }

    public LiveData<Resource<String>> checkNotFullBoxCode(Long configId) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> {
                    String code = "";
                    for (InWarehousePackageEntity entity : mOperator.queryBoxCodeListByConfigId(id)) {
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
                    mOperator.deleteAllByConfigId(configId);
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

    public LiveData<Resource<List<PackageConfigEntity>>> getAllConfigCodeList() {
        PackageConfigOperator configurationOperator = new PackageConfigOperator();
        MutableLiveData<Resource<List<PackageConfigEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) whatever ->
                        Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .filter(configId -> mOperator.queryCountByConfigId(configId) > 1)//过滤空箱码
                .map(configurationOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<PackageConfigEntity>() {
                    private final List<PackageConfigEntity> list = new ArrayList<>();

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

    public LiveData<Resource<List<InWarehousePackageEntity>>> getConfigCodeList(long configId) {
        MutableLiveData<Resource<List<InWarehousePackageEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map((Function<Long, List<InWarehousePackageEntity>>) aLong -> {
                    ArrayList<InWarehousePackageEntity> list = new ArrayList<>();
                    for (InWarehousePackageEntity boxBean : mOperator.queryBoxListByConfigurationId(aLong)) {
                        list.add(boxBean);
                        List<InWarehousePackageEntity> sonList = mOperator.querySonListByBoxCode(boxBean.getOuterCode());
                        list.addAll(sonList);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<InWarehousePackageEntity>>() {

                    @Override
                    public void onNext(List<InWarehousePackageEntity> list) {
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
                    mOperator.deleteAllByConfigId(configId);
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

        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;

                    List<InWarehousePackageEntity> list1;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list1 = mOperator.queryBoxListByConfigurationId(configId, currentId, mUploadGroupCount);
                        if (list1 == null || list1.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            InWarehousePackageEntity entity = list1.get(list1.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;
                        }
                        success += list1 == null ? 0 : list1.size();
                    } while (next);
                    LogUtils.xswShowLog("");
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(index -> mOperator.queryBoxListByConfigurationId(configId, index, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<InWarehousePackageEntity>, Publisher<HttpResult<String>>>) boxCodeList -> {


                    ArrayList<String> boxCodeStrList = new ArrayList<>();
                    ArrayList<Map<String,Object>> tempList = new ArrayList<>();

                    for (int i = 0; i < boxCodeList.size(); i++) {
                        JsonObject jb = new JsonObject();
                        InWarehousePackageEntity entity = boxCodeList.get(i);
                        jb.put("parentOuterCodeId", entity.getOuterCode());
                        boxCodeStrList.add(entity.getOuterCode());
                        ArrayList<String> codeList = new ArrayList<>();
                        for (InWarehousePackageEntity e : mOperator.querySonListByBoxCode(entity.getOuterCode())) {
                            codeList.add(e.getOuterCode());
                        }
                        jb.put("outerCodeIdList", codeList);
                        tempList.add(jb.covertMap());
                    }
                    PackageConfigOperator configurationOperator = new PackageConfigOperator();
                    PackageConfigEntity configEntity = configurationOperator.queryDataById(configId);
                    HashMap<String, Object> map = new HashMap<>();
                    //noinspection ConstantConditions
                    String taskId = configEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("packageLevel", configEntity.getPackageLevel());
                    map.put("firstNumberCode", configEntity.getFirstNumberCode());
                    map.put("firstNumberName", configEntity.getFirstNumberName());
                    map.put("lastNumberCode", configEntity.getLastNumberCode());
                    map.put("lastNumberName", configEntity.getLastNumberName());
                    map.put("packageSpecification", configEntity.getPackageSpecification());
                    map.put("productBatch", configEntity.getProductBatchName());
                    map.put("productBatchId", configEntity.getProductBatchId());
                    map.put("productId", configEntity.getProductId());
                    map.put("productName", configEntity.getProductName());
                    map.put("productCode", configEntity.getProductCode());
                    map.put("wareHouseId", configEntity.getWareHouseId());
                    map.put("wareHouseName", configEntity.getWareHouseName());
                    map.put("wareHouseCode", configEntity.getWareHouseCode());
                    map.put("storeHouseId", configEntity.getStoreHouseId());
                    map.put("storeHouseName", configEntity.getStoreHouseName());
                    map.put("remark", configEntity.getRemark());
                    map.put("predictCodes", mOperator.queryCountByConfigId(configId));
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("codeList", tempList);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .uploadInWareHousePackage(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.deleteAllSonByPrentCodes(boxCodeStrList);
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

                    List<InWarehousePackageEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryBoxListByConfigurationId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            InWarehousePackageEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;
                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    LogUtils.xswShowLog("");
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                LogUtils.xswShowLog("");
                                PackageConfigEntity entity = configurationOperator.queryDataById(configId);
                                //noinspection ConstantConditions
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryBoxListByConfigurationId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<PackageConfigEntity, Publisher<HttpResult<String>>>) configEntity -> {
                    Long configId = configEntity.getId();
                    List<InWarehousePackageEntity> boxCodeList = mOperator.queryBoxListByConfigurationId(configId, configEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> boxCodeStrList = new ArrayList<>();
                    ArrayList<Map<String,Object>> tempList = new ArrayList<>();

                    for (int i = 0; i < boxCodeList.size(); i++) {
                        JsonObject jb = new JsonObject();
                        InWarehousePackageEntity entity = boxCodeList.get(i);
                        jb.put("parentOuterCodeId", entity.getOuterCode());
                        boxCodeStrList.add(entity.getOuterCode());
                        ArrayList<String> codeList = new ArrayList<>();
                        for (InWarehousePackageEntity e : mOperator.querySonListByBoxCode(entity.getOuterCode())) {
                            codeList.add(e.getOuterCode());
                        }
                        jb.put("outerCodeIdList", codeList);
                        tempList.add(jb.covertMap());
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = configEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("packageLevel", configEntity.getPackageLevel());
                    map.put("firstNumberCode", configEntity.getFirstNumberCode());
                    map.put("firstNumberName", configEntity.getFirstNumberName());
                    map.put("lastNumberCode", configEntity.getLastNumberCode());
                    map.put("lastNumberName", configEntity.getLastNumberName());
                    map.put("packageSpecification", configEntity.getPackageSpecification());
                    map.put("productBatch", configEntity.getProductBatchName());
                    map.put("productBatchId", configEntity.getProductBatchId());
                    map.put("productId", configEntity.getProductId());
                    map.put("productName", configEntity.getProductName());
                    map.put("productCode", configEntity.getProductCode());
                    map.put("wareHouseId", configEntity.getWareHouseId());
                    map.put("wareHouseName", configEntity.getWareHouseName());
                    map.put("wareHouseCode", configEntity.getWareHouseCode());
                    map.put("storeHouseId", configEntity.getStoreHouseId());
                    map.put("storeHouseName", configEntity.getStoreHouseName());
                    map.put("remark", configEntity.getRemark());
                    map.put("predictCodes", mOperator.queryCountByConfigId(configId));
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("codeList", tempList);
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiService.class)
                                .uploadPackagingAssociation(map).blockingSingle();
                        if (stringHttpResult != null && stringHttpResult.state == 200) {
                            mOperator.deleteAllSonByPrentCodes(boxCodeStrList);
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
}
