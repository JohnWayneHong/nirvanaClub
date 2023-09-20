package com.jgw.delingha.module.stock_out.stock_out_fast.viewmodel;

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
import com.jgw.delingha.module.stock_out.stock_out_fast.model.StockOutFastPDAModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockOutFastEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockOutFastPDAViewModel extends BaseViewModel {

    private final StockOutFastPDAModel model;
    private final ConfigInfoModel mConfigModel;
    private List<StockOutFastEntity> mList = new ArrayList<>();

    private long mConfigId;
    private int mPage = 1;

    private final MutableLiveData<Long> mRequestHeaderDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private long mSingleNumber;

    public StockOutFastPDAViewModel(@NonNull Application application) {
        super(application);
        model = new StockOutFastPDAModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<StockOutFastEntity> dataList) {
        mList = dataList;
    }

    /**
     * 通过configId获取头部数据
     *
     * @param configId 设置界面保存的信息
     */
    public void getConfigData(long configId) {
        mConfigId = configId;
        mRequestHeaderDataLiveData.setValue(configId);
    }

    public long getConfigId() {
        return mConfigId;
    }

    public LiveData<Resource<ConfigurationEntity>> getRequestHeaderDataLiveData() {
        return Transformations.switchMap(mRequestHeaderDataLiveData, mConfigModel::getConfigInfo);
    }

    public void requestCodeInfo(String code, CodeEntityRecyclerAdapter<StockOutFastEntity> adapter, CustomBaseRecyclerView recyclerView) {
        StockOutFastEntity entity = new StockOutFastEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.getConfigEntity().setTargetId(mConfigId);
        if (!model.putData(entity)){
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<StockOutFastEntity> tempList = new ArrayList<>();
        if (mList.size() > CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            for (int i = CustomRecyclerAdapter.ITEM_PAGE_SIZE; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CustomRecyclerAdapter.ITEM_PAGE_SIZE, tempList.size());
            }
        }
        calculationTotal();

        mCheckCodeLiveData.setValue(code);
        mPage = 1;
    }

    public boolean checkCodeExisted(String code){
        StockOutFastEntity data = model.queryEntityByCode(code);
        if (data != null) {
            String msg;
            if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                msg = code + "该码被其他用户录入,请切换账号或清除离线数据";
            } else {
                msg = code + "该码已在库存中!";
            }
            ToastUtils.showToast(msg);
            return true;
        }
        return false;
    }

    public LiveData<Resource<CodeBean>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, model::getCheckCodeInfo);
    }

    /**
     * 通过网络校验码非法后从数据库移除该条数据
     *
     * @param code 码
     */
    public void deleteCode(String code) {
        model.removeEntityByCode(code);
    }

    /**
     * 从反扫或确认出库界面返回和码校验为错误后删除码后
     * 省略判断直接刷新当前列表 为该configId下最近20条扫码数据
     */
    public void refreshListByConfigId() {
        mRefreshListLiveData.setValue(mConfigId);
        mPage = 1;
    }

    public LiveData<Resource<List<StockOutFastEntity>>> getRefreshListLiveData() {
        return Transformations.switchMap(mRefreshListLiveData, model::refreshListByConfigId);
    }


    public List<StockOutFastEntity> getList() {
        return mList;
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


    public void uploadCodes() {
        if (mList.isEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        long l = model.queryVerifyingDataByConfigId(mConfigId);
        if (l != 0) {
            ToastUtils.showToast("还有码在校验中,请稍后再试");
            return;
        }
        mUploadLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::uploadListByConfigId);
    }

    /**
     * 查询统计该configId下本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Map<String, Long>>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, input -> model.getCalculationTotal(mConfigId));
    }

    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadMoreLiveData.setValue(null);
        }
    }

    public LiveData<Resource<List<StockOutFastEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> {
            int pageSize = CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            return model.loadMoreList(mConfigId, pageSize, mPage);
        });
    }


    public void setScanSingleNumber(long singleNumber) {
        mSingleNumber = singleNumber;
    }

    public long getSingleNumber() {
        return mSingleNumber;
    }
}
