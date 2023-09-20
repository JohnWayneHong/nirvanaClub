package com.jgw.delingha.module.scan_back.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.scan_back.ui.CommonPackageScanBackActivity;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.BasePackageCodeEntity;
import com.jgw.delingha.sql.operator.BasePackageCodeOperator;
import com.jgw.delingha.sql.operator.InWarehousePackageOperator;
import com.jgw.delingha.sql.operator.PackageStockInOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationCustomOperator;
import com.jgw.delingha.sql.operator.PackagingAssociationOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : J-T
 * @date : 2022/2/21 14:26
 * description :通用 包装反扫类model 主要是根据不同type操作不同的数据库
 */

public class CommonPackageScanBackModel {

    @Nullable
    private BasePackageCodeOperator<? extends BasePackageCodeEntity> switchTypeWaitUpload(int type) {
        BasePackageCodeOperator<? extends BaseCodeEntity> mOperator;
        switch (type) {
            case CommonPackageScanBackActivity.TYPE_IN_WARE_HOUSE_PACKAGE:
                mOperator = new InWarehousePackageOperator();
                break;
            case CommonPackageScanBackActivity.TYPE_PACKAGE_STOCK_IN:
                mOperator = new PackageStockInOperator();
                break;
            case CommonPackageScanBackActivity.TYPE_PACKAGE_ASSOCIATION:
                mOperator = new PackagingAssociationOperator();
                break;
            case CommonPackageScanBackActivity.TYPE_PACKAGE_ASSOCIATION_CUSTOM:
                mOperator = new PackagingAssociationCustomOperator();
                break;
            default:
                mOperator = null;
        }
        return mOperator;
    }

    private ValueKeeperLiveData<Resource<List<BasePackageCodeEntity>>> getCodeLiveData;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LiveData<Resource<List<BasePackageCodeEntity>>> getNextBoxGroupCode(long configId, int type, long boxId, int page, boolean isNeedChangeBox, boolean isFirst) {
        if (getCodeLiveData == null) {
            getCodeLiveData = new ValueKeeperLiveData<>();
        }
        BasePackageCodeOperator<? extends BasePackageCodeEntity> operator = switchTypeWaitUpload(type);
        if (operator == null) {
            getCodeLiveData.postValue(new Resource<>(Resource.ERROR, null, "初始化数据失败"));
            return getCodeLiveData;
        }
        Observable.just(boxId)
                .map(id -> {
                    if (isFirst) {
                        //如果是第一箱 直接去找数据库中的最后一箱(倒序第一箱)
                        BasePackageCodeEntity lastBox = operator.findLastBox(configId);
                        if (lastBox == null) {
                            throw new CustomHttpApiException(500, "已经没有下一箱了!");
                        }
                        return lastBox;
                    } else {
                        //如果不是第一箱 去找根据是否需要换箱 去找比boxId更小的id的箱
                        long findId = boxId;
                        if (isNeedChangeBox) {
                            findId--;
                        }
                        if (findId < 1) {
                            throw new CustomHttpApiException(500, "已经没有下一箱了!");
                        }
                        BasePackageCodeEntity box = operator.findBox(configId, boxId);
                        if (box == null) {
                            throw new CustomHttpApiException(500, "已经没有下一箱了!");
                        }
                        return box;
                    }
                })
                .map((Function<BasePackageCodeEntity, List<BasePackageCodeEntity>>) basePackageCodeEntity -> {
                    int offSet = (page - 1) * CustomRecyclerAdapter.ITEM_PAGE_SIZE;
                    List<? extends BasePackageCodeEntity> entityList = operator.querySonListByBoxCode(basePackageCodeEntity.getCode()
                            , offSet, CustomRecyclerAdapter.ITEM_PAGE_SIZE);

                    ArrayList<BasePackageCodeEntity> trueEntityList = new ArrayList<>();
                    if (page == 1) {
                        trueEntityList.add(basePackageCodeEntity);
                    }
                    if (entityList == null || entityList.isEmpty()) {
                        return trueEntityList;
                    }
                    trueEntityList.addAll(entityList);
                    return trueEntityList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<BasePackageCodeEntity>>() {
                    @Override
                    public void onNext(@NonNull List<BasePackageCodeEntity> entities) {
                        getCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, entities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getCodeLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return getCodeLiveData;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public LiveData<Resource<Map<String, Integer>>> calculationTotalByConfigId(long configId, int mType) {
        ValueKeeperLiveData<Resource<Map<String, Integer>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(aLong -> {
                    BasePackageCodeOperator operator = switchTypeWaitUpload(mType);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    //list 第一项为 父码数量 第二项为子码数量
                    return operator.queryBoxAndChildCountByConfig(aLong);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Map>() {
                    @Override
                    public void onNext(Map map) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, map, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    @SuppressWarnings("rawtypes")
    public LiveData<Resource<String>> deleteAllByConfigId(long configId, int type) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(configId)
                .map(id -> {
                    BasePackageCodeOperator operator = switchTypeWaitUpload(type);
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

    public LiveData<Resource<BasePackageCodeEntity>> checkBox(String code, int type) {
        ValueKeeperLiveData<Resource<BasePackageCodeEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(code)
                .map(s -> {
                    BasePackageCodeOperator<? extends BasePackageCodeEntity> operator = switchTypeWaitUpload(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    BasePackageCodeEntity basePackageCodeEntity = operator.queryEntityByCode(code);
                    if (basePackageCodeEntity == null) {
                        //码不存在走错误流程无需包装
                        throw new CustomHttpApiException(500, "码:" + s + "不存在");
                    }
                    return basePackageCodeEntity;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<BasePackageCodeEntity>() {
                    @Override
                    public void onNext(BasePackageCodeEntity codeEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, codeEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> getRemoveAllByBoxCodeLiveDate(String boxCode, int type) {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(boxCode)
                .map(code -> {
                    BasePackageCodeOperator<? extends BasePackageCodeEntity> operator = switchTypeWaitUpload(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    operator.deleteAllSonByPrentCode(code);
                    operator.removeEntityByCode(code);
                    return boxCode;
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<Map<String, BasePackageCodeEntity>>> deleteChildCodeAndAddOne(boolean isNeedAdd, BasePackageCodeEntity bean, int count, int type) {
        ValueKeeperLiveData<Resource<Map<String, BasePackageCodeEntity>>> boxIdLiveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map((Function<String, Map<String, BasePackageCodeEntity>>) s -> {
                    BasePackageCodeOperator<? extends BasePackageCodeEntity> operator = switchTypeWaitUpload(type);
                    if (operator == null) {
                        throw new CustomHttpApiException(500, "初始化数据失败");
                    }
                    HashMap<String, BasePackageCodeEntity> hashMap = new HashMap<>();
                    if (isNeedAdd) {
                        List<? extends BasePackageCodeEntity> list = operator.querySonListByParentCode(bean.getParentCode(), count + 1);
                        if (list == null) {
                            throw new CustomHttpApiException(500, "数据库查询异常");
                        }
                        for (BasePackageCodeEntity entity : list) {
                            if (TextUtils.equals(entity.getCode(), bean.getCode())) {
                                hashMap.put("deleteCode", entity);
                                break;
                            }
                        }
                        if (list.size() >= count + 1) {
                            hashMap.put("addCode", list.get(count));
                        }
                    } else {
                        BasePackageCodeEntity deleteCode = operator.queryEntityByCode(bean.getCode());
                        hashMap.put("deleteCode", deleteCode);
                    }
                    operator.removeEntityByCode(bean.getCode());
                    operator.updateParentCodeIsFull(bean.getParentCode(), false);
                    return hashMap;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Map<String, BasePackageCodeEntity>>() {
                    @Override
                    public void onNext(@NonNull Map<String, BasePackageCodeEntity> map) {
                        boxIdLiveData.postValue(new Resource<>(Resource.SUCCESS, map, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        boxIdLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return boxIdLiveData;

    }


}
