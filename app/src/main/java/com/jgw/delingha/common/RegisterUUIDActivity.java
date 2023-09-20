package com.jgw.delingha.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.DevicesUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.RegisterBean;
import com.jgw.delingha.bean.RegisterDeviceBean;
import com.jgw.delingha.common.viewmodel.RegisterUUIDViewModel;
import com.jgw.delingha.databinding.ActivityRegisterBinding;
import com.jgw.delingha.module.login.ui.LoginActivity;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.select_list.organization_list.OrganizationListActivity;
import com.jgw.delingha.utils.ConstantUtil;

public class RegisterUUIDActivity extends BaseActivity<RegisterUUIDViewModel, ActivityRegisterBinding> {
    public static final int UNREGISTERED = 1;
    public static final int WAITED_REVIEW = 2;
    public static final int DISABLE = 3;
    public static final int REGISTERED = 4;

    @Override
    protected void initView() {
        setTitle("注册备案");
    }

    @Override
    protected void initData() {
        mBindingView.tvRegisterRegister.setEnabled(false);
        RegisterBean registerBean = new RegisterBean();
        RegisterDeviceBean bean = MMKVUtils.getTempData(RegisterDeviceBean.class);
        registerBean.setType(bean.type);
        registerBean.setErrorMessage(bean.errorMessage);
        mBindingView.setData(registerBean);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getRegisterLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mBindingView.getData().setType(WAITED_REVIEW);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRefreshRegisterInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    jump();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    RegisterDeviceBean data = resource.getData();
                    mBindingView.getData().setType(data.type);
                    mBindingView.getData().setErrorMessage(data.errorMessage);
                    break;
                case Resource.NETWORK_ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void jump() {
        MMKVUtils.save(ConstantUtil.FIRST_IN, false);
        String string = MMKVUtils.getString(ConstantUtil.USER_TOKEN);
        if (TextUtils.isEmpty(string)) {
            LoginActivity.start(this);
        } else {
            MainActivity.start(this, MMKVUtils.getInt("index"));
        }
        finish();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvRegisterSelectContent)
                .addView(mBindingView.ivRegisterSelectOrg)
                .addView(mBindingView.tvRegisterRegister)
                .addView(mBindingView.tvRegisterRefresh)
                .addView(mBindingView.tvRegisterReRegister)
                .addOnClickListener()
                .submit();

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_register_select_content || id == R.id.iv_register_select_org) {
            OrganizationListActivity.start(1, this);
        } else if (id == R.id.tv_register_register) {
            tryRegister();
        } else if (id == R.id.tv_register_refresh) {
            mViewModel.refreshRegisterInfo(DevicesUtils.getDevicesSerialNumber());
        } else if (id == R.id.tv_register_re_register) {
            mBindingView.getData().setRemarks("");
            mBindingView.getData().setCompanyName("");
            mBindingView.getData().setCompanyId("");
            mBindingView.getData().setType(UNREGISTERED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == 1) {
            // 选择公司
            String companyId = data.getStringExtra(OrganizationListActivity.EXTRA_NAME_ID);
            String companyName = data.getStringExtra(OrganizationListActivity.EXTRA_NAME_NAME);
            mBindingView.getData().setCompanyName(companyName);
            mBindingView.getData().setCompanyId(companyId);
            mBindingView.tvRegisterRegister.setEnabled(true);
        }
    }

    private void tryRegister() {
        if (TextUtils.isEmpty(mBindingView.getData().getCompanyName())) {
            ToastUtils.showToast("请选择企业!");
            return;
        }
        String brand = Build.MANUFACTURER + Build.BRAND;
        mBindingView.getData().setBrand(brand);
        mBindingView.getData().setModelType(Build.MODEL);
        mBindingView.getData().setSecretKey(DevicesUtils.getDevicesSerialNumber());
        mViewModel.register(mBindingView.getData());
    }

    public static void start(Context context, RegisterDeviceBean bean) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, RegisterUUIDActivity.class);
            MMKVUtils.saveTempData(bean);
            context.startActivity(intent);
        }
    }
}
