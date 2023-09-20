package com.jgw.delingha.module.login.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.LoginBean;
import com.jgw.delingha.bean.UserBean;
import com.jgw.delingha.module.login.model.LoginModel;

public class LoginViewModel extends BaseViewModel {

    private final LoginModel model;
    private final MutableLiveData<LoginBean> loginLiveData = new ValueKeeperLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        model = new LoginModel();
    }

    public void login(LoginBean bean) {
        loginLiveData.setValue(bean);
    }

    public LiveData<Resource<UserBean>> login() {
        return Transformations.switchMap(loginLiveData, model::login);
    }

}
