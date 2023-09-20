package com.jgw.delingha.module.setting_center.ui;

import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.FragmentSettingCenterBinding;
import com.jgw.delingha.module.about_us.AboutUsActivity;
import com.jgw.delingha.module.login.ui.LoginActivity;
import com.jgw.delingha.module.setting_center.viewModel.SettingCenterViewModel;
import com.jgw.delingha.utils.CheckUpdateUtils;

import java.util.List;

public class SettingCenterFragment extends BaseFragment<SettingCenterViewModel, FragmentSettingCenterBinding> {


    @Override
    protected void initView() {
    }

    @Override
    protected void initFragmentData() {
        mViewModel.setToolsTableHeaderData();
        mBindingView.setData(mViewModel.getToolsTableHeaderData());
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setToolsTableHeaderData();
        mBindingView.setData(mViewModel.getToolsTableHeaderData());
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getUpdateOfflineLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    ToastUtils.showToast(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getClearDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ToastUtils.showToast(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getSaveLocalDataToFileLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    ToastUtils.showToast("导出离线数据成功");
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getLogoutLiveData().observe(this, resource -> {
            FragmentActivity activity = getActivity();
            if (BaseActivity.isActivityNotFinished(activity)){
                LoginActivity.start(activity);
                activity.finish();
            }
        });

    }

    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.rlAboutUs, mBindingView.rlVersion, mBindingView.tvSignOut)
                .addView(mBindingView.rlUpdateOfflineData)
                .addView(mBindingView.rlClearData)
                .addView(mBindingView.ivSettingCenterAvatar)
                .addOnClickListener()
                .submit();

        findViewById(R.id.iv_setting_center_avatar).setOnClickListener(v -> {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    if (elapsedTime < MIN_CLICK_INTERVAL) {
                        ++mSecretNumber;
                        if (9 == mSecretNumber) {
                            // to do 在这处理你想做的事件
                            outputOfflineData();
                        }
                    } else {
                        mSecretNumber = 0;
                    }
                }
        );
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_version) {
            checkUpdateVersion();
        } else if (id == R.id.rl_about_us) {
            AboutUsActivity.start(getContext());
        } else if (id == R.id.rl_update_offline_data) {
            mViewModel.updateOfflineData();
        } else if (id == R.id.rl_clear_data) {
            showClearDataDialog();
        } else if (id == R.id.tv_sign_out) {
            tryLogout();
        }
    }

    private void checkUpdateVersion() {
        CheckUpdateUtils.getVersionType(true, getActivity());
    }

    private void showClearDataDialog() {
        CommonDialogUtil.showSelectDialog(context,"确认清除?", "清除后需要手动点击更新数据,获取基础数据", "否", "是",
               new CommonDialog.OnButtonClickListener() {

                    @Override
                    public void onRightClick() {
                        CommonDialogUtil.showSelectDialog(context,"警告", "当前所有扫码数据都会清空!请再次确认!", "取消", "确定",
                                new CommonDialog.OnButtonClickListener() {
                                   @Override
                                    public void onRightClick() {
                                        mViewModel.clearData();
                                    }
                                });
                    }
                });
    }


    private void tryLogout() {
        CommonDialogUtil.showSelectDialog(context,"是否确定退出程序?", "", "考虑一下", "退出", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                //做离线登录 退出登录不清空用户数据
                mViewModel.logout();
            }
        });
    }

    private void outputOfflineData() {
        XXPermissions.with(this)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean all) {
                        if (!all) {
                            return;
                        }
                        CommonDialogUtil.showInputDialog(getActivity(), "提示", "是否确认导出离线数据?", "取消", "确认", InputType.TYPE_CLASS_NUMBER,new CommonDialog.OnButtonClickListener() {
                            String code = null;
                            @Override
                            public boolean onInput(String input) {
                                if (TextUtils.isEmpty(input)){
                                    return false;
                                }
                                if (input.length()!=4){
                                    return false;
                                }
                                code=input;
                                return true;
                            }

                            @Override
                            public void onRightClick() {
                                mViewModel.saveLocalDataToFile(code);
                            }
                        });
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean never) {
                        CommonDialogUtil.showSelectDialog(getActivity(), "权限不足", "系统权限不足,无法正常使用,是否前往系统设置?",
                                "取消", "设置", new CommonDialog.OnButtonClickListener() {
                                    @Override
                                    public void onRightClick() {
                                        XXPermissions.startPermissionActivity(getActivity(), permissions);
                                    }
                                });
                    }
                });

    }

}
