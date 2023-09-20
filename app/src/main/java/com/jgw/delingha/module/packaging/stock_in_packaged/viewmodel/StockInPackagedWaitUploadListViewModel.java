package com.jgw.delingha.module.packaging.stock_in_packaged.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.ConfigurationSelectBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.packaging.stock_in_packaged.model.StockInPackagedModel;

import java.util.ArrayList;
import java.util.List;


public class StockInPackagedWaitUploadListViewModel extends BaseViewModel {

    private final ConfigInfoModel mConfigModel;
    private List<ConfigurationSelectBean> mList = new ArrayList<>();
    private StockInPackagedModel mModel;
    private final MutableLiveData<List<Long>> mGetConfigListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUploadConfigLiveData = new ValueKeeperLiveData<>();

    public StockInPackagedWaitUploadListViewModel(@NonNull Application application) {
        super(application);
        mModel = new StockInPackagedModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<ConfigurationSelectBean> list) {
        mList = list;
    }

    public void getConfigList() {
        mGetConfigListLiveData.setValue(null);
    }

    /**
     * 获取显示的list
     */
    public LiveData<Resource<List<ConfigurationSelectBean>>> getSelectBeanList() {
        return Transformations.switchMap(mGetConfigListLiveData, input -> mModel.getSelectBeanList());
    }

    /**
     * 判断是否被选中 无true 有false
     */
    public boolean getNoSelectedConfigList() {
        for (ConfigurationSelectBean selectBean : mList) {
            if (selectBean.selected) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取被选中的list的configId
     */
    private List<Long> getConfigIds() {
        List<Long> configs = new ArrayList<>();
        for (ConfigurationSelectBean e : mList) {
            if (e.selected) {
                configs.add(e.configId);
            }
        }
        return configs;
    }

    /**
     * 删除数据库选中的list，更新list
     */
    public void deleteConfigs(CustomRecyclerAdapter<ConfigurationSelectBean> adapter) {
        List<Long> configIds = new ArrayList<>();
        ArrayList<ConfigurationSelectBean> tempList = new ArrayList<>();
        for (ConfigurationSelectBean e : mList) {
            if (e.selected) {
                tempList.add(e);
                configIds.add(e.configId);
            }
        }
        if (configIds.size() < mList.size()) {
            for (Long i : configIds) {
                mModel.deleteAllByConfigId(i);
            }
            for (ConfigurationSelectBean e : tempList) {
                adapter.notifyRemoveItem(e);
            }
        } else {
            mModel.deleteAll();
            adapter.notifyRemoveListItem();
        }
    }

    public void getTaskIdList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(getConfigIds());
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getTaskIdList);
    }

    /**
     * 触发上传configId
     */
    public void uploadData() {
        mUploadConfigLiveData.setValue(getConfigIds());
    }

    /**
     * 上传
     */
    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadConfigLiveData, input -> {
            if (input.size() == 1) {
                return mModel.uploadListByConfigId(input.get(0));
            } else {
                return mModel.uploadConfigGroupList(input);
            }
        });
    }

}

