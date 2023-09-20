package com.jgw.delingha.custom_module.wanwei.stock_out.viewmodel;

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
import com.jgw.delingha.custom_module.wanwei.stock_out.adapter.WanWeiStockOutPDAListAdapter;
import com.jgw.delingha.custom_module.wanwei.stock_out.model.WanWeiStockOutPDAModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.WanWeiStockOutEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WanWeiStockOutPDAViewModel extends BaseViewModel {

    private final WanWeiStockOutPDAModel model;
    private final ConfigInfoModel mConfigModel;
    private List<WanWeiStockOutEntity> mList = new ArrayList<>();

    private long mConfigId;
    private int mPage = 1;

    private final MutableLiveData<Long> mRequestHeaderDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRefreshListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private ConfigurationEntity mConfigInfo;
    private long mSingleNumber;

    public WanWeiStockOutPDAViewModel(@NonNull Application application) {
        super(application);
        model = new WanWeiStockOutPDAModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<WanWeiStockOutEntity> dataList) {
        mList = dataList;
    }

    /**
     * 通过configId获取头部数据
     *
     * @param configId 设置界面保存的信息
     */
    public void initHeaderData(long configId) {
        mConfigId = configId;
        mRequestHeaderDataLiveData.setValue(configId);
    }

    public LiveData<Resource<ConfigurationEntity>> getRequestHeaderDataLiveData() {
        return Transformations.switchMap(mRequestHeaderDataLiveData, mConfigModel::getConfigInfo);
    }

    public void requestCodeInfo(String code, WanWeiStockOutPDAListAdapter adapter, CustomBaseRecyclerView recyclerView) {
        WanWeiStockOutEntity data = model.queryEntityByCode(code);
        if (data != null) {
            String msg;
            if (data.getConfigEntity().getTarget().getUserEntity().getTarget().getId() != LocalUserUtils.getCurrentUserId()) {
                msg = code + "该码被其他用户录入,请切换账号或清除离线数据";
            } else {
                msg = code + "该码已在库存中!";
            }
            ToastUtils.showToast(msg);
            return;
        }
        WanWeiStockOutEntity entity = new WanWeiStockOutEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.getConfigEntity().setTarget(mConfigInfo);
        if (!model.putData(entity)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);

        List<WanWeiStockOutEntity> tempList = new ArrayList<>();
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

    public LiveData<Resource<CodeBean>> getCheckCodeLiveData() {
        return Transformations.switchMap(mCheckCodeLiveData, code -> model.getCheckCodeInfo(code, mConfigInfo));
    }

    public void updateCodeStatus(CodeBean bean, int status) {
        model.updateCodeStatus(bean, status);
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
     * 省略判断直接刷新当前列表 为firstId后最近20条扫码数据
     */
    public void refreshListByConfigId() {
        mRefreshListLiveData.setValue(mConfigId);
        mPage = 1;
    }

    public LiveData<Resource<List<WanWeiStockOutEntity>>> getRefreshListLiveData() {
        return Transformations.switchMap(mRefreshListLiveData, model::refreshListByConfigId);
    }


    public List<WanWeiStockOutEntity> getList() {
        return mList;
    }

    public long getConfigId() {
        return mConfigId;
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
        long l = model.queryVerifyingDataConfigId(mConfigId);
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
     * 查询统计firstId之后本次扫码数量
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

    public LiveData<Resource<List<WanWeiStockOutEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> {
            int pageSize = CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            return model.loadMoreList(mConfigId, mPage, pageSize);
        });
    }


    public void setConfigInfo(ConfigurationEntity data) {
        mConfigInfo = data;
    }

    public void setScanSingleNumber(long singleNumber) {
        mSingleNumber = singleNumber;
    }

    public long getSingleNumber() {
        return mSingleNumber;
    }
}
