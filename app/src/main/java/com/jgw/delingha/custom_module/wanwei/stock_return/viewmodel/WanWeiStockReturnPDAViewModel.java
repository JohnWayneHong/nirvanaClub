package com.jgw.delingha.custom_module.wanwei.stock_return.viewmodel;

import android.app.Application;
import android.text.TextUtils;

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
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.custom_module.wanwei.stock_return.model.WanWeiStockReturnV3CodeModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.WanWeiStockReturnEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * author : Cxz
 * data : 2019/11/21
 * description :
 */
public class WanWeiStockReturnPDAViewModel extends BaseViewModel {

    private final static int CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;//展示list最大展示量
    private final ConfigInfoModel mConfigModel;

    private ConfigurationEntity configurationEntity;
    private final WanWeiStockReturnV3CodeModel mModel;
    private List<WanWeiStockReturnEntity> mList;//展示List
    private long mConfigId;
    private boolean isUpdateCustomerId = false;

    private String oldCustomerId;
    private final MutableLiveData<Long> mGetCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<checkCodeParamsBean> mGetStockReturnCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();

    private long mSingleNumber;
    private int mPage = 1;


    public WanWeiStockReturnPDAViewModel(@NonNull Application application) {
        super(application);
        mModel = new WanWeiStockReturnV3CodeModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<WanWeiStockReturnEntity> list) {
        mList = list;
    }

    public void getConfigData(long id) {
        if (id == -1) {
            return;
        }
        mConfigId = id;
        mConfigLiveData.setValue(mConfigId);
    }

    public long getConfigId() {
        return mConfigId;
    }


    /**
     * 通过configId获取config的data数据
     */
    public LiveData<Resource<ConfigurationEntity>> getConfigurationLiveData() {
        return Transformations.switchMap(mConfigLiveData, mModel::getConfigurationEntityData);
    }

    /**
     * 获取成功的config绑定在成员变量configurationEntity上
     */
    public void setConfigurationEntity(ConfigurationEntity entity) {
        oldCustomerId = entity.getCustomerId();
        configurationEntity = entity;
    }

    /**
     * @return 有数据的情况下更改了用户返回true
     */
    public boolean isUpdateUpload(long id) {
        if (mList.size() != 0) {
            ConfigurationEntity entity = mConfigModel.getConfigEntity(id);
            isUpdateCustomerId = !TextUtils.equals(entity.getCustomerId(), oldCustomerId);
            return isUpdateCustomerId;
        } else {
            return false;
        }
    }

    public boolean getIsUpdateCustomerId() {
        return isUpdateCustomerId;
    }

    public void setIsUpdateCustomerId(boolean changed) {
        isUpdateCustomerId = changed;
    }

    /**
     * 传递扫码返回code 进行码插入
     */
    public void handleScanQRCode(String code, CustomRecyclerAdapter<WanWeiStockReturnEntity> adapter, CustomBaseRecyclerView recyclerView) {
        WanWeiStockReturnEntity entity = new WanWeiStockReturnEntity();
        entity.setCode(code);
        entity.getConfigEntity().setTargetId(configurationEntity.getId());
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        mModel.getWanWeiStockReturnId(entity);
        getCodeCount();
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        mPage=1;
        List<WanWeiStockReturnEntity> tempList = new ArrayList<>();
        if (mList.size() > CAPACITY) {
            for (int i = CAPACITY; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CAPACITY, tempList.size());
            }
        }

        checkCodeParamsBean bean = new checkCodeParamsBean(1, code, configurationEntity.getCustomerId(), false);
        mGetStockReturnCodeLiveData.setValue(bean);
    }

    public boolean checkCodeExisted(String code){
        WanWeiStockReturnEntity data = mModel.getWanWeiStockReturnEntity(code);
        if (data != null) {
            if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
            } else {
                ToastUtils.showToast(code + "该码已在库存中!");
            }
            return true;
        }
        return false;
    }

    public LiveData<Resource<CodeBean>> getStockReturnCodeLiveData() {
        return Transformations.switchMap(mGetStockReturnCodeLiveData, mModel::getWanWeiStockReturnCode);
    }

    public void deleteListItem(String code) {
        mModel.removeEntityByCode(code);
    }

    /**
     * 触发获取count并更新
     */
    public void getCodeCount() {
        mGetCountLiveData.setValue(mConfigId);
    }

    /**
     * 获取count
     */
    public LiveData<Resource<Map<String, Long>>> getCountLiveData() {
        return Transformations.switchMap(mGetCountLiveData, mModel::getCountTotal);
    }


    /**
     * 触发获取反扫的更新
     */
    public void getRefreshListData() {
        mPage=1;
        mRefreshListDataLiveData.setValue(mConfigId);
    }

    /**
     * 获取反扫的更新
     */
    public LiveData<Resource<List<WanWeiStockReturnEntity>>> getRefreshListDataLiveData() {
        return Transformations.switchMap(mRefreshListDataLiveData, mModel::getRefreshListData);
    }


    /**
     * @return 还在码验证true 反之false
     */
    public boolean isVerifyingId() {
        return mModel.getVerifyingId(mConfigId);
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
     * 触发上传
     */
    public void uploadData() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(mConfigId);
    }

    /**
     * 上传
     */
    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, mModel::uploadListByConfigId);
    }

    public void clearList() {
        mModel.deleteData(mConfigId);
    }

    public void updateItemStatus(String outerCodeId, int status) {
        mModel.updateCodeStatus(outerCodeId, status);
    }

    public void setScanSingleNumber(long singleNumber) {
        mSingleNumber = singleNumber;
    }

    public long getSingleNumber() {
        return mSingleNumber;
    }

    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadMoreLiveData.setValue(null);
        }
    }

    public LiveData<Resource<List<WanWeiStockReturnEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> {
            int pageSize = CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            return mModel.loadMoreList(mConfigId, pageSize, mPage);
        });
    }

}
