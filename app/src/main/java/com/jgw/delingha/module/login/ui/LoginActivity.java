package com.jgw.delingha.module.login.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityLoginBinding;
import com.jgw.delingha.module.login.event.LoginEvent;
import com.jgw.delingha.module.login.event.SelectOrgSystemEvent;
import com.jgw.delingha.module.login.viewmodel.LoginContentViewModel;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.utils.ConstantUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("rawtypes")
public class LoginActivity extends BaseActivity<LoginContentViewModel, ActivityLoginBinding> {

    public static final int LOGIN_POSITION = 0;
    public static final int SELECT_ORG_SYS_POSITION = 1;

    private List<BaseFragment> mFragments;
    private ArrayList<String> tags;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        //进入LoginActivity意味token失效 路由跳转不进入start方法 改为此处清除失效token
        MMKVUtils.save(ConstantUtil.USER_TOKEN, "");

        mViewModel.initFragments(fm);
        mFragments = mViewModel.getFragments();
        tags = mViewModel.getTags();
        jumpToFragment(LOGIN_POSITION);
    }

    private void jumpToFragment(int index) {

        if (mViewModel.getLastPage() != -1 && mViewModel.getLastPage() != index) {
            Fragment baseFragment = mFragments.get(mViewModel.getLastPage());
            if (mViewModel.getLastPage() == LOGIN_POSITION) {
                fm.beginTransaction()
                        .setCustomAnimations(com.jgw.common_library.R.anim.custom_slide_in_left, com.jgw.common_library.R.anim.custom_slide_out_left)
                        .hide(baseFragment)
                        .commitAllowingStateLoss();
            } else {
                fm.beginTransaction()
                        .setCustomAnimations(com.jgw.common_library.R.anim.custom_slide_in_right, com.jgw.common_library.R.anim.custom_slide_out_right)
                        .hide(baseFragment)
                        .commitAllowingStateLoss();
            }
        }
        String tag = tags.get(index);
        BaseFragment baseFragment = mFragments.get(index);
        if (!baseFragment.isAdded() && null == fm.findFragmentByTag(tag)) {
            fm.beginTransaction()
                    .setCustomAnimations(com.jgw.common_library.R.anim.custom_slide_in_left, com.jgw.common_library.R.anim.custom_slide_out_left)
                    .add(R.id.fl_login_content, baseFragment, tag).
                    addToBackStack(null)
                    .commitAllowingStateLoss();
        }
        if (mViewModel.getLastPage() == SELECT_ORG_SYS_POSITION) {
            fm.beginTransaction()
                    .setCustomAnimations(com.jgw.common_library.R.anim.custom_slide_in_right, com.jgw.common_library.R.anim.custom_slide_out_right)
                    .show(baseFragment)
                    .commitAllowingStateLoss();
        } else {
            fm.beginTransaction().setCustomAnimations(com.jgw.common_library.R.anim.custom_slide_in_left, com.jgw.common_library.R.anim.custom_slide_out_left)
                    .show(baseFragment)
                    .commitAllowingStateLoss();
        }
        mViewModel.setLastPage(index);

    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onLoginSuccess(LoginEvent.LoginSuccessEvent event) {
        if (MMKVUtils.getBoolean(ConstantUtil.USER_REMEMBER_ME)) {
            MMKVUtils.save(ConstantUtil.USER_PASSWORD, event.password);
        } else {
            MMKVUtils.save(ConstantUtil.USER_PASSWORD, "");
        }
        MMKVUtils.save(ConstantUtil.USER_MOBILE, event.mobile);
        mViewModel.setTempToken(event.token);
        jumpToFragment(SELECT_ORG_SYS_POSITION);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSubmitOrgSysSuccess(SelectOrgSystemEvent.submitOrgSysSuccessEvent event) {
        MainActivity.start(this, 0);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.getLastPage() == -1) {
            Log.wtf("wtf", "??????");
            finish();
            return;
        }
        if (mViewModel.getLastPage() == LOGIN_POSITION) {
            finish();
        } else if (mViewModel.getLastPage() == SELECT_ORG_SYS_POSITION) {
            MMKVUtils.save(ConstantUtil.USER_TOKEN, "");
            jumpToFragment(LOGIN_POSITION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
