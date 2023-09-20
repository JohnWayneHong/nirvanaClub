package com.jgw.delingha.module.login.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.bean.OrgAndSysBean;
import com.jgw.delingha.bean.OrgSystemBean;
import com.jgw.delingha.bean.OrganizationBean;
import com.jgw.delingha.bean.SystemBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.sql.entity.UserEntity;
import com.jgw.delingha.sql.operator.UserOperator;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class SelectOrgSystemModel {
    private final UserOperator userOperator = new UserOperator();

    public LiveData<Resource<List<OrganizationBean.ListBean>>> getOrganizationList(String tempToken) {
        MutableLiveData<Resource<List<OrganizationBean.ListBean>>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .getOrganizationList(tempToken)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrganizationBean>() {
                    @Override
                    public void onNext(OrganizationBean organizationBean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, organizationBean.list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<SystemBean.ListBean>>> getSystemList(String organizationId, String tempToken) {
        MutableLiveData<Resource<List<SystemBean.ListBean>>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .getSystemList(tempToken, organizationId)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<SystemBean>() {
                    @Override
                    public void onNext(SystemBean systemBean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, systemBean.list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<OrgAndSysBean>> submitOrgSystem(OrgSystemBean bean, String tempToken) {
        MutableLiveData<Resource<OrgAndSysBean>> liveData = new ValueKeeperLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("organizationId", bean.getmOrganizationId());
        map.put("sysId", bean.getmSystemId());
        map.put("sysUrl", HttpUtils.getGatewayUrl());
        HttpUtils.getGatewayApi(ApiService.class)
                .submitOrgSys(tempToken, map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(orgAndSysBean -> {
                    try {
                        MMKVUtils.save(ConstantUtil.USER_NAME, orgAndSysBean.organizationCache.employeeCache.employeeName);
                        MMKVUtils.save(ConstantUtil.USER_ID, orgAndSysBean.organizationCache.employeeCache.employeeId);
                    } catch (NullPointerException e) {
                        MMKVUtils.save(ConstantUtil.USER_NAME, orgAndSysBean.userName);
                        MMKVUtils.save(ConstantUtil.USER_ID, orgAndSysBean.userId);
                    }
                    return orgAndSysBean;
                })
                .subscribe(new CustomObserver<OrgAndSysBean>() {
                    @Override
                    public void onNext(OrgAndSysBean orgAndSysBean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, orgAndSysBean, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

    public long putData(UserEntity entity) {
        return userOperator.getUserIdByInfo(entity);
    }

}
