package com.jgw.delingha.module.packaging.stock_in_packaged.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.packaging.stock_in_packaged.model.StockInPackagedModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockInPackagedEntity;
import com.jgw.delingha.sql.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class StockInPackagedPDAViewModel extends BaseViewModel {

    private List<StockInPackagedEntity> mList = new ArrayList<>();

    private int mCurrentPage = 1;

    private long configId;
    private ConfigurationEntity mConfigEntity;
    private final StockInPackagedModel mModel;
    private final ConfigInfoModel mConfigModel;
    private final MutableLiveData<Long> mConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();

    public StockInPackagedPDAViewModel(@NonNull Application application) {
        super(application);
        mConfigModel = new ConfigInfoModel();
        mModel = new StockInPackagedModel();
    }

    public void getConfigData(long configId) {
        this.configId = configId;
        mConfigLiveData.setValue(configId);
    }

    public long getConfigId() {
        return configId;
    }


    public LiveData<Resource<ConfigurationEntity>> getConfigurationEntityLiveData() {
        return Transformations.switchMap(mConfigLiveData, mConfigModel::getConfigInfo);
    }

    public void setConfigIdEntity(ConfigurationEntity entity) {
        mConfigEntity = entity;
    }

    public ConfigurationEntity getConfigEntity() {
        return mConfigEntity;
    }

    public void setDataList(List<StockInPackagedEntity> list) {
        mList = list;
    }


    public void getCount() {
        mCountLiveData.setValue(configId);
    }

    public LiveData<Resource<Long>> getCountLiveData() {
        return Transformations.switchMap(mCountLiveData, mModel::getCount);
    }

    public void handleScanQRCode(String code, CodeEntityRecyclerAdapter<StockInPackagedEntity> adapter, CustomBaseRecyclerView recyclerView) {
        StockInPackagedEntity entity = new StockInPackagedEntity();
        entity.setCode(code);
        entity.getConfigEntity().setTarget(mConfigEntity);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.setProductId(mConfigEntity.getProductId());
        entity.setProductCode(mConfigEntity.getProductCode());
        entity.setProductName(mConfigEntity.getProductName());
        mModel.putData(entity);
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<StockInPackagedEntity> tempList = new ArrayList<>();
        if (mList.size() > CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            for (int i = CustomRecyclerAdapter.ITEM_PAGE_SIZE; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CustomRecyclerAdapter.ITEM_PAGE_SIZE, tempList.size());
            }
        }
        mCurrentPage = 1;
        mCheckCodeLiveData.setValue(code);
        getCount();
    }

    public boolean checkCodeExisted(String code){
        StockInPackagedEntity data = mModel.getCodeData(code);
        if (data != null) {
            ConfigurationEntity configInfo = data.getConfigEntity().getTarget();
            UserEntity userInfo = configInfo.getUserEntity().getTarget();
            if (userInfo.getId() != LocalUserUtils.getCurrentUserId()) {
                ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
            } else {
                ToastUtils.showToast(code + "该码已在库存中!");
            }
            return true;
        }
        return false;
    }

    public LiveData<Resource<CodeBean>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, input -> mModel.getCheckCode(input, mConfigEntity.getProductId()));
    }

    public void removeCode(String code) {
        mModel.removeEntityByCode(code);
    }

    /**
     * 更新码状态 - 成功
     */
    public void updateCodeSuccess(String code) {
        mModel.updateCodeStatusByCode(code, CodeBean.STATUS_CODE_SUCCESS);
    }

    public boolean checkCode() {
        return (checkEmptyCode() || checkVerifyingCode());
    }

    public boolean checkEmptyCode() {
        if (mList.isEmpty()) {
            ToastUtils.showToast("请先扫码!");
            return true;
        }
        return false;
    }

    public boolean checkVerifyingCode() {
        if (getVerifyingOrFailDataCount() != 0) {
            ToastUtils.showToast("还有码在校验中,请稍后再试");
            return true;
        }
        return false;
    }

    public long getVerifyingOrFailDataCount() {
        return mModel.getVerifyingOrFailDataCount(configId);
    }

    public void getTaskId() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(configId);
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getTaskId);
    }

    public void uploadData() {
        mUploadLiveData.setValue(configId);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, mModel::uploadListByConfigId);
    }

    /**
     * 反扫后更新本地 list 中的数据
     */
    public void refreshListData() {
        mRefreshListDataLiveData.setValue(configId);
        mCurrentPage = 1;
    }

    public LiveData<Resource<List<StockInPackagedEntity>>> getRefreshListDataLiveData() {
        return Transformations.switchMap(mRefreshListDataLiveData, mModel::getRefreshListData);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        mLoadMoreListLiveData.setValue(configId);
    }

    public LiveData<Resource<List<StockInPackagedEntity>>> getLoadMoreListLiveData() {
        return Transformations.switchMap(mLoadMoreListLiveData, input -> mModel.loadList(input, mCurrentPage));
    }


}
