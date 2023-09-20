package com.jgw.delingha.module.stock_in.base.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_in.base.model.StockInCodeModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

public class StockInSettingViewModel extends BaseViewModel {

    private ConfigurationEntity mEntity;
    private ConfigInfoModel mModel;
    private final MutableLiveData<Long> mGetConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mUpdateConfigLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<Long> mWearHouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductBatchLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mStoreHouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mWaitUploadLiveData = new ValueKeeperLiveData<>();

    private StockInCodeModel model = new StockInCodeModel();

    public StockInSettingViewModel(@NonNull Application application) {
        super(application);
        mEntity = new ConfigurationEntity();
        mModel = new ConfigInfoModel();
    }

    /**
     * 触发获取configData
     */
    public void getGetConfigData(long configId) {
        mGetConfigLiveData.setValue(configId);
    }

    /**
     * 获取configData
     */
    public LiveData<Resource<ConfigurationEntity>> getConfigLiveData() {
        return Transformations.switchMap(mGetConfigLiveData, input -> mModel.getConfigInfo(input));
    }

    /**
     * 获取到ConfigurationEntity时替换VM中的对象
     *
     * @param entity 修改设置时上次设置数据
     */
    public void setConfigInfo(ConfigurationEntity entity) {
        mEntity = entity;
    }

    /**
     * 触发获取productInfo
     */
    public void getProductInfoData(long id) {
        mProductInfoLiveData.setValue(id);
    }

    /**
     * 获取productInfo
     */
    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mProductInfoLiveData, input -> mModel.getProductInfoEntity(input));
    }

    /**
     * 触发获取productBatchInfo
     */
    public void getProductBatchData(long id) {
        mProductBatchLiveData.setValue(id);
    }

    /**
     * 获取productBatchInfo
     */
    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfoLiveData() {
        return Transformations.switchMap(mProductBatchLiveData, input -> mModel.getProductBatchInfoEntity(input));
    }

    /**
     * 触发获取WareHouseData
     */
    public void getWareHouseData(long id) {
        mWearHouseLiveData.setValue(id);
    }

    /**
     * 获取WareHouseData
     */
    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoLiveData() {
        return Transformations.switchMap(mWearHouseLiveData, input -> mModel.getWareHouseInfoEntity(input));
    }

    /**
     * 触发获取StoreHouseData
     */
    public void getStoreHouseData(long id) {
        mStoreHouseLiveData.setValue(id);
    }

    /**
     * 获取StoreHouseData
     */
    public LiveData<Resource<StorePlaceEntity>> getStorePlaceInfoLiveData() {
        return Transformations.switchMap(mStoreHouseLiveData, input -> mModel.getStorePlaceInfoEntity(input));
    }

    public void setProductMessage(ProductInfoEntity entity) {
        mEntity.setProductId(entity.getProductId());
        mEntity.setProductCode(entity.getProductCode());
        mEntity.setProductName(entity.getProductName());
        mEntity.setProductClassifyId(entity.getProductSortId());
        mEntity.setProductClassifyName(entity.getProductSortName());
        mEntity.setBatchId("");
        mEntity.setBatchName("");
    }

    public void setProductBatchMessage(ProductBatchEntity entity) {
        mEntity.setBatchId(entity.getBatchId());
        mEntity.setBatchName(entity.getBatchName());
    }

    public void setWareHouseMessage(WareHouseEntity entity) {
        mEntity.setWareHouseId(entity.getWareHouseId());
        mEntity.setWareHouseName(entity.getWareHouseName());
        mEntity.setWareHouseCode(entity.getWareHouseCode());
        mEntity.setStockHouseId("");
        mEntity.setStockHouseName("");
    }

    public void setStorePlaceMessage(StorePlaceEntity entity) {
        mEntity.setStockHouseId(entity.getStoreHouseId());
        mEntity.setStockHouseName(entity.getStoreHouseName());
    }

    public void updateConfig(ConfigurationEntity data) {
        mUpdateConfigLiveData.setValue(data);
    }

    public LiveData<Resource<Long>> getUpdateConfigLiveData() {
        return Transformations.switchMap(mUpdateConfigLiveData, mModel::updateConfig);
    }

    /**
     * 触发储存新的Config数据
     */
    public void getSaveConfigData(ConfigurationEntity entity) {
        mSaveConfigLiveData.postValue(entity);
    }

    /**
     * 储存新的Config数据
     */
    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, input -> mModel.saveConfig(mEntity));
    }

    /**
     * @return 返回当前ConfigEntity
     */
    public ConfigurationEntity getNowConfigData() {
        return mEntity;
    }

    public void hasWaitUpload() {
        mWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mWaitUploadLiveData, input -> model.hasWaitUpload());
    }

}
