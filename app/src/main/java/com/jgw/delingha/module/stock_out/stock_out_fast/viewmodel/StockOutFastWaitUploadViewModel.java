package com.jgw.delingha.module.stock_out.stock_out_fast.viewmodel;

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
import com.jgw.delingha.bean.StockOutFastWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.stock_out.stock_out_fast.model.StockOutFastPDAModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StockOutFastWaitUploadViewModel extends BaseViewModel {

    private final StockOutFastPDAModel model;
    private final ConfigInfoModel mConfigModel;
    private List<StockOutFastWaitUploadListBean> mList = new ArrayList<>();

    private final MutableLiveData<Long> mConfigGroupListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUploadConfigLiveData = new ValueKeeperLiveData<>();

    public StockOutFastWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new StockOutFastPDAModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<StockOutFastWaitUploadListBean> dataList) {
        mList = dataList;
    }

    public void getConfigGroupList() {
        mConfigGroupListLiveData.setValue(null);
    }

    public LiveData<Resource<List<StockOutFastWaitUploadListBean>>> getConfigGroupListLiveData() {
        return Transformations.switchMap(mConfigGroupListLiveData, input -> model.getConfigGroupList());
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
        for (StockOutFastWaitUploadListBean b : mList) {
            if (b.selected) {
                configs.add(b.config_id);
            }
        }
        return configs;
    }

    public boolean isSelectEmpty() {
        for (int i = 0; i < mList.size(); i++) {
            StockOutFastWaitUploadListBean bean = mList.get(i);
            if (bean.selected) {
                return false;
            }
        }
        return true;
    }

    public void deleteSelectItem(CustomRecyclerAdapter<StockOutFastWaitUploadListBean> adapter) {
        ArrayList<Long> ids = new ArrayList<>();
        ArrayList<StockOutFastWaitUploadListBean> tempList = new ArrayList<>();
        for (StockOutFastWaitUploadListBean b : mList) {
            if (b.selected) {
                ids.add(b.config_id);
                tempList.add(b);
            }
        }
        if (ids.size() < mList.size()) {
            model.deleteAllByConfigId(ids);
            for (StockOutFastWaitUploadListBean b : tempList) {
                adapter.notifyRemoveItem(b);
            }
        } else {
            model.deleteAll();
            adapter.notifyRemoveListItem();
        }
    }
}
