package com.jgw.delingha.module.login.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.bean.OrgAndSysBean;
import com.jgw.delingha.bean.OrgSystemBean;
import com.jgw.delingha.bean.OrganizationBean;
import com.jgw.delingha.bean.SystemBean;
import com.jgw.delingha.common.model.OfflineDataModel;
import com.jgw.delingha.module.login.model.SelectOrgSystemModel;
import com.jgw.delingha.sql.entity.UserEntity;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.List;

public class SelectOrgSystemViewModel extends BaseViewModel {

    private final OfflineDataModel mOfflineDataModel;

    private SelectOrgSystemModel mModel;

    private String mOrganizationId = "";
    private String mOrganizationFullName = "";
    private String mSystemId = "";
    private String mSystemName = "";
    private String tempToken = "";

    private final MutableLiveData<String> getOrganizationListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> getSystemListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<OrgSystemBean> getOrgSystemLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> requestCurrentCustomerInfoLiveData = new ValueKeeperLiveData<>();

    public String getOrganizationId() {
        return mOrganizationId;
    }

    public String getOrganizationFullName() {
        return mOrganizationFullName;
    }

    public String getSystemId() {
        return mSystemId;
    }

    public String getSystemName() {
        return mSystemName;
    }

    public void setOrganizationId(String mOrganizationId) {
        this.mOrganizationId = mOrganizationId;
    }

    public void setOrganizationFullName(String mOrganizationFullName) {
        this.mOrganizationFullName = mOrganizationFullName;
    }

    public void setSystemId(String mSystemId) {
        this.mSystemId = mSystemId;
    }

    public void setSystemName(String mSystemName) {
        this.mSystemName = mSystemName;
    }

    public SelectOrgSystemViewModel(@NonNull Application application) {
        super(application);
        mModel = new SelectOrgSystemModel();
        mOfflineDataModel = new OfflineDataModel();
    }

    public void getOrganizationList() {
        getOrganizationListLiveData.setValue(null);
    }

    public LiveData<Resource<List<OrganizationBean.ListBean>>> getOrganizationListLiveData() {
        return Transformations.switchMap(getOrganizationListLiveData, input -> mModel.getOrganizationList(tempToken));
    }

    public void getSystemList() {
        getSystemListLiveData.setValue(mOrganizationId);
    }

    public LiveData<Resource<List<SystemBean.ListBean>>> getSystemListLiveData() {
        return Transformations.switchMap(getSystemListLiveData, input -> mModel.getSystemList(input, tempToken));
    }

    public void submitOrgSystem() {
        OrgSystemBean orgSysBean = new OrgSystemBean(mOrganizationId, mSystemId);
        getOrgSystemLiveData.setValue(orgSysBean);
    }

    public LiveData<Resource<OrgAndSysBean>> getSubmitOrgSystemLiveData() {
        return Transformations.switchMap(getOrgSystemLiveData, input -> mModel.submitOrgSystem(input, tempToken));
    }

    public void saveUserEntity() {
        UserEntity userEntity = new UserEntity(0, MMKVUtils.getString(ConstantUtil.USER_MOBILE), mOrganizationId);
        long value = mModel.putData(userEntity);
        MMKVUtils.save(ConstantUtil.USER_ENTITY_ID, value);
    }

    public void requestCurrentCustomerInfo() {
        requestCurrentCustomerInfoLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getRequestCurrentCustomerInfoLiveData() {
        return Transformations.switchMap(requestCurrentCustomerInfoLiveData, input -> mOfflineDataModel.requestCurrentCustomerInfo());
    }

    public void setTempToken(String token) {
        tempToken = token;
    }
}
