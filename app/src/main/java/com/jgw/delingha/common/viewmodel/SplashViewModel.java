package com.jgw.delingha.common.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.delingha.bean.RegisterDeviceBean;
import com.jgw.delingha.common.model.SplashModel;

/**
 * @author : J-T
 * @date : 2022/5/6 13:47
 * description : 闪屏页ViewModel
 */
public class SplashViewModel extends BaseViewModel {
    /**
     * 注册LiveData
     */
    private final MutableLiveData<String> registerLiveData = new MutableLiveData<>();
    private String secretKey;
    private String oldSecretKey;
    private final SplashModel mModel;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        mModel = new SplashModel();
    }

    public void getRegisterInfo(String serialNumber, String oldDevicesSerialNumber) {
        this.secretKey = serialNumber;
        this.oldSecretKey = oldDevicesSerialNumber;
        registerLiveData.setValue("");
    }

    public LiveData<Resource<RegisterDeviceBean>> getRegisterLiveData() {
        return Transformations.switchMap(registerLiveData, input -> mModel.getRegisterInfo(secretKey, oldSecretKey));
    }


}
