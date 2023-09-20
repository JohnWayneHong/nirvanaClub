package com.jgw.delingha.module.exchange_warehouse.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.exchange_warehouse.model.ExchangeWarehouseModel;
import com.jgw.delingha.sql.entity.ExchangeWarehouseConfigurationEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

public class ExchangeWarehouseSettingViewModel extends BaseViewModel {

    private final ExchangeWarehouseModel model;
    private final ExchangeWarehouseConfigurationEntity configurationEntity;
    private final MutableLiveData<Long> mWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCallOutWarehouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCallInWarehouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mStoreHouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ExchangeWarehouseConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();

    public ExchangeWarehouseSettingViewModel(@NonNull Application application) {
        super(application);
        model = new ExchangeWarehouseModel();
        configurationEntity = new ExchangeWarehouseConfigurationEntity();
    }

    public void hasWaitUpload() {
        mWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mWaitUploadLiveData, input -> model.hasWaitUpload());
    }

    public ExchangeWarehouseConfigurationEntity getWarehouseConfigurationEntity() {
        return configurationEntity;
    }

    public void getCallOutWarehouse(long id) {
        mCallOutWarehouseLiveData.setValue(id);
    }

    public LiveData<Resource<WareHouseEntity>> getCallOutWarehouseLivData() {
        return Transformations.switchMap(mCallOutWarehouseLiveData, model::getCallOutWarehouse);
    }

    public void setCallOutWarehouseInfo(WareHouseEntity entity) {
        configurationEntity.setOutWareHouseId(entity.getWareHouseId());
        configurationEntity.setOutWareHouseName(entity.getWareHouseName());
        configurationEntity.setOutWareHouseCode(entity.getWareHouseCode());
    }

    public void getCallInWarehouse(long id) {
        mCallInWarehouseLiveData.setValue(id);
    }

    public LiveData<Resource<WareHouseEntity>> getCallInWarehouseLivData() {
        return Transformations.switchMap(mCallInWarehouseLiveData, model::getCallInWarehouse);
    }

    public void setCallInWarehouseInfo(WareHouseEntity entity) {
        configurationEntity.setInWareHouseId(entity.getWareHouseId());
        configurationEntity.setInWareHouseName(entity.getWareHouseName());
        configurationEntity.setInWareHouseCode(entity.getWareHouseCode());
        configurationEntity.setInStoreHouseId("");
        configurationEntity.setInStoreHouseName("");
    }

    public void getStoreHouse(long id) {
        mStoreHouseLiveData.setValue(id);
    }

    public LiveData<Resource<StorePlaceEntity>> getStoreHouseLivData() {
        return Transformations.switchMap(mStoreHouseLiveData, model::getStoreHouse);
    }

    public void setStoreHouseInfo(StorePlaceEntity entity) {
        configurationEntity.setInStoreHouseId(entity.getStoreHouseId());
        configurationEntity.setInStoreHouseName(entity.getStoreHouseName());
    }

    public void getSaveConfigData(ExchangeWarehouseConfigurationEntity entity) {
        mSaveConfigLiveData.postValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, model::saveConfig);
    }

}
