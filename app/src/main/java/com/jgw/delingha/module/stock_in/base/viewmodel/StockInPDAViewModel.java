package com.jgw.delingha.module.stock_in.base.viewmodel;

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
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_in.base.model.StockInCodeModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockInEntity;

import java.util.ArrayList;
import java.util.List;

public class StockInPDAViewModel extends BaseViewModel {

    private static final int PAGE_CAPACITY = CustomRecyclerAdapter.ITEM_PAGE_SIZE;
    private final ConfigInfoModel mConfigModel;

    private List<StockInEntity> mList;

    private long mConfigId;
    private int mCurrentPage = 1;

    private final MutableLiveData<Long> mConfigurationEntityLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetCountLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mLoadMoreListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUpLoadLiveData = new ValueKeeperLiveData<>();

    private final StockInCodeModel model;
    private ConfigurationEntity mConfigInfo;

    public StockInPDAViewModel(@NonNull Application application) {
        super(application);
        model = new StockInCodeModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<StockInEntity> list) {
        mList = list;
    }

    public long getConfigId() {
        return mConfigId;
    }

    public void getConfigData(long configId) {
        mConfigId = configId;
        mConfigurationEntityLiveData.setValue(configId);
    }

    /**
     * 获取入库设置时保存在数据库的数据，在扫码入库中显示
     */
    public LiveData<Resource<ConfigurationEntity>> getConfigurationEntityLiveData() {
        return Transformations.switchMap(mConfigurationEntityLiveData, mConfigModel::getConfigInfo);
    }

    public void getCount() {
        mGetCountLiveData.setValue(mConfigId);
    }

    /**
     * 根据ConfigId获取码数量
     */
    public LiveData<Resource<Integer>> getCountLiveData() {
        return Transformations.switchMap(mGetCountLiveData, model::getCount);
    }

    /**
     * 普通（非兼容版本）扫码操作
     * tip : 只保留 20 条记录在当前 list 中
     */
    public void handleScanQRCodeCommon(String code, CodeEntityRecyclerAdapter<StockInEntity> adapter, CustomBaseRecyclerView recyclerView) {
        //更改计数
        StockInEntity entity = new StockInEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
        entity.getConfigEntity().setTarget(mConfigInfo);
        if (!model.putData(entity)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<StockInEntity> tempList = new ArrayList<>();
        if (mList.size() > PAGE_CAPACITY) {
            for (int i = PAGE_CAPACITY; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(PAGE_CAPACITY, tempList.size());
            }
        }
        getCount();
        mCurrentPage = 1;
    }

    public boolean checkCodeExisted(String code){
        StockInEntity data = model.getCodeData(code);
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
     * 加载更多
     */
    public void onLoadMore() {
        if (mList.size() != PAGE_CAPACITY * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        mLoadMoreListLiveData.setValue(null);
    }

    /**
     * 加载更多时获取更多list数据
     */
    public LiveData<Resource<List<StockInEntity>>> getLoadMoreListLiveData() {
        return Transformations.switchMap(mLoadMoreListLiveData, input -> model.loadList(mConfigId, mCurrentPage));
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
        if (getVerifyingId() != 0) {
            ToastUtils.showToast("还有码在校验中,请稍后再试");
            return true;
        }
        return false;
    }

    public long getVerifyingId() {
        return model.getVerifyingId(mConfigId);
    }

    /**
     * 触发反扫
     */
    public void getRefreshListData() {
        mRefreshListDataLiveData.setValue(mConfigId);
    }

    /**
     * 反扫对mList的更新
     */
    public LiveData<Resource<List<StockInEntity>>> getRefreshListDataLiveData() {
        return Transformations.switchMap(mRefreshListDataLiveData, model::getRefreshListData);
    }

    /**
     * 触发反扫更新后 page变化
     */
    public void resetPage(List<StockInEntity> list) {
        mCurrentPage = 1;
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
        mUpLoadLiveData.setValue(mConfigId);
    }

    /**
     * 上传数据
     */
    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUpLoadLiveData, model::uploadListByConfigId);
    }

    public void setConfigInfo(ConfigurationEntity entity) {
        mConfigInfo =entity;
    }
}
