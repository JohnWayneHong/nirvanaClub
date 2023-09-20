package com.jgw.delingha.module.label_edit.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.label_edit.model.LabelEditModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;


/**
 * author : Cxz
 * data : 2019/12/17
 * description : 包装关联设置ViewModel
 */
public class LabelEditSettingViewModel extends BaseViewModel {

    private final ConfigInfoModel mConfigModel;
    private final LabelEditModel model;

    private final MutableLiveData<Long> mCheckWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetProductBatchInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();

    private final ConfigurationEntity mEntity = new ConfigurationEntity();

    public LabelEditSettingViewModel(@NonNull Application application) {
        super(application);
        mConfigModel = new ConfigInfoModel();
        model = new LabelEditModel();
    }

    public void checkWaitUpload() {
        mCheckWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getCheckWaitUploadLiveData() {
        return Transformations.switchMap(mCheckWaitUploadLiveData, input -> model.hasWaitUpload());
    }

    public void getProductInfo(long productId) {
        mGetProductInfoLiveData.setValue(productId);
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mGetProductInfoLiveData, mConfigModel::getProductInfoEntity);
    }

    public void setProductInfo(ProductInfoEntity entity) {
        mEntity.setProductId(entity.getProductId());
        mEntity.setProductName(entity.getProductName());
        mEntity.setProductCode(entity.getProductCode());
        mEntity.setBatchId(null);
        mEntity.setBatchName(null);
    }


    public void getProductBatchInfo(long id) {
        mGetProductBatchInfoLiveData.setValue(id);
    }

    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfoLiveData() {
        return Transformations.switchMap(mGetProductBatchInfoLiveData, mConfigModel::getProductBatchInfoEntity);
    }

    public void setProductBatchInfo(ProductBatchEntity entity) {
        mEntity.setBatchName(entity.getBatchName());
        mEntity.setBatchId(entity.getBatchId());
    }

    public void saveConfig(ConfigurationEntity entity) {
        mSaveConfigLiveData.setValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, mConfigModel::saveConfig);
    }

    public ConfigurationEntity getConfigEntity() {
        return mEntity;
    }

    public String getProductId() {
        return mEntity.getProductId();
    }

}
