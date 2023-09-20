package com.jgw.delingha.module.login.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.StringUtil;
import com.jgw.delingha.bean.LoginBean;
import com.jgw.delingha.bean.UserBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;

public class LoginModel {

    public LiveData<Resource<UserBean>> login(LoginBean bean) {
        LogUtils.xswShowLog("phone = " + bean.getMobile() + " password = " + StringUtil.md5Decode(bean.getPwd()));
        MutableLiveData<Resource<UserBean>> liveData = new ValueKeeperLiveData<>();
        Map<String, Object> map = new HashMap<>();
        map.put("account", bean.getMobile());
        map.put("password", StringUtil.md5Decode(bean.getPwd()));
        map.put("tokenTimeout", 3600 * 24 * 30);
        HttpUtils.getGatewayApi(ApiService.class)
                .login(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<UserBean>() {
                    @Override
                    public void onNext(UserBean userBean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, userBean, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }

}
