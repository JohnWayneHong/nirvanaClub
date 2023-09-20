package com.jgw.delingha.module.setting_center.viewModel;

import static com.jgw.common_library.utils.ResourcesUtils.getString;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.BuildConfigUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.BuildConfig;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ToolsTableHeaderBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.common.model.OfflineDataModel;
import com.jgw.delingha.module.setting_center.model.SettingCenterModel;
import com.jgw.delingha.utils.ConstantUtil;

/**
 * author : Cxz
 * data : 2019/12/11
 * description :
 */
public class SettingCenterViewModel extends BaseViewModel {
    private final OfflineDataModel mOfflineDataModel;
    private final ToolsTableHeaderBean toolsTableHeaderBean;
    private final MutableLiveData<Long> updateOfflineLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> clearDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> saveLocalDataToFileLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mLogoutLiveData = new ValueKeeperLiveData<>();
    private final SettingCenterModel mSettingCenterModel;

    public SettingCenterViewModel(@NonNull Application application) {
        super(application);
        mOfflineDataModel = new OfflineDataModel();
        mSettingCenterModel = new SettingCenterModel();
        toolsTableHeaderBean = new ToolsTableHeaderBean();
    }

    public void setToolsTableHeaderData() {
        toolsTableHeaderBean.appName = getString(R.string.supercode);
        toolsTableHeaderBean.mobile = MMKVUtils.getString(ConstantUtil.USER_MOBILE);
        toolsTableHeaderBean.systemName = MMKVUtils.getString(ConstantUtil.SYSTEM_NAME);
        toolsTableHeaderBean.companyName = MMKVUtils.getString(ConstantUtil.ORGANIZATION_NAME);
        toolsTableHeaderBean.companyIcon = MMKVUtils.getString(ConstantUtil.ORGANIZATION_ICON);
        toolsTableHeaderBean.companyNomalIcon = R.drawable.company_nomal_icon;
//        String s = BuildConfig.DEBUG ? "_" + BuildConfig.VERSION_CODE : "";
        String s = "_" + BuildConfigUtils.getVersionCode();//方便排错 展示版本号
        toolsTableHeaderBean.version = "V" + BuildConfigUtils.getVersionName() + s;
        if (BuildConfig.DEBUG) {
            int httpType = MMKVUtils.getInt(ConstantUtil.HTTP_TYPE);
            switch (httpType) {
                case ConstantUtil.TYPE_DEBUG:
                    toolsTableHeaderBean.versionType = "开发";
                    break;
                case ConstantUtil.TYPE_TEST:
                    toolsTableHeaderBean.versionType = "测试";
                    break;
                case ConstantUtil.TYPE_PRERELEASE:
                    toolsTableHeaderBean.versionType = "预发布";
                    break;
            }

        }
    }

    public ToolsTableHeaderBean getToolsTableHeaderData() {
        return toolsTableHeaderBean;
    }

    public void updateOfflineData() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        updateOfflineLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getUpdateOfflineLiveData() {
        return Transformations.switchMap(updateOfflineLiveData, input -> mOfflineDataModel.requestCurrentCustomerInfo());
    }

    public void clearData() {
        clearDataLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getClearDataLiveData() {
        return Transformations.switchMap(clearDataLiveData, input -> mOfflineDataModel.clearData());
    }

    public void saveLocalDataToFile(String code) {
        saveLocalDataToFileLiveData.setValue(code);
    }

    public LiveData<Resource<String>> getSaveLocalDataToFileLiveData() {
        return Transformations.switchMap(saveLocalDataToFileLiveData, mOfflineDataModel::saveLocalDataToFile);
    }

    public void logout() {
        mLogoutLiveData.setValue(null);
    }
    public LiveData<Resource<String>> getLogoutLiveData(){
        return Transformations.switchMap(mLogoutLiveData, input -> mSettingCenterModel.logout());
    }
}
