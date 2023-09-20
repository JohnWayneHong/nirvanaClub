package com.jgw.delingha.module.login.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.CompoundButton;

import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.LoginBean;
import com.jgw.delingha.databinding.FragmentLoginBinding;
import com.jgw.delingha.module.login.event.LoginEvent;
import com.jgw.delingha.module.login.event.SelectOrgSystemEvent;
import com.jgw.delingha.module.login.viewmodel.LoginViewModel;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.utils.PasswordUtils;
import com.jgw.delingha.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

public class LoginFragment extends BaseFragment<LoginViewModel, FragmentLoginBinding> implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    @Override
    protected void initView() {
        mBindingView.cbLoginRemember.setChecked(MMKVUtils.getBoolean(ConstantUtil.USER_REMEMBER_ME));
        ViewUtils.expandTouchArea(mBindingView.cbLoginRemember, 40, 40, 40, 40);
    }

    @Override
    protected void initFragmentData() {
        initAccount();
    }

    @Override
    public void initLiveData() {
        mViewModel.login().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    String phone = mBindingView.etLoginMobile.getText().toString().trim();
                    String password = mBindingView.etLoginPwd.getText().toString().trim();
                    MMKVUtils.save(ConstantUtil.NEED_CHANGE_PASSWORD, PasswordUtils.strongPasswordIsLegal(password) ? 0 : 1);
                    MMKVUtils.save(ConstantUtil.TODAY_CHECK_NEED_CHANGE_PASSWORD, "");
                    EventBus.getDefault().post(new LoginEvent.LoginSuccessEvent(resource.getData().token, phone, password));
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvLoginSubmit, mBindingView.ivLoginPwdSwitch,
                        mBindingView.ivLoginMobileRemove, mBindingView.ivLoginPwdRemove)
                .submit();

        mBindingView.etLoginMobile.addTextChangedListener(this);
        mBindingView.etLoginPwd.addTextChangedListener(this);
        mBindingView.cbLoginRemember.setOnCheckedChangeListener(this);
    }

    private void initAccount() {
        if (MMKVUtils.getBoolean(ConstantUtil.USER_REMEMBER_ME)) {
            mBindingView.etLoginMobile.setText(MMKVUtils.getString(ConstantUtil.USER_MOBILE));
            mBindingView.etLoginMobile.setSelection(mBindingView.etLoginMobile.getText().toString().length());
            mBindingView.etLoginPwd.setText(MMKVUtils.getString(ConstantUtil.USER_PASSWORD));
            mBindingView.tvLoginSubmit.setEnabled(checkInput());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mBindingView.ivLoginMobileRemove.setVisibility(TextUtils.isEmpty(mBindingView.etLoginMobile.getText().toString()) ? View.GONE : View.VISIBLE);
        mBindingView.ivLoginPwdRemove.setVisibility(TextUtils.isEmpty(mBindingView.etLoginPwd.getText().toString()) ? View.GONE : View.VISIBLE);
        mBindingView.etLoginPwd.setSelection(mBindingView.etLoginPwd.getText().toString().length());
        mBindingView.tvLoginSubmit.setEnabled(checkInput());
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_login_submit) {
            login();
        } else if (id == R.id.iv_login_pwd_switch) {
            switchPasswordView();
        } else if (id == R.id.iv_login_mobile_remove) {
            mBindingView.etLoginMobile.setText("");
        } else if (id == R.id.iv_login_pwd_remove) {
            mBindingView.etLoginPwd.setText("");
        }
    }

    private void switchPasswordView() {
        boolean isSelected = mBindingView.ivLoginPwdSwitch.isSelected();
        TransformationMethod transformationMethod = isSelected ? PasswordTransformationMethod.getInstance()
                : HideReturnsTransformationMethod.getInstance();
        mBindingView.etLoginPwd.setTransformationMethod(transformationMethod);
        mBindingView.ivLoginPwdSwitch.setSelected(!isSelected);
    }

    private boolean checkInput() {
        return !TextUtils.isEmpty(mBindingView.etLoginMobile.getText().toString().trim())
                && !TextUtils.isEmpty(mBindingView.etLoginPwd.getText().toString().trim());
    }

    private void login() {
        String mobile = mBindingView.etLoginMobile.getText().toString().trim();
        String pwd = mBindingView.etLoginPwd.getText().toString().trim();
        if (!NetUtils.iConnected()) {
            String orgId = MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID);//用来判断是否成功登录
            if (!TextUtils.isEmpty(orgId)) {
                String localMobile = MMKVUtils.getString(ConstantUtil.USER_MOBILE);
                String localPWD = MMKVUtils.getString(ConstantUtil.USER_PASSWORD);
                if (TextUtils.equals(mobile, localMobile) && TextUtils.equals(localPWD, pwd)) {
                    EventBus.getDefault().post(new SelectOrgSystemEvent.submitOrgSysSuccessEvent());
                } else {
                    ToastUtils.showToast("账号或密码错误，请重试");
                }
            } else {
                ToastUtils.showToast("请先在线登录成功后方可离线登录!");
            }
        } else {
            LoginBean bean = new LoginBean(mobile, pwd);
            mViewModel.login(bean);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MMKVUtils.save(ConstantUtil.USER_REMEMBER_ME, isChecked);
    }

}
