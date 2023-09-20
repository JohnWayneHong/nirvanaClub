package com.jgw.delingha.module.stock_in.package_stock_in.model;

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
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.PackageCheckCodeRequestParamBean;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.PackageStockInEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.ProductPackageInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.PackageConfigOperator;
import com.jgw.delingha.sql.operator.PackageStockInOperator;
import com.jgw.delingha.sql.operator.ProductBatchOperator;
import com.jgw.delingha.sql.operator.ProductInfoOperator;
import com.jgw.delingha.sql.operator.ProductPackageOperator;
import com.jgw.delingha.sql.operator.StorePlaceOperator;
import com.jgw.delingha.sql.operator.WareHouseOperator;
import com.jgw.delingha.utils.CodeTypeUtils;

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
public class PackageStockInModel {
    private static final int mUploadGroupCount = 50;

    private PackageConfigOperator mConfigOperator;
    private WareHouseOperator wareHouseOperator;
    private StorePlaceOperator storePlaceOperator;
    private ProductPackageOperator productPackageOperator;
    private PackageStockInOperator mOperator;
    private ProductInfoOperator productInfoOperator;
    private ProductBatchOperator productBatchOperator;

    public PackageStockInModel() {
        mConfigOperator = new PackageConfigOperator();
        wareHouseOperator = new WareHouseOperator();
        storePlaceOperator = new StorePlaceOperator();
        productPackageOperator = new ProductPackageOperator();
        mOperator = new PackageStockInOperator();
        productInfoOperator = new ProductInfoOperator();
        productBatchOperator = new ProductBatchOperator();
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfo(Long id) {
        MutableLiveData<Resource<ProductInfoEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(id)
                .map(aLong -> productInfoOperator.queryDataById(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ProductInfoEntity>() {
                    @Override
                    public void onNext(ProductInfoEntity productInfoEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, productInfoEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>> getProductWareMessage(String productId) {
        MutableLiveData<Resource<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(productId)
                .map(s -> {
                    ProductPackageInfoEntity entity = productPackageOperator.queryDataByProductId(productId);
                    return JsonUtils.parseArray(entity.getProductPackageRatios(), ProductWareHouseBean.ListBean.ProductPackageRatiosBean.class);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>() {

                    @Override
                    public void onNext(List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> productPackageRatiosBeans) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, productPackageRatiosBeans, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<ProductBatchEntity>> getProductBatch(Long productBatchId) {
        MutableLiveData<Resource<ProductBatchEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(productBatchId)
                .map(aLong -> productBatchOperator.queryDataById(productBatchId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<ProductBatchEntity>() {
                    @Override
                    public void onNext(ProductBatchEntity productBatchEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, productBatchEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouse(Long wareHouseId) {
        MutableLiveData<Resource<WareHouseEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(wareHouseId)
                .map(aLong -> wareHouseOperator.queryDataById(wareHouseId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<WareHouseEntity>() {
                    @Override
                    public void onNext(WareHouseEntity wareHouseEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, wareHouseEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<StorePlaceEntity>> getStorePlace(Long storePlaceId) {
        MutableLiveData<Resource<StorePlaceEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(storePlaceId)
                .map(aLong -> storePlaceOperator.queryDataById(storePlaceId))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<StorePlaceEntity>() {
                    @Override
                    public void onNext(StorePlaceEntity storePlaceEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, storePlaceEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Long>> getCountTotal(Long configId) {
        MutableLiveData<Resource<Long>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> (long) mOperator.queryBoxCodeSize(id))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, aLong, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public PackageStockInEntity queryEntityByCode(String code) {
        return mOperator.queryEntityByCode(code);
    }

    public boolean isRepeatBoxCode(String code) {
        return mOperator.isRepeatBoxCode(code);
    }

    public long putData(PackageStockInEntity entity) {
        return mOperator.putData(entity);
    }

    public void updateCodeListByNewBoxCode(String code, String codeTypeId, List<PackageStockInEntity> list) {
        mOperator.updateCodeParentCode(code, codeTypeId, list);
    }

    public void updateFullBoxCode(String code, boolean isFull) {
        mOperator.updateParentCodeIsFull(code, isFull);
    }

    public boolean hasWaitUpload(long configId) {
        return mOperator.queryBoxCodeSize(configId) > 0;
    }

    private ValueKeeperLiveData<Resource<String>> mCheckParentCodeInfoLiveData;

    public LiveData<Resource<String>> checkBoxCode(PackageCheckCodeRequestParamBean bean) {
        if (mCheckParentCodeInfoLiveData == null) {
            mCheckParentCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("productId", bean.productId);
        if (!TextUtils.isEmpty(bean.productBatchId)) {
            map.put("productBatchId", bean.productBatchId);
        }
        final String code = bean.code;
        map.put("parentOuterCodeId", code);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkPackageStockInParentCode(map)
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
        PackageStockInEntity entity = mOperator.queryEntityByCode(code);
        if (entity == null) {
            return;
        }
        entity.setCodeStatus(status);
        mOperator.putData(entity);
    }

    public void removeCode(String code) {
        PackageStockInEntity entity = mOperator.queryEntityByCode(code);
        if (entity == null) {
            return;
        }
        String boxCode = entity.getParentOuterCodeId();
        if (!TextUtils.isEmpty(boxCode)) {
            updateFullBoxCode(boxCode, false);
        }
        mOperator.deleteCodeAndCheckParent(entity);
    }

    public void removeAllByBoxCode(String code) {
        mOperator.removeEntityByCode(code);
        mOperator.deleteAllSonByPrentCode(code);
    }


    private ValueKeeperLiveData<Resource<PackageStockInEntity>> mCheckSonCodeInfoLiveData;

    public LiveData<Resource<PackageStockInEntity>> checkCode(PackageCheckCodeRequestParamBean param) {

        if (mCheckSonCodeInfoLiveData == null) {
            mCheckSonCodeInfoLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("productId", param.productId);
        if (!TextUtils.isEmpty(param.productBatchId)) {
            map.put("productBatchId", param.productBatchId);
        }
        String code = param.code;
        map.put("outerCodeId", code);
        String boxCode = param.boxCode;
        map.put("parentOuterCodeId", boxCode);
        //map.put("codeTypeId", CodeTypeUtils.getCodeTypeId(code));
        map.put("parentCodeTypeId", CodeTypeUtils.getCodeTypeId(boxCode));
        //map.put("packageLevel", param.packageLevel);
        PackageStockInEntity entity = new PackageStockInEntity();
        entity.setOuterCode(code);
        entity.setParentOuterCodeId(boxCode);
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkPackageStockInSingleCode(map)
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

    public LiveData<Resource<PackageStockInEntity>> showBoxList(PackageStockInEntity entity) {
        MutableLiveData<Resource<PackageStockInEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    List<PackageStockInEntity> list = mOperator.querySonListByBoxCode(entity1.getOuterCode());
                    entity1.setSonList(list);
                    return entity1;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<PackageStockInEntity>() {
                    @Override
                    public void onNext(PackageStockInEntity entity) {
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

    public LiveData<Resource<List<PackageStockInEntity>>> querySonListByBoxCode(String boxCode) {
        MutableLiveData<Resource<List<PackageStockInEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(boxCode)
                .map(mOperator::querySonListByBoxCode)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<PackageStockInEntity>>() {
                    @Override
                    public void onNext(List<PackageStockInEntity> list) {
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

    public LiveData<Resource<String>> checkNotFullBoxCode(Long configId) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> {
                    String code = "";
                    for (PackageStockInEntity entity : mOperator.queryBoxListByConfigurationId(id)) {
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

    //************************scanBack**********************

    public LiveData<Resource<List<PackageStockInEntity>>> getConfigCodeList(long configId) {
        MutableLiveData<Resource<List<PackageStockInEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map((Function<Long, List<PackageStockInEntity>>) aLong -> {
                    ArrayList<PackageStockInEntity> list = new ArrayList<>();
                    for (PackageStockInEntity boxBean : mOperator.queryBoxListByConfigurationId(aLong)) {
                        list.add(boxBean);
                        List<PackageStockInEntity> sonList = mOperator.querySonListByBoxCode(boxBean.getOuterCode());
                        list.addAll(sonList);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<PackageStockInEntity>>() {
                    @Override
                    public void onNext(List<PackageStockInEntity> packageStockInEntities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, packageStockInEntities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "获取待执行数据失败"));
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
                    mOperator.deleteAllByConfigId(code1);
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

    //*******************ConfigList*****************************//
    public LiveData<Resource<List<PackageConfigEntity>>> getAllConfigCodeList() {
        MutableLiveData<Resource<List<PackageConfigEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .flatMap((Function<String, ObservableSource<Long>>) whatever ->
                        Observable.fromIterable(mOperator.queryAllConfigIdList()))
                .filter(configId -> mOperator.queryCountByConfigId(configId) > 1)//过滤空箱码
                .map(mConfigOperator::queryDataById)
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

    public LiveData<Resource<String>> removeAllByConfigIds(List<Long> input) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
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
                        liveData.postValue(new Resource<>(Resource.SUCCESS, str, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "删除失败"));
                    }
                });

        return liveData;
    }

    public int success;
    public int error;

    public LiveData<Resource<UploadResultBean>> uploadListByConfigId(long configId) {
        success = 0;
        error = 0;
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        LogUtils.showLog("uploadNewCodes:startTime=", System.currentTimeMillis() + "");
        PackageConfigEntity configEntity = mConfigOperator.queryDataById(configId);
        Flowable.just(configId)
                .map((Function<Long, List<Long>>) configId1 -> {
                    boolean next = true;
                    long currentId = 0;

                    List<PackageStockInEntity> list1;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list1 = mOperator.queryBoxListByConfigurationId(configId, currentId, mUploadGroupCount);
                        if (list1 == null || list1.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            PackageStockInEntity entity = list1.get(list1.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;

                        }
                        success += list1 == null ? 0 : list1.size();
                    } while (next);
                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    return indexList;
                })
                .flatMap((Function<List<Long>, Publisher<Long>>) Flowable::fromIterable)
                .parallel()
                .runOn(Schedulers.io())
                .map(index -> mOperator.queryBoxListByConfigurationId(configId, index, mUploadGroupCount))
                .filter(list -> list != null && list.size() > 0)
                .flatMap((Function<List<PackageStockInEntity>, Publisher<HttpResult<String>>>) boxCodeList -> {
                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    ArrayList<Map<String,Object>> codeList = new ArrayList<>();
                    ArrayList<String> boxCodeStrList = new ArrayList<>();
                    for (int i = 0; i < boxCodeList.size(); i++) {
                        PackageStockInEntity entity = boxCodeList.get(i);
                        boxCodeStrList.add(entity.getOuterCode());
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("parentOuterCodeId", entity.getOuterCode());
                        ArrayList<String> subCodeList = new ArrayList<>();
                        for (PackageStockInEntity entity1 : mOperator.querySonListByBoxCode(entity.getOuterCode())) {
                            subCodeList.add(entity1.getOuterCode());
                        }
                        jsonObject.put("outerCodeIdList", subCodeList);
                        codeList.add(jsonObject.covertMap());
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("codeList", codeList);
                    map.put("operationType", AppConfig.OPERATION_TYPE);

                    //noinspection ConstantConditions
                    String taskId = configEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("packageSpecification", configEntity.getPackageSpecification());
                    if (!TextUtils.isEmpty(configEntity.getProductBatchName())) {
                        map.put("productBatch", configEntity.getProductBatchName());
                    }
                    if (!TextUtils.isEmpty(configEntity.getProductBatchId())) {
                        map.put("productBatchId", configEntity.getProductBatchId());
                    }
                    map.put("productCode", configEntity.getProductCode());
                    map.put("productId", configEntity.getProductId());
                    map.put("productName", configEntity.getProductName());
                    map.put("firstNumberCode", configEntity.getFirstNumberCode());
                    map.put("lastNumberCode", configEntity.getLastNumberCode());
                    if (!TextUtils.isEmpty(configEntity.getRemark())) {
                        map.put("remark", configEntity.getRemark());
                    }
                    if (!TextUtils.isEmpty(configEntity.getStoreHouseId())) {
                        map.put("storeHouseId", configEntity.getStoreHouseId());
                    }
                    if (!TextUtils.isEmpty(configEntity.getStoreHouseName())) {
                        map.put("storeHouseName", configEntity.getStoreHouseName());
                    }
                    map.put("wareHouseCode", configEntity.getWareHouseCode());
                    map.put("wareHouseId", configEntity.getWareHouseId());
                    map.put("wareHouseName", configEntity.getWareHouseName());
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .uploadPackageStockIn(map).blockingSingle();
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
        MutableLiveData<Resource<UploadResultBean>> liveData = new ValueKeeperLiveData<>();
        Flowable.fromIterable(configIds)
                .parallel()
                .runOn(Schedulers.io())
                .flatMap((Function<Long, Publisher<PackageConfigEntity>>) configId -> {
                    boolean next = true;
                    long currentId = 0;

                    List<PackageStockInEntity> list;
                    ArrayList<Long> indexList = new ArrayList<>();
                    do {
                        indexList.add(currentId);
                        list = mOperator.queryBoxListByConfigurationId(configId, currentId, mUploadGroupCount);
                        if (list == null || list.size() < mUploadGroupCount) {
                            next = false;
                        } else {
                            PackageStockInEntity entity = list.get(list.size() - 1);
                            //下次查询从本组最后一个id+1开始
                            currentId = entity.getId() + 1;

                        }
                        success += list == null ? 0 : list.size();
                    } while (next);
                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    return Flowable.fromIterable(indexList)
                            .map(index -> {
                                //String name1 = Thread.currentThread().getName();
                                LogUtils.xswShowLog("");
                                PackageConfigEntity entity = mConfigOperator.queryDataById(configId);
                                //noinspection ConstantConditions
                                entity.setIndexId(index);
                                return entity;
                            });
                })
                .filter(config -> {
                    List<?> list = mOperator.queryBoxListByConfigurationId(config.getId(), config.getIndexId(), mUploadGroupCount);
                    return list != null && list.size() > 0;
                })
                .flatMap((Function<PackageConfigEntity, Publisher<HttpResult<String>>>) mConfigEntity -> {

                    //String name = Thread.currentThread().getName();
                    LogUtils.xswShowLog("");
                    ArrayList<Map<String,Object>> upCodeList = new ArrayList<>();
                    List<PackageStockInEntity> boxCodeList = mOperator.queryBoxListByConfigurationId(mConfigEntity.getId(), mConfigEntity.getIndexId(), mUploadGroupCount);
                    ArrayList<String> boxCodeStrList = new ArrayList<>();
                    for (int i = 0; i < boxCodeList.size(); i++) {
                        PackageStockInEntity entity = boxCodeList.get(i);
                        boxCodeStrList.add(entity.getOuterCode());
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("parentOuterCodeId", entity.getOuterCode());
                        ArrayList<String> subCodeList = new ArrayList<>();
                        for (PackageStockInEntity entity1 : mOperator.querySonListByBoxCode(entity.getOuterCode())) {
                            subCodeList.add(entity1.getOuterCode());
                        }
                        jsonObject.put("outerCodeIdList", subCodeList);
                        upCodeList.add(jsonObject.covertMap());
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    String taskId = mConfigEntity.getTaskId();
                    if (TextUtils.isEmpty(taskId)) {
                        throw new IllegalArgumentException("taskId is null");
                    }
                    map.put("taskId", taskId);
                    map.put("codeList", upCodeList);
                    map.put("operationType", AppConfig.OPERATION_TYPE);
                    map.put("packageSpecification", mConfigEntity.getPackageSpecification());
                    if (!TextUtils.isEmpty(mConfigEntity.getProductBatchName())) {
                        map.put("productBatch", mConfigEntity.getProductBatchName());
                    }
                    if (!TextUtils.isEmpty(mConfigEntity.getProductBatchId())) {
                        map.put("productBatchId", mConfigEntity.getProductBatchId());
                    }
                    map.put("productCode", mConfigEntity.getProductCode());
                    map.put("productId", mConfigEntity.getProductId());
                    map.put("productName", mConfigEntity.getProductName());
                    map.put("firstNumberCode", mConfigEntity.getFirstNumberCode());
                    map.put("lastNumberCode", mConfigEntity.getLastNumberCode());
                    if (!TextUtils.isEmpty(mConfigEntity.getRemark())) {
                        map.put("remark", mConfigEntity.getRemark());
                    }
                    if (!TextUtils.isEmpty(mConfigEntity.getStoreHouseId())) {
                        map.put("storeHouseId", mConfigEntity.getStoreHouseId());
                    }
                    if (!TextUtils.isEmpty(mConfigEntity.getStoreHouseName())) {
                        map.put("storeHouseName", mConfigEntity.getStoreHouseName());
                    }
                    map.put("wareHouseCode", mConfigEntity.getWareHouseCode());
                    map.put("wareHouseId", mConfigEntity.getWareHouseId());
                    map.put("wareHouseName", mConfigEntity.getWareHouseName());
                    HttpResult<String> stringHttpResult = null;
                    try {
                        stringHttpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                                .uploadPackageStockIn(map).blockingSingle();
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


    public LiveData<Resource<List<Long>>> checkWaitUpload() {
        MutableLiveData<Resource<List<Long>>> liveData = new ValueKeeperLiveData<>();

        Observable.just("whatever")
                .map(s -> {
                    List<Long> idList = mOperator.queryAllConfigIdList();
                    ArrayList<Long> tempList = new ArrayList<>();
                    for (int i = 0; i < idList.size(); i++) {
                        Long config_id = idList.get(i);
                        long l = mOperator.queryCountByConfigId(config_id);
                        if (l < 2) {
                            //仅存空箱码
                            tempList.add(config_id);
                        }
                    }
                    idList.removeAll(tempList);
                    return idList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<Long>>() {
                    @Override
                    public void onNext(List<Long> list) {
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
}
