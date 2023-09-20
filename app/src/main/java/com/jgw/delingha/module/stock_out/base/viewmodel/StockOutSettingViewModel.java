package com.jgw.delingha.module.stock_out.base.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_out.base.model.StockOutPDAModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;

public class StockOutSettingViewModel extends BaseViewModel {

    private final ConfigInfoModel model;
    private final StockOutPDAModel mStockOutModel;

    private final MutableLiveData<Long> mHasWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetConfigInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetCustomerInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetLogisticsCompanyInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mUpdateConfigLiveData = new ValueKeeperLiveData<>();

    private ConfigurationEntity mEntity = new ConfigurationEntity();
    private long mConfigId;

    public StockOutSettingViewModel(@NonNull Application application) {
        super(application);
        model = new ConfigInfoModel();
        mStockOutModel = new StockOutPDAModel();
    }

    public void hasWaitUpload() {
        mHasWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mHasWaitUploadLiveData, input -> mStockOutModel.hasWaitUpload());
    }

    /**
     * 通过configId获取获取到ConfigurationEntity
     *
     * @param configId 修改设置时先通过上次的设置id获取数据
     */
    public void getConfigInfo(long configId) {
        mGetConfigInfoLiveData.setValue(configId);
        mConfigId = configId;
    }

    public long getConfigId() {
        return mConfigId;
    }

    public LiveData<Resource<ConfigurationEntity>> getConfigInfoLiveData() {
        return Transformations.switchMap(mGetConfigInfoLiveData, model::getConfigInfo);
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
     * 通过customerId获取获取到CustomerEntity
     *
     * @param customerId 本地数据库客户ID
     */
    public void getCustomerInfo(long customerId) {
        mGetCustomerInfoLiveData.setValue(customerId);
    }

    public LiveData<Resource<CustomerEntity>> getCustomerInfoLiveData() {
        return Transformations.switchMap(mGetCustomerInfoLiveData, model::getCustomerInfo);
    }

    /**
     * 获取到客户数据时直接存入ConfigurationEntity
     *
     * @param entity 客户数据
     */
    public void setCustomerInfo(CustomerEntity entity) {
        mEntity.setCustomerName(entity.getCustomerName());
        mEntity.setCustomerCode(entity.getCustomerCode());
        mEntity.setCustomerId(entity.getCustomerId());
        mEntity.setContacts(entity.getContacts());
    }

    /**
     * 通过id获取获取到LogisticsCompanyEntity
     *
     * @param id 本地数据库物流公司id
     */
    public void getLogisticsCompanyInfo(long id) {
        mGetLogisticsCompanyInfoLiveData.setValue(id);
    }

    public LiveData<Resource<LogisticsCompanyEntity>> getLogisticsCompanyInfoLiveData() {
        return Transformations.switchMap(mGetLogisticsCompanyInfoLiveData, model::getLogisticsCompanyInfo);
    }

    public void setLogisticsCompanyInfo(LogisticsCompanyEntity entity) {
        mEntity.setLogisticsCompanyCode(entity.getShipperCode());
        mEntity.setLogisticsCompanyName(entity.getShipperName());
    }

    /**
     * 保存本地设置的数据 并生成新的设置ID
     *
     * @param entity 本地设置的数据
     */
    public void saveConfig(ConfigurationEntity entity) {
        mSaveConfigLiveData.setValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, model::saveConfig);
    }

    public ConfigurationEntity getConfigEntity() {
        return mEntity;
    }


    public void updateConfig(ConfigurationEntity data) {
        mUpdateConfigLiveData.setValue(data);
    }

    public LiveData<Resource<Long>> getUpdateConfigLiveData() {
        return Transformations.switchMap(mUpdateConfigLiveData, model::updateConfig);
    }
}
