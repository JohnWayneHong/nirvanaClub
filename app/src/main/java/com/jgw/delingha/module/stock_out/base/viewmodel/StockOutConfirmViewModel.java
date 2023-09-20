package com.jgw.delingha.module.stock_out.base.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_out.base.model.StockOutPDAModel;
import com.jgw.delingha.sql.entity.CodeEntity;
import com.jgw.delingha.sql.entity.ConfigurationEntity;

import java.util.List;

public class StockOutConfirmViewModel extends BaseViewModel {

    private final StockOutPDAModel model;
    private final ConfigInfoModel mConfigModel;

    private final MutableLiveData<Long> mFormatListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mHeaderDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private long mConfigId;

    public StockOutConfirmViewModel(@NonNull Application application) {
        super(application);
        model = new StockOutPDAModel();
        mConfigModel = new ConfigInfoModel();
    }

    public LiveData<Resource<List<CodeEntity>>> getFormatListLiveData() {
        return Transformations.switchMap(mFormatListLiveData, model::getFormatList);
    }

    /**
     * 根据configId获取头部数据
     *
     * @param configId 设置界面获取的configId
     */
    public void setConfigId(long configId) {
        mConfigId = configId;
        mHeaderDataLiveData.setValue(configId);
        mFormatListLiveData.setValue(configId);
    }

    public LiveData<Resource<ConfigurationEntity>> getHeaderDataLiveData() {
        return Transformations.switchMap(mHeaderDataLiveData, mConfigModel::getConfigInfo);
    }


    public void getTaskId() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getTaskId);
    }


    /**
     * 仅上传扫码界面校验成功的码
     */
    public void uploadCodes() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, configId -> model.uploadListByConfigId(configId, CodeBean.STATUS_CODE_SUCCESS));
    }

    public void clearSQLCache() {
        model.clearCache();
    }

}
