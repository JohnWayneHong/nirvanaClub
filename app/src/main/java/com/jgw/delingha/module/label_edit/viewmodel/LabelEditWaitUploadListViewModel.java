package com.jgw.delingha.module.label_edit.viewmodel;

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
import com.jgw.delingha.bean.LabelEditWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.label_edit.adapter.LabelEditWaitUploadListRecyclerAdapter;
import com.jgw.delingha.module.label_edit.model.LabelEditModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LabelEditWaitUploadListViewModel extends BaseViewModel {

    private final LabelEditModel model;
    private final ConfigInfoModel mConfigModel;

    private final MutableLiveData<Long> mConfigGroupListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUploadConfigLiveData = new ValueKeeperLiveData<>();
    private List<LabelEditWaitUploadListBean> mList;

    public LabelEditWaitUploadListViewModel(@NonNull Application application) {
        super(application);
        model = new LabelEditModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<LabelEditWaitUploadListBean> dataList) {
        mList = dataList;
    }

    public void getConfigGroupList() {
        mConfigGroupListLiveData.setValue(null);
    }

    public LiveData<Resource<List<LabelEditWaitUploadListBean>>> getConfigGroupListLiveData() {
        return Transformations.switchMap(mConfigGroupListLiveData, input -> model.getConfigGroupList());
    }

    public boolean isSelectEmpty() {
        for (LabelEditWaitUploadListBean b : mList) {
            if (b.selected) {
                return false;
            }
        }
        return true;
    }

    public void getTaskIdList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mRequestTaskIdLiveData.setValue(getSelectConfigs());
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getTaskIdList);
    }

    public void uploadConfigList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadConfigLiveData.setValue(getSelectConfigs());
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadConfigLiveData, configs -> {
            if (configs.size() == 1) {
                //单配置并发分组上传
                return model.uploadListByConfigId(configs.get(0));
            } else {
                //并发配置后单配置串行上传
                return model.uploadConfigGroupList(configs);
            }
        });
    }


    @NotNull
    private ArrayList<Long> getSelectConfigs() {
        ArrayList<Long> configs = new ArrayList<>();
        for (LabelEditWaitUploadListBean b : mList) {
            if (b.selected) {
                configs.add(b.config_id);
            }
        }
        return configs;
    }

    /**
     * 删除选中分组的码
     * @param mAdapter
     */
    public void deleteGroup(LabelEditWaitUploadListRecyclerAdapter mAdapter) {
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<LabelEditWaitUploadListBean> tempList = new ArrayList<>();
        for (LabelEditWaitUploadListBean b : mList) {
            if (b.selected) {
                ids.add(b.config_id);
                tempList.add(b);
            }
        }
        if (ids.size() < mList.size()) {
            model.removeAllByConfigIds(ids);
        } else {
            model.removeAll();
        }
        for (LabelEditWaitUploadListBean bean:tempList){
            mAdapter.notifyRemoveItem(bean);
        }
    }

}
