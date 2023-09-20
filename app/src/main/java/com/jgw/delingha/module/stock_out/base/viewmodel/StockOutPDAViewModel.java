package com.jgw.delingha.module.stock_out.base.viewmodel;

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
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_out.base.model.StockOutPDAModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockOutEntity;

import java.util.ArrayList;
import java.util.List;

public class StockOutPDAViewModel extends BaseViewModel {

    private final StockOutPDAModel model;
    private final ConfigInfoModel mConfigModel;
    private List<StockOutEntity> mList;
    private long mConfigId = -1;
    private int mPage = 1;

    private final MutableLiveData<Long> mRequestHeaderDataLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<String> mCheckCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<CodeBean> mUpdateCodeInfoLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mUpdateErrorCodeStatusLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<Long> mRefreshListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mLoadMoreLiveData = new ValueKeeperLiveData<>();

    public StockOutPDAViewModel(@NonNull Application application) {
        super(application);
        model = new StockOutPDAModel();
        mConfigModel = new ConfigInfoModel();
    }

    /**
     * 获取mAdapter中的数据源引用 用来对同一个数据源进行操作
     *
     * @param list mAdapter中的数据源
     */
    public void setDataList(List<StockOutEntity> list) {
        mList = list;
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

    public LiveData<Resource<ConfigurationEntity>> getRequestHeaderDataLiveData() {
        return Transformations.switchMap(mRequestHeaderDataLiveData, mConfigModel::getConfigInfo);
    }

    /**
     * 扫入一个码时执行的本地校验方法
     * 判断码是否已经存在数据库
     * 执行本地校验之后插入数据库并记录首条数据为firstId用来标记本次扫码数据
     * 绘制列表和更新统计结果
     * 当前界面默认最多保留20条数据 超过20条数据后从list中移出 防止大量数据时占用过多内存
     *
     * @param code         码
     * @param adapter      插入码时对界面进行更新绘制
     * @param recyclerView 界面绘制时要保证动画正常显示
     */
    public void requestCodeInfo(String code, CodeEntityRecyclerAdapter<StockOutEntity> adapter, CustomBaseRecyclerView recyclerView) {
        StockOutEntity entity = new StockOutEntity();
        entity.setCode(code);
        entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
        entity.getConfigEntity().setTargetId(mConfigId);
        model.putData(entity);
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<StockOutEntity> tempList = new ArrayList<>();
        if (mList.size() > CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            for (int i = CustomRecyclerAdapter.ITEM_PAGE_SIZE; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CustomRecyclerAdapter.ITEM_PAGE_SIZE, tempList.size());
            }
        }
        mCheckCodeLiveData.setValue(code);
        mPage = 1;
        calculationTotal();
    }

    public boolean checkCodeExisted(String code){
        StockOutEntity data = model.queryEntityByCode(code);
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
     * 获取到码信息时把数据插入数据库中
     *
     * @param bean 联网获得的码信息
     */
    public void updateCodeStatus(CodeBean bean) {
        mUpdateCodeInfoLiveData.setValue(bean);
    }

    public LiveData<Resource<StockOutEntity>> getUpdateCodeInfoLiveData() {
        return Transformations.switchMap(mUpdateCodeInfoLiveData, bean -> model.updateCodeInfo(bean, mConfigId));
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
     * 码校验时出现网络错误保留码信息不删除
     * 但是本地更新为错误状态
     *
     * @param code 码
     */
    public void updateErrorCode(String code) {
        mUpdateErrorCodeStatusLiveData.setValue(code);
    }

    public LiveData<Resource<StockOutEntity>> getUpdateErrorCodeStatusLiveData() {
        return Transformations.switchMap(mUpdateErrorCodeStatusLiveData, model::updateErrorCodeStatus);
    }

    /**
     * 从反扫或确认出库界面返回和码校验为错误后删除码后
     * 省略判断直接刷新当前列表 为firstId后最近20条扫码数据
     */
    public void refreshListByConfigId() {
        mRefreshListLiveData.setValue(mConfigId);
        mPage = 1;
    }

    public LiveData<Resource<List<StockOutEntity>>> getRefreshListLiveData() {
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

    public LiveData<Resource<List<StockOutEntity>>> getLoadMoreLiveData() {
        return Transformations.switchMap(mLoadMoreLiveData, input -> model.loadMoreList(mConfigId, mPage, CustomRecyclerAdapter.ITEM_PAGE_SIZE));
    }

    /**
     * 判断firstId之后是否有未校验成功的码
     *
     * @return 是否存在未校验成功的码
     */
    public boolean checkErrorCode() {
        long l = model.queryErrorDataConfigId(mConfigId);
        return l != 0;
    }

    /**
     * 判断是否全部不成功
     *
     * @return 如果全部不成功则无法进入下个界面
     */
    public boolean checkSuccessEmpty() {
        boolean b = true;
        List<StockOutEntity> list = model.querySuccessDataByKey(mConfigId);
        if (list != null && !list.isEmpty()) {
            b = false;
        }
        return b;
    }

    public long getConfigId() {
        return mConfigId;
    }

    /**
     * 查询统计firstId之后本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, input -> model.getCalculationTotal(mConfigId));
    }

}
