package com.jgw.delingha.module.batch_management.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.BatchManagementBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.batch_management.model.BatchManagementModel;
import com.jgw.delingha.sql.entity.ProductInfoEntity;

/**
 * Created by xswwg
 * on 2020/12/15
 */
public class BatchManagementAddBatchViewModel extends BaseViewModel {

    private final BatchManagementModel model;
    private final ConfigInfoModel mConfigModel;
    private BatchManagementBean mData;
    private final MutableLiveData<BatchManagementBean> mAddBatchLiveData = new MutableLiveData<>();
    private final MutableLiveData<BatchManagementBean> mEditBatchLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mGetProductInfoLiveData = new ValueKeeperLiveData<>();

    public BatchManagementAddBatchViewModel(@NonNull Application application) {
        super(application);
        model = new BatchManagementModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setData(BatchManagementBean data) {
        mData = data;
    }

    public void addBatch() {
        mAddBatchLiveData.setValue(mData);
    }

    public LiveData<Resource<String>> getAddBatchLiveData() {
        return Transformations.switchMap(mAddBatchLiveData, model::addBatch);
    }

    public void getProductInfo(long productId) {
        mGetProductInfoLiveData.setValue(productId);
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mGetProductInfoLiveData, mConfigModel::getProductInfoEntity);
    }

    public void setProductInfo(ProductInfoEntity productInfo) {
        mData.productId = productInfo.getProductId();
        mData.productName = productInfo.getProductName();
    }

    public void editBatch() {
        mEditBatchLiveData.setValue(mData);
    }

    public LiveData<Resource<String>> getEditBatchLiveData() {
        return Transformations.switchMap(mEditBatchLiveData, model::editBatch);
    }

    public void setMarketDate(String time) {
        mData.marketDate=time;
    }
    public void setProduceDate(String time) {
        mData.produceDate=time;
    }
}
