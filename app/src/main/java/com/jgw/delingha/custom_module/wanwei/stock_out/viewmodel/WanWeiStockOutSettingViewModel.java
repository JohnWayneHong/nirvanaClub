package com.jgw.delingha.custom_module.wanwei.stock_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.custom_module.wanwei.stock_out.model.WanWeiStockOutPDAModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;

public class WanWeiStockOutSettingViewModel extends BaseViewModel {

    private final ConfigInfoModel model;
    private final WanWeiStockOutPDAModel mStockOutFastModel;
    private ConfigurationEntity mEntity = new ConfigurationEntity();

    private final MutableLiveData<Long> mHasWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetCustomerInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductBatchLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetLogisticsCompanyInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();

    public WanWeiStockOutSettingViewModel(@NonNull Application application) {
        super(application);
        model = new ConfigInfoModel();
        mStockOutFastModel = new WanWeiStockOutPDAModel();
    }

    public void hasWaitUpload() {
        mHasWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mHasWaitUploadLiveData, input -> mStockOutFastModel.hasWaitUpload());
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
     * 触发获取productInfo
     */
    public void getProductInfo(long id) {
        mProductInfoLiveData.setValue(id);
    }

    /**
     * 获取productInfo
     */
    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mProductInfoLiveData, model::getProductInfoEntity);
    }

    public void setProductInfo(ProductInfoEntity entity) {
        mEntity.setProductId(entity.getProductId());
        mEntity.setProductCode(entity.getProductCode());
        mEntity.setProductName(entity.getProductName());
        mEntity.setProductClassifyId(entity.getProductSortId());
        mEntity.setProductClassifyName(entity.getProductSortName());
        mEntity.setBatchId("");
        mEntity.setBatchName("");
    }

    /**
     * 触发获取productBatchInfo
     */
    public void getProductBatchInfo(long id) {
        mProductBatchLiveData.setValue(id);
    }

    /**
     * 获取productBatchInfo
     */
    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfoLiveData() {
        return Transformations.switchMap(mProductBatchLiveData, model::getProductBatchInfoEntity);
    }

    public void setProductBatchInfo(ProductBatchEntity entity) {
        mEntity.setBatchId(entity.getBatchId());
        mEntity.setBatchName(entity.getBatchName());
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

    public String getProductId() {
        return mEntity.getProductId();
    }

    public void setPlanNumber(Integer planNumber) {
        mEntity.setPlanNumber(planNumber);
    }
}
