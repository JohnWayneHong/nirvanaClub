package com.jgw.delingha.module.exchange_warehouse.viewmodel;

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
import com.jgw.delingha.bean.ExchangeWarehouseConfigurationPendingBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.module.exchange_warehouse.model.ExchangeWarehouseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Cxz
 * data : 2020/3/4
 * description :
 */
public class ExchangeWarehouseWaitUploadViewModel extends BaseViewModel {

    private ExchangeWarehouseModel model;
    private List<ExchangeWarehouseConfigurationPendingBean> mList = new ArrayList<>();
    private final MutableLiveData<List<Long>> mGetConfigListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUpLoadLiveData = new ValueKeeperLiveData<>();


    public ExchangeWarehouseWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new ExchangeWarehouseModel();
    }

    public void setDataList(List<ExchangeWarehouseConfigurationPendingBean> list) {
        mList = list;
    }

    public void getLoadListData() {
        mGetConfigListLiveData.setValue(null);
    }

    /**
     * 获取显示的list
     */
    public LiveData<Resource<List<ExchangeWarehouseConfigurationPendingBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mGetConfigListLiveData, input -> model.getBeanList());
    }

    /**
     * 判断是否选择数据
     */
    public boolean isDataNoSelect() {
        for (ExchangeWarehouseConfigurationPendingBean bean : mList) {
            if (bean.isSelect) {
                return false;
            }
        }
        return true;
    }

    public void deleteDataBySelect() {
        model.deleteDataBySelect(mList);
    }

    private List<Long> getSelectedConfigListIds() {
        List<Long> selectListIds = new ArrayList<>();
        for (ExchangeWarehouseConfigurationPendingBean bean : mList) {
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
        mRequestTaskIdLiveData.setValue(getSelectedConfigListIds());
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, model::getTaskIdList);
    }

    /**
     * 触发上传
     */
    public void uploadData() {
        mUpLoadLiveData.setValue(getSelectedConfigListIds());
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
