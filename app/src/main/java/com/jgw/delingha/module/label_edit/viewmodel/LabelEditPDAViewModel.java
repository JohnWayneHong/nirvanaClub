package com.jgw.delingha.module.label_edit.viewmodel;

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
import com.jgw.delingha.module.label_edit.model.LabelEditModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.LabelEditEntity;
import com.jgw.delingha.sql.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class LabelEditPDAViewModel extends BaseViewModel {

    private final LabelEditModel model;
    private final ConfigInfoModel mConfigModel;
    private List<LabelEditEntity> mList;
    private long mConfigId = -1;
    private int mPage = 1;

    private final MutableLiveData<Long> mRequestHeaderDataLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<String> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<CodeBean> mUpdateCodeInfoLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<Long> mRefreshListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mUploadLiveData = new ValueKeeperLiveData<>();
    private ConfigurationEntity mConfigInfo;

    public LabelEditPDAViewModel(@NonNull Application application) {
        super(application);
        model = new LabelEditModel();
        mConfigModel = new ConfigInfoModel();
    }

    /**
     * 获取mAdapter中的数据源引用 用来对同一个数据源进行操作
     *
     * @param list mAdapter中的数据源
     */
    public void setDataList(List<LabelEditEntity> list) {
        mList = list;
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

    /**
     * 扫入一个码时执行的本地校验方法
     * 判断码是否已经存在数据库
     * 绘制列表和更新统计结果
     * 当前界面默认最多保留20条数据 超过20条数据后从list中移出 防止大量数据时占用过多内存
     *
     * @param code         码
     * @param adapter      插入码时对界面进行更新绘制
     * @param recyclerView 界面绘制时要保证动画正常显示
     */
    public void handleScanQRCode(String code, CodeEntityRecyclerAdapter<LabelEditEntity> adapter, CustomBaseRecyclerView recyclerView) {
        LabelEditEntity entity = new LabelEditEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.getConfigEntity().setTarget(mConfigInfo);
        if (!model.putData(entity)){
            ToastUtils.showToast(ResourcesUtils.getString(R.string.scan_code_failed));
            return;
        }
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<LabelEditEntity> tempList = new ArrayList<>();
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
        LabelEditEntity data = model.queryEntityByCode(code);
        if (data != null) {
            String msg;
            ConfigurationEntity configInfo = data.getConfigEntity().getTarget();
            UserEntity userInfo = configInfo.getUserEntity().getTarget();
            if (LocalUserUtils.getCurrentUserId() != userInfo.getId()) {
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
        return Transformations.switchMap(mCheckCodeLiveData, model::checkCode);
    }

    /**
     * 获取到码信息时把数据插入数据库中
     *
     * @param bean 联网获得的码信息
     */
    public void updateCodeStatus(CodeBean bean) {
        mUpdateCodeInfoLiveData.setValue(bean);
    }

    public LiveData<Resource<LabelEditEntity>> getUpdateCodeInfoLiveData() {
        return Transformations.switchMap(mUpdateCodeInfoLiveData, model::updateCodeStatus);
    }

    /**
     * 通过网络校验码非法后从数据库移除该条数据
     *
     * @param code 码
     */
    public void deleteCode(String code) {
        model.removeCode(code);
    }

    /**
     * 从反扫或确认出库界面返回和码校验为错误后删除码后
     * 省略判断直接刷新当前列表 为configId下最近20条扫码数据
     */
    public void refreshListByConfigId() {
        mRefreshListLiveData.setValue(mConfigId);
        mPage = 1;
    }

    public LiveData<Resource<List<LabelEditEntity>>> getRefreshListLiveData() {
        return Transformations.switchMap(mRefreshListLiveData, model::refreshListByConfigId);
    }

    /**
     * 当前页数据完整时尝试请求下一页数据
     */
    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mLoadMoreLiveData.setValue(null);
        }
    }

    public LiveData<Resource<List<LabelEditEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> {
            int pageSize = CustomRecyclerAdapter.ITEM_PAGE_SIZE;
            return model.loadMoreList(mConfigId, pageSize, mPage);
        });
    }

    public long getConfigId() {
        return mConfigId;
    }

    /**
     * 查询统计该configId下扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(mConfigId);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, model::getCalculationTotal);
    }

    public void getTaskId() {
        if (mList.isEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
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
        return Transformations.switchMap(mUploadLiveData, model::uploadListByConfigId);
    }

    public void setConfigInfo(ConfigurationEntity entity) {
        mConfigInfo =entity;
    }
}
