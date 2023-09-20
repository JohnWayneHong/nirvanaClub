package com.jgw.delingha.common.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.RegisterBean;
import com.jgw.delingha.bean.RegisterDeviceBean;
import com.jgw.delingha.common.model.RegisterUUIDModel;

/**
 * @author : J-T
 * @date : 2022/5/7 9:38
 * description :
 */
public class RegisterUUIDViewModel extends BaseViewModel {
    private final RegisterUUIDModel mModel;
    private final MutableLiveData<RegisterBean> registerLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> refreshLiveData = new ValueKeeperLiveData<>();

    public RegisterUUIDViewModel(@NonNull Application application) {
        super(application);
        mModel = new RegisterUUIDModel();
    }

    public void register(RegisterBean bean) {
        registerLiveData.setValue(bean);
    }

    public LiveData<Resource<String>> getRegisterLiveData() {
        return Transformations.switchMap(registerLiveData, mModel::register);
    }

    public void refreshRegisterInfo(String secretKey) {
        refreshLiveData.setValue(secretKey);
    }

    public LiveData<Resource<RegisterDeviceBean>> getRefreshRegisterInfoLiveData() {
        return Transformations.switchMap(refreshLiveData, mModel::refreshRegisterState);
    }
}
