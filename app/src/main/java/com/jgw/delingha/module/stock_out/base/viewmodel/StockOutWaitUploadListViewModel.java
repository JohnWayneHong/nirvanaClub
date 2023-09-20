package com.jgw.delingha.module.stock_out.base.viewmodel;

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
import com.jgw.delingha.bean.StockOutWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_out.base.model.StockOutPDAModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StockOutWaitUploadListViewModel extends BaseViewModel {

    private final StockOutPDAModel model;
    private final ConfigInfoModel mConfigModel;

    private final MutableLiveData<Long> mConfigGroupListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUploadConfigLiveData = new ValueKeeperLiveData<>();
    private List<StockOutWaitUploadListBean> mList;

    public StockOutWaitUploadListViewModel(@NonNull Application application) {
        super(application);
        model = new StockOutPDAModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<StockOutWaitUploadListBean> dataList) {
        mList = dataList;
    }

    public void getConfigGroupList() {
        mConfigGroupListLiveData.setValue(null);
    }

    public LiveData<Resource<List<StockOutWaitUploadListBean>>> getConfigGroupListLiveData() {
        return Transformations.switchMap(mConfigGroupListLiveData, input -> model.getConfigGroupList());
    }

    public boolean isSelectEmpty() {
        for (StockOutWaitUploadListBean b : mList) {
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
    private List<Long> getSelectConfigs() {
        List<Long> configs = new ArrayList<>();
        for (StockOutWaitUploadListBean e : mList) {
            if (e.selected) {
                configs.add(e.config_id);
            }
        }
        return configs;
    }

    /**
     * 删除选中分组的码
     */
    public void deleteGroup(CustomRecyclerAdapter<StockOutWaitUploadListBean> adapter) {
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<StockOutWaitUploadListBean> tempList = new ArrayList<>();
        for (StockOutWaitUploadListBean b : mList) {
            if (b.selected) {
                ids.add(b.config_id);
                tempList.add(b);
            }
        }
        if (ids.size() < mList.size()) {
            model.deleteAllByConfigId(ids);
            for (StockOutWaitUploadListBean b : tempList) {
                adapter.notifyRemoveItem(b);
            }
        } else {
            model.deleteAll();
            adapter.notifyRemoveListItem();
        }
    }

}
