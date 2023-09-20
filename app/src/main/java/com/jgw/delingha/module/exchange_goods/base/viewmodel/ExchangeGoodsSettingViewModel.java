package com.jgw.delingha.module.exchange_goods.base.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.exchange_goods.base.model.ExchangeGoodsModel;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsConfigurationEntity;

public class ExchangeGoodsSettingViewModel extends BaseViewModel {

    private final ExchangeGoodsConfigurationEntity goodsConfigurationEntity;
    private final ExchangeGoodsModel model;
    private final MutableLiveData<Long> mWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCallOutCustomerLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCallInCustomerLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<ExchangeGoodsConfigurationEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();

    public ExchangeGoodsSettingViewModel(@NonNull Application application) {
        super(application);
        model = new ExchangeGoodsModel();
        goodsConfigurationEntity = new ExchangeGoodsConfigurationEntity();
    }

    public void hasWaitUpload() {
        mWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<Boolean>> getWaitUploadLiveData() {
        return Transformations.switchMap(mWaitUploadLiveData, input -> model.hasWaitUpload());
    }

    public ExchangeGoodsConfigurationEntity getGoodsConfigurationEntity() {
        return goodsConfigurationEntity;
    }

    public void getCallOutCustomer(long id) {
        mCallOutCustomerLiveData.setValue(id);
    }

    public LiveData<Resource<CustomerEntity>> getCallOutCustomerLivData() {
        return Transformations.switchMap(mCallOutCustomerLiveData, model::getCallOutCustomer);
    }

    public void setCallOutCustomerInfo(CustomerEntity entity) {
        goodsConfigurationEntity.setWareGoodsOutId(entity.getCustomerId());
        goodsConfigurationEntity.setWareGoodsOutCode(entity.getCustomerCode());
        goodsConfigurationEntity.setWareGoodsOutName(entity.getCustomerName());
    }

    public void getCallInCustomer(long id) {
        mCallInCustomerLiveData.setValue(id);
    }

    public LiveData<Resource<CustomerEntity>> getCallInCustomerLivData() {
        return Transformations.switchMap(mCallInCustomerLiveData, model::getCallInCustomer);
    }

    public void setCallInCustomerInfo(CustomerEntity entity) {
        goodsConfigurationEntity.setWareGoodsInId(entity.getCustomerId());
        goodsConfigurationEntity.setWareGoodsInCode(entity.getCustomerCode());
        goodsConfigurationEntity.setWareGoodsInName(entity.getCustomerName());
    }

    public void getSaveConfigData(ExchangeGoodsConfigurationEntity entity) {
        mSaveConfigLiveData.postValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, model::saveConfig);
    }

}
