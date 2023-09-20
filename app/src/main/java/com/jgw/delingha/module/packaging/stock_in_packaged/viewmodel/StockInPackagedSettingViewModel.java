package com.jgw.delingha.module.packaging.stock_in_packaged.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.packaging.stock_in_packaged.model.StockInPackagedModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

public class StockInPackagedSettingViewModel extends BaseViewModel {

    private StockInPackagedModel model = new StockInPackagedModel();
    private ConfigInfoModel configModel = new ConfigInfoModel();
    private final MutableLiveData<Boolean> mWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mWareHouseInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mStorePlaceLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();
    private ConfigurationEntity mEntity = new ConfigurationEntity();

    public StockInPackagedSettingViewModel(@NonNull Application application) {
        super(application);
    }

    public void hasWaitUpload() {
        mWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mWaitUploadLiveData, input -> model.hasWaitUpload());
    }

    public ConfigurationEntity getConfigInfo() {
        return mEntity;
    }

    public void getProductInfo(long id) {
        mProductInfoLiveData.setValue(id);
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mProductInfoLiveData, input -> configModel.getProductInfoEntity(input));
    }

    public void setProductInfo(ProductInfoEntity entity) {
        mEntity.setProductId(entity.getProductId());
        mEntity.setProductCode(entity.getProductCode());
        mEntity.setProductName(entity.getProductName());
    }

    public void getWareHouseInfo(long id) {
        mWareHouseInfoLiveData.setValue(id);
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoLiveData() {
        return Transformations.switchMap(mWareHouseInfoLiveData, input -> configModel.getWareHouseInfoEntity(input));
    }

    public void setWareHouseInfo(WareHouseEntity entity) {
        mEntity.setWareHouseId(entity.getWareHouseId());
        mEntity.setWareHouseName(entity.getWareHouseName());
        mEntity.setWareHouseCode(entity.getWareHouseCode());
        mEntity.setStockHouseName("");
        mEntity.setStockHouseId("");
    }

    public void getStorePlaceInfo(long id) {
        mStorePlaceLiveData.setValue(id);
    }

    public LiveData<Resource<StorePlaceEntity>> getStorePlaceInfoLiveData() {
        return Transformations.switchMap(mStorePlaceLiveData, input -> configModel.getStorePlaceInfoEntity(input));
    }

    public void setStorePlaceInfo(StorePlaceEntity entity) {
        mEntity.setStockHouseId(entity.getStoreHouseId());
        mEntity.setStockHouseName(entity.getStoreHouseName());
    }

    public void getSaveConfig(ConfigurationEntity entity) {
        mSaveConfigLiveData.setValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, input -> configModel.saveConfig(input));
    }

}
