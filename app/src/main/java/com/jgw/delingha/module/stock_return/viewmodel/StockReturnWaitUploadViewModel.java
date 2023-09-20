package com.jgw.delingha.module.stock_return.viewmodel;

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
import com.jgw.delingha.bean.StockReturnPendingConfigurationBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_return.model.StockReturnV3CodeModel;

import java.util.ArrayList;
import java.util.List;


/**
 * author : Cxz
 * data : 2020/3/3
 * description :
 */
public class StockReturnWaitUploadViewModel extends BaseViewModel {

    private final ConfigInfoModel mConfigModel;
    private final MutableLiveData<List<Long>> mUpLoadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mLoadListLiveData = new ValueKeeperLiveData<>();

    private final StockReturnV3CodeModel model;
    private List<StockReturnPendingConfigurationBean> mList;

    public StockReturnWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new StockReturnV3CodeModel();
        mConfigModel = new ConfigInfoModel();
    }

    /**
     * 绑定mList
     */
    public void setDataList(List<StockReturnPendingConfigurationBean> list) {
        mList = list;
    }

    /**
     * 触发获取更新的list数据
     */
    public void getLoadListData() {
        mLoadListLiveData.setValue(null);
    }

    /**
     * 获取更新的list数据
     */
    public LiveData<Resource<List<StockReturnPendingConfigurationBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mLoadListLiveData, input -> model.getLoadList());
    }

    /**
     * 判断是否选择数据
     */
    public boolean isDataNoSelect() {
        for (StockReturnPendingConfigurationBean bean : mList) {
            if (bean.isSelect) {
                return false;
            }
        }
        return true;
    }

    /**
     * 删除所有选择数据
     */
    public void deleteDataBySelect() {
        model.deleteDataBySelect(mList);
    }

    /**
     * @return 被选中的待上传id集合
     */
    private List<Long> getSelectedConfigs() {
        List<Long> selectListIds = new ArrayList<>();
        for (StockReturnPendingConfigurationBean bean : mList) {
            if (bean.isSelect) {
                selectListIds.add(bean.id);
            }
        }
        return selectListIds;
    }

    public void getTaskIdList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(getSelectedConfigs());
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getTaskIdList);
    }

    /**
     * 触发上传
     */
    public void uploadData() {
        mUpLoadLiveData.setValue(getSelectedConfigs());
    }

    /**
     * 上传
     */
    public LiveData<Resource<UploadResultBean>> getUpLoadLiveData() {
        return Transformations.switchMap(mUpLoadLiveData, input -> {
            if (input.size() == 1) {
                return model.uploadListByConfigId(input.get(0));
            } else {
                return model.uploadConfigGroupList(input);
            }
        });
    }

}
