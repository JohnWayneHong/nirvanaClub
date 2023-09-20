package com.jgw.delingha.module.exchange_goods.base.viewmodel;

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
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.bean.checkCodeParamsBean;
import com.jgw.delingha.module.exchange_goods.base.model.ExchangeGoodsModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ExchangeGoodsConfigurationEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsEntity;
import com.jgw.scan_code_library.CheckCodeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * author : Cxz
 * data : 2020/2/26
 * description : 调货设置ViewModel
 */
public class ExchangeGoodsViewModel extends BaseViewModel {

    private final static int CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;//展示list最大展示量
    private int mPage = 1;

    private List<ExchangeGoodsEntity> showList = new ArrayList<>();//展示list
    private final ExchangeGoodsModel mModel;
    private ExchangeGoodsConfigurationEntity configurationEntity;//配置Entity类
    private final MutableLiveData<Long> mConfigLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<checkCodeParamsBean> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();
    private long mConfigId;

    public ExchangeGoodsViewModel(@NonNull Application application) {
        super(application);
        configurationEntity = new ExchangeGoodsConfigurationEntity();
        mModel = new ExchangeGoodsModel();
    }

    public void setDataList(List<ExchangeGoodsEntity> list) {
        showList = list;
    }

    public void getConfigData(long configId) {
        mConfigId = configId;
        mConfigLiveData.setValue(configId);
    }

    public long getConfigId() {
        return mConfigId;
    }

    public LiveData<Resource<ExchangeGoodsConfigurationEntity>> getConfigurationEntityLiveData() {
        return Transformations.switchMap(mConfigLiveData, mModel::getConfig);
    }

    public void setConfigurationEntity(ExchangeGoodsConfigurationEntity entity) {
        configurationEntity = entity;
    }

    public void getCodeCount() {
        mGetCountLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<Long>> getCountLiveData() {
        return Transformations.switchMap(mGetCountLiveData, mModel::getCountByConfigId);
    }

    public void handleScanQRCode(String code, CodeEntityRecyclerAdapter<ExchangeGoodsEntity> adapter, CustomBaseRecyclerView recyclerView) {
        if (!CheckCodeUtils.isV3(code)) {
            ToastUtils.showToast(code + ResourcesUtils.getString(R.string.pda_scan_error_code_toast));
            return;
        }
        ExchangeGoodsEntity data = mModel.getExchangeGoodsEntity(code);
        if (data != null) {
            if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                ToastUtils.showToast(code + "该码被其他用户录入,请切换账号或清除离线数据");
            } else {
                ToastUtils.showToast(code + "该码已在库存中!");
            }
            return;
        }
        ExchangeGoodsEntity entity = new ExchangeGoodsEntity();
        entity.setOuterCode(code);
        entity.getConfigEntity().setTarget(configurationEntity);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        if (!mModel.getExchangeGoodsId(entity)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<ExchangeGoodsEntity> tempList = new ArrayList<>();
        if (showList.size() > CAPACITY) {
            for (int i = CAPACITY; i < showList.size(); i++) {
                tempList.add(showList.get(i));
            }
            if (!tempList.isEmpty()) {
                showList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CAPACITY, tempList.size());
            }
        }
        mPage = 1;
        getCodeCount();
        checkCodeParamsBean bean = new checkCodeParamsBean(2, code, configurationEntity.getWareGoodsOutId(), false);
        mCheckCodeLiveData.setValue(bean);

    }

    public boolean checkCodeExisted(String code) {
        ExchangeGoodsEntity data = mModel.getExchangeGoodsEntity(code);
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

    public LiveData<Resource<String>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, mModel::getCheckCode);
    }

    public void updateCode(String code, CodeEntityRecyclerAdapter<ExchangeGoodsEntity> adapter, int status) {
        int codeStatus = 0;
        switch (status) {
            case Resource.SUCCESS:
                codeStatus = CodeBean.STATUS_CODE_SUCCESS;
                break;
            case Resource.NETWORK_ERROR:
                codeStatus = CodeBean.STATUS_CODE_FAIL;
                break;
        }
        mModel.updateCodeStatusByCode(code, codeStatus);
        adapter.notifyRefreshItemStatus(code, codeStatus);
    }

    /**
     * 删除list的某一项
     */
    public void deleteCode(String code, CodeEntityRecyclerAdapter<ExchangeGoodsEntity> mAdapter) {
        ExchangeGoodsEntity exchangeGoodsEntity = new ExchangeGoodsEntity();
        exchangeGoodsEntity.setOuterCode(code);
        int i = showList.indexOf(exchangeGoodsEntity);
        if (i != -1) {
            mAdapter.notifyRemoveItem(i);
        }
        mModel.removeEntityByCode(code);
    }

    public void getRefreshListData() {
        mRefreshListDataLiveData.setValue(mConfigId);
        mPage = 1;
    }

    /**
     * 获取反扫回来的更新
     */
    public LiveData<Resource<List<ExchangeGoodsEntity>>> getRefreshListDataLiveData() {
        return Transformations.switchMap(mRefreshListDataLiveData, mModel::getRefreshListData);
    }

    public void getTaskId() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(configurationEntity.getId());
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mModel::getTaskId);
    }

    /**
     * 触发上传
     */
    public void uploadData() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(configurationEntity.getId());
    }

    /**
     * 上传
     */
    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, mModel::uploadListByConfigId);
    }

    /**
     * 当前页数据完整时尝试请求下一页数据
     */
    public void loadMoreList() {
        if (showList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadMoreLiveData.setValue(null);
        }
    }

    public LiveData<Resource<List<ExchangeGoodsEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> mModel.loadMoreList(mConfigId, mPage, CustomRecyclerAdapter.ITEM_PAGE_SIZE));
    }

}
