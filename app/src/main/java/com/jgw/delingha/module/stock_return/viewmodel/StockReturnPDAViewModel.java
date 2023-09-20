package com.jgw.delingha.module.stock_return.viewmodel;

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
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_return.model.StockReturnV3CodeModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockReturnEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * author : Cxz
 * data : 2019/11/21
 * description :
 */
public class StockReturnPDAViewModel extends BaseViewModel {

    private final ConfigInfoModel mConfigModel;

    private ConfigurationEntity configurationEntity;
    private final StockReturnV3CodeModel mModel;
    private List<StockReturnEntity> mList;//展示List

    private long mConfigId;
    private boolean isUpdateCustomerId = false;

    private String oldCustomerId;
    private final MutableLiveData<Long> mGetCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<checkCodeParamsBean> mGetStockReturnCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadListLiveData = new ValueKeeperLiveData<>();

    private int mPage = 1;

    public StockReturnPDAViewModel(@NonNull Application application) {
        super(application);
        mModel = new StockReturnV3CodeModel();
        mConfigModel = new ConfigInfoModel();
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
        return Transformations.switchMap(mConfigLiveData, mConfigModel::getConfigInfo);
    }

    /**
     * 获取成功的config绑定在成员变量configurationEntity上
     */
    public void setConfigurationEntity(ConfigurationEntity entity) {
        oldCustomerId = entity.getCustomerId();
        configurationEntity = entity;
    }

    public void setDataList(List<StockReturnEntity> list) {
        mList = list;
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
    public void handleScanQRCode(String code, CodeEntityRecyclerAdapter<StockReturnEntity> adapter, CustomBaseRecyclerView recyclerView) {
        StockReturnEntity entity = new StockReturnEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.getConfigEntity().setTarget(configurationEntity);
        if (!mModel.getStockReturnId(entity)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));

            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<StockReturnEntity> tempList = new ArrayList<>();
        if (mList.size() > CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            for (int i = CustomRecyclerAdapter.ITEM_PAGE_SIZE; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CustomRecyclerAdapter.ITEM_PAGE_SIZE, tempList.size());
            }
        }

        checkCodeParamsBean bean = new checkCodeParamsBean(1, code, configurationEntity.getCustomerId(), false);
        mGetStockReturnCodeLiveData.setValue(bean);

        getCodeCount();
    }

    public boolean checkCodeExisted(String code) {
        StockReturnEntity data = mModel.getStockReturnEntity(code);
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

    /**
     * 获取客户仓库的码
     */
    public LiveData<Resource<String>> getStockReturnCodeLiveData() {
        return Transformations.switchMap(mGetStockReturnCodeLiveData, mModel::getStockReturnCode);
    }

    /**
     * 删除list的某一项
     */
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
    public LiveData<Resource<Long>> getCountLiveData() {
        return Transformations.switchMap(mGetCountLiveData, mModel::getCountTotal);
    }


    /**
     * 触发获取反扫的更新
     */
    public void getRefreshListData() {
        mPage = 1;
        mLoadListLiveData.setValue(null);
    }

    /**
     * 更新反扫返回的List
     */
    public void refreshList(List<StockReturnEntity> list) {
        mList.clear();
        mList.addAll(list);
    }

    /**
     * @return 还在码验证true 反之false
     */
    public boolean hasVerifyingCode() {
        return mModel.hasVerifyingCode(mConfigId);
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

    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadListLiveData.setValue(null);
        }
    }

    public LiveData<Resource<List<StockReturnEntity>>> getLoadListLiveData() {
        return Transformations.switchMap(mLoadListLiveData, input -> mModel.loadList(mConfigId, mPage, CustomRecyclerAdapter.ITEM_PAGE_SIZE));
    }

    public int getPage() {
        return mPage;
    }
}
