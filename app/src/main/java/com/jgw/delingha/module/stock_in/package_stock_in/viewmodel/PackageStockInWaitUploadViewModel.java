package com.jgw.delingha.module.stock_in.package_stock_in.viewmodel;

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
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.model.PackageConfigInfoModel;
import com.jgw.delingha.module.stock_in.package_stock_in.model.PackageStockInModel;
import com.jgw.delingha.sql.entity.PackageConfigEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Cxz
 * data : 2020/3/4
 * description :
 */
public class PackageStockInWaitUploadViewModel extends BaseViewModel {

    private final PackageConfigInfoModel mConfigModel;
    private final PackageStockInModel model;
    private List<PackageConfigEntity> mConfigList;
    private final MutableLiveData<Long> mConfigCodeListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mDeleteItemLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUploadLiveData = new ValueKeeperLiveData<>();

    public PackageStockInWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new PackageStockInModel();
        mConfigModel = new PackageConfigInfoModel();
    }

    public void setDataList(List<PackageConfigEntity> list) {
        mConfigList = list;
    }

    public void loadConfigCodeList() {
        mConfigCodeListLiveData.setValue(null);
    }

    public LiveData<Resource<List<PackageConfigEntity>>> getConfigCodeListLiveData() {
        return Transformations.switchMap(mConfigCodeListLiveData, input -> model.getAllConfigCodeList());
    }

    public void deleteSelectItem() {
        List<Long> tempList = new ArrayList<>();
        for (PackageConfigEntity entity : mConfigList) {
            if (entity.isSelect()) {
                tempList.add(entity.getId());
            }
        }
        mDeleteItemLiveData.setValue(tempList);
    }

    public LiveData<Resource<String>> getDeleteItemLiveData() {
        return Transformations.switchMap(mDeleteItemLiveData, model::removeAllByConfigIds);
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

    public void uploadCodeList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        mUploadLiveData.setValue(getSelectConfigs());
    }

    @NotNull
    private ArrayList<Long> getSelectConfigs() {
        ArrayList<Long> configs = new ArrayList<>();
        for (PackageConfigEntity e : mConfigList) {
            if (e.isSelect()) {
                configs.add(e.getId());
            }
        }
        return configs;
    }

    public LiveData<Resource<UploadResultBean>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, configs -> {
            if (configs.size() == 1) {
                //单配置并发分组上传
                return model.uploadListByConfigId(configs.get(0));
            } else {
                //并发配置后单配置串行上传
                return model.uploadConfigGroupList(configs);
            }
        });
    }

    /**
     * 判断是否选择数据
     */
    public boolean isDataNoSelect() {
        for (PackageConfigEntity entity : mConfigList) {
            if (entity.isSelect()) {
                return false;
            }
        }
        return true;
    }

}
