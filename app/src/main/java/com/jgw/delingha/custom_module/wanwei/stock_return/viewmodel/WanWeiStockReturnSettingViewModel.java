package com.jgw.delingha.custom_module.wanwei.stock_return.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.custom_module.wanwei.stock_return.model.WanWeiStockReturnV3CodeModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

/**
 * author : Cxz
 * data : 2019/11/18
 * description :
 */
public class WanWeiStockReturnSettingViewModel extends BaseViewModel {

    private final ConfigInfoModel mConfigModel;

    private final WanWeiStockReturnV3CodeModel model;
    private ConfigurationEntity mEntity;

    private final MutableLiveData<Long> mConfigIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Boolean> mWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCustomerInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetWareHouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetStorePlaceLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mUpdateConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mSaveConfigInfoLiveData = new ValueKeeperLiveData<>();


    public WanWeiStockReturnSettingViewModel(@NonNull Application application) {
        super(application);
        mEntity = new ConfigurationEntity();
        model = new WanWeiStockReturnV3CodeModel();
        mConfigModel = new ConfigInfoModel();
    }


    /**
     * 触发liveData判断是否在待执行
     */
    public void hasWaitUpload() {
        mWaitUploadLiveData.setValue(null);
    }

    /**
     * 判断是否在待执行
     */
    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mWaitUploadLiveData, input -> model.hasWaitUpload());
    }


    public void setConfigId(long id) {
        if (id != -1) {
            mConfigIdLiveData.setValue(id);
        }
    }

    /**
     * 通过configId获取config的data数据
     */
    public LiveData<Resource<ConfigurationEntity>> getConfigLiveData() {
        return Transformations.switchMap(mConfigIdLiveData, model::getConfigurationEntityData);
    }

    public void setConfigInfo(ConfigurationEntity entity) {
        mEntity = entity;
    }

    public ConfigurationEntity getConfigInfo() {
        return mEntity;
    }

    /**
     * 触发获取customer数据
     */
    public void getCustomerInfoData(Long customerId) {
        mCustomerInfoLiveData.setValue(customerId);
    }

    /**
     * 获取customer数据
     */
    public LiveData<Resource<CustomerEntity>> getCustomerInfoLiveData() {
        return Transformations.switchMap(mCustomerInfoLiveData, model::getCustomerInfo);
    }

    /**
     * 数据保存在成员变量
     *
     * @param entity mCustomerInfoLiveData回传的data
     */
    public void setCustomerData(CustomerEntity entity) {
        mEntity.setCustomerName(entity.getCustomerName());
        mEntity.setCustomerCode(entity.getCustomerCode());
        mEntity.setCustomerId(entity.getCustomerId());
    }

    /**
     * 触发获取WareHouseData数据
     */
    public void getWareHouseData(Long wareHouseId) {
        mGetWareHouseLiveData.setValue(wareHouseId);
    }

    /**
     * 获取WareHouseData数据
     */
    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoLiveData() {
        return Transformations.switchMap(mGetWareHouseLiveData, model::getWareHouseData);
    }

    /**
     * 数据保存在成员变量
     *
     * @param entity mGetWareHouseLiveData回传的data
     */
    public void setWareHouseData(WareHouseEntity entity) {
        mEntity.setStockHouseName("");
        mEntity.setStockHouseId("");
        mEntity.setWareHouseCode(entity.getWareHouseCode());
        mEntity.setWareHouseName(entity.getWareHouseName());
        mEntity.setWareHouseId(entity.getWareHouseId());
    }

    /**
     * 触发获取StorePlaceData数据
     */
    public void getStorePlaceData(Long storePlaceId) {
        mGetStorePlaceLiveData.setValue(storePlaceId);
    }

    public LiveData<Resource<StorePlaceEntity>> getStorePlaceInfoLiveData() {
        return Transformations.switchMap(mGetStorePlaceLiveData, model::getStorePlaceData);
    }

    public void setStorePlaceData(StorePlaceEntity entity) {
        mEntity.setStockHouseId(entity.getStoreHouseId());
        mEntity.setStockHouseName(entity.getStoreHouseName());
    }

    public void updateConfig(ConfigurationEntity data) {
        mUpdateConfigLiveData.setValue(data);
    }

    public LiveData<Resource<Long>> getUpdateConfigLiveData() {
        return Transformations.switchMap(mUpdateConfigLiveData, entity -> {
            if (!TextUtils.equals(entity.getCustomerId(), mEntity.getCustomerId())) {
                entity.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
                entity.setCreateTime(System.currentTimeMillis());
                entity.setId(0);
                return mConfigModel.saveConfig(entity);
            } else {
                return mConfigModel.updateConfig(entity);
            }
        });
    }

    public void saveConfigInfo(ConfigurationEntity entity) {
        entity.getUserEntity().setTarget(LocalUserUtils.getCurrentUserEntity());
        entity.setCreateTime(System.currentTimeMillis());
        mSaveConfigInfoLiveData.setValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigInfoLiveData, mConfigModel::saveConfig);
    }

    public void setPlanNumber(int planNumber) {
        mEntity.setPlanNumber(planNumber);
    }
}
