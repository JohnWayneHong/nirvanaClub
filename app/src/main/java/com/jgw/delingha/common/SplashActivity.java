package com.jgw.delingha.common;


import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.DevicesUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.BuildConfig;
import com.jgw.delingha.bean.RegisterDeviceBean;
import com.jgw.delingha.common.viewmodel.SplashViewModel;
import com.jgw.delingha.databinding.ActivitySplashBinding;
import com.jgw.delingha.module.login.ui.LoginActivity;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.List;


public class SplashActivity extends BaseActivity<SplashViewModel, ActivitySplashBinding> {

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        requestPermission();
    }

    private void checkLaw() {
        if (checkTestDevice()) {
            gotoLogin();
            return;
        }
        String serialNumber = DevicesUtils.getDevicesSerialNumber();
        String oldDevicesSerialNumber = DevicesUtils.getOldDevicesSerialNumber();
        if (TextUtils.isEmpty(serialNumber)) {
            ToastUtils.showToast("未知设备!");
            return;
        }
        mViewModel.getRegisterInfo(serialNumber, oldDevicesSerialNumber);
    }

    private boolean checkTestDevice() {
        if (!BuildConfig.DEBUG) {
            return false;
        }
        String model = Build.MODEL;
        switch (model) {
            case "M2012K11AC":
            case "SM-G9500":
            case "23013RK75C":
            case "T2U":
                return true;
            default:
                return false;

        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getRegisterLiveData().observe(this, registerDeviceBeanResource -> {
            switch (registerDeviceBeanResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    RegisterDeviceBean data = registerDeviceBeanResource.getData();
                    dealWithRegister(data);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    RegisterDeviceBean registerDeviceBean = new RegisterDeviceBean();
                    if (isFirstIn()) {
                        // 如果无网络 并且没有注册过 去注册设备页面
                        registerDeviceBean.type = RegisterUUIDActivity.UNREGISTERED;
                    } else {
                        //如果无网络,并且之前已经注册过 直接进
                        registerDeviceBean.type = 0;
                    }
                    dealWithRegister(registerDeviceBean);
                    break;
            }
        });
    }

    private void dealWithRegister(RegisterDeviceBean bean) {
        switch (bean.type) {
            case -1:
                ToastUtils.showToast(bean.errorMessage);
                break;
            case 0:
                putFirstIn();
                gotoLogin();
                break;
            case RegisterUUIDActivity.UNREGISTERED:
            case RegisterUUIDActivity.WAITED_REVIEW:
            case RegisterUUIDActivity.DISABLE:
                RegisterUUIDActivity.start(this, bean);
                finish();
        }
    }

    private void gotoLogin() {
        String string = MMKVUtils.getString(ConstantUtil.USER_TOKEN);
        if (TextUtils.isEmpty(string)) {
            LoginActivity.start(this);
        } else {
            MainActivity.start(this, MMKVUtils.getInt("index"));
        }
        finish();
    }


    //-------------------------检查pda合法性--------------------------------------
    private void putFirstIn() {
        MMKVUtils.save(ConstantUtil.FIRST_IN, false);
    }

    private boolean isFirstIn() {
        return MMKVUtils.getBoolean(ConstantUtil.FIRST_IN, true);
    }

    //-------------------------权限处理------------------------------------------
    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_PHONE_STATE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        // 检查设备合法性
                        if (!all) {
                            return;
                        }
                        checkLaw();
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        CommonDialogUtil.showSelectDialog(SplashActivity.this,"权限不足", "系统权限不足,无法正常使用,是否前往系统设置?",
                                "取消", "设置", new CommonDialog.OnButtonClickListener() {
                                    @Override
                                    public void onLeftClick() {
                                        finish();
                                    }

                                    @Override
                                    public void onRightClick() {
                                        XXPermissions.startPermissionActivity(SplashActivity.this);
                                    }
                                });
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ToastUtils.showToast("授权成功");

                // 检查设备合法性
                checkLaw();
            } else {
                ToastUtils.showToast("授权失败");
            }
        }
    }
}
