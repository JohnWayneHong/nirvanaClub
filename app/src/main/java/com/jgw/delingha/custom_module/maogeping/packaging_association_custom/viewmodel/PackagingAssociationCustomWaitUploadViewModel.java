package com.jgw.delingha.custom_module.maogeping.packaging_association_custom.viewmodel;

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
import com.jgw.delingha.custom_module.maogeping.packaging_association_custom.model.PackagingAssociationCustomModel;
import com.jgw.delingha.sql.entity.PackageConfigEntity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Cxz
 * data : 2020/3/4
 * description :
 */
public class PackagingAssociationCustomWaitUploadViewModel extends BaseViewModel {

    private final PackagingAssociationCustomModel model;
    private final PackageConfigInfoModel mConfigModel;
    private List<PackageConfigEntity> mConfigList;
    private final MutableLiveData<Long> mConfigCodeListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mDeleteItemLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<Long>> mRequestTaskIdLiveData = new ValueKeeperLiveData<>();

    public PackagingAssociationCustomWaitUploadViewModel(@NonNull Application application) {
        super(application);
        model = new PackagingAssociationCustomModel();
        mConfigModel = new PackageConfigInfoModel();
    }

    public void setDataList(List<PackageConfigEntity> dataList) {
        mConfigList = dataList;
    }

    public void loadConfigCodeList() {
        mConfigCodeListLiveData.setValue(null);
    }

    public LiveData<Resource<List<PackageConfigEntity>>> getConfigCodeListLiveData() {
        return Transformations.switchMap(mConfigCodeListLiveData, input -> model.getAllConfigCodeList());
    }

    /**
     * 删除选中的数据
     */
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

    /**
     * 判断是否没有选择任何数据
     */
    public boolean isDataNoSelect() {
        for (PackageConfigEntity entity : mConfigList) {
            if (entity.isSelect()) {
                return false;
            }
        }
        return true;
    }

    public void tryUploadCodeList() {
        getTaskIdList();
    }

    public void getTaskIdList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        ArrayList<Long> configs = getSelectConfigs();
        mRequestTaskIdLiveData.setValue(configs);
    }

    public LiveData<Resource<String>> getRequestTaskIdLiveData() {
        return Transformations.switchMap(mRequestTaskIdLiveData, mConfigModel::getPackageTaskIdList);
    }

    public void uploadCodeList() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        ArrayList<Long> configs = getSelectConfigs();
        mUploadLiveData.setValue(configs);
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
}
