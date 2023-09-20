package com.jgw.delingha.module.identification_replace;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityIdentificationReplaceBinding;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author : J-T
 * @date : 2022/8/2 10:22
 * description :标识替换Activity
 */
public class IdentificationReplaceActivity extends BaseActivity<IdentificationReplaceViewModel,
        ActivityIdentificationReplaceBinding> {


    public static int POSITION_SOURCE = 1;
    public static int POSITION_TARGET = 2;

    @Override
    protected void initView() {
        setTitle("标识替换");
    }

    @Override
    protected void initData() {
        mBindingView.setData(mViewModel.getData());
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvIdentificationReplaceUpload)
                .addView(mBindingView.ivIdentificationReplaceSourceClear)
                .addView(mBindingView.ivIdentificationReplaceTargetClear)
                .addView(mBindingView.clIdentificationReplaceSourceCodeContainer)
                .addView(mBindingView.clIdentificationReplaceTargetCodeContainer)
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.cl_identification_replace_source_code_container) {
            showInputDialog(POSITION_SOURCE);
        } else if (id == R.id.cl_identification_replace_target_code_container) {
            showInputDialog(POSITION_TARGET);
        } else if (id == R.id.iv_identification_replace_source_clear) {
            mViewModel.inputSourceCode("");
        } else if (id == R.id.iv_identification_replace_target_clear) {
            mViewModel.inputTargetCode("");
        } else if (id == R.id.tv_identification_replace_upload) {
            tryUpload();
        }
    }

    @Override
    public void initLiveData() {
        mViewModel.getCheckCodeLiveData().observe(this, integerResource -> {
            switch (integerResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    Integer data = integerResource.getData();
                    if (data == 1) {
                        // 校验旧码
                        mViewModel.inputSourceCode("");
                    } else if (data == 2) {
                        //校验新码
                        mViewModel.inputTargetCode("");
                    }
                    ToastUtils.showToast(integerResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getUploadLiveData().observe(this, stringResource -> {
            switch (stringResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    showUploadSuccessDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(stringResource.getErrorMsg());
                    break;
            }
        });
    }

    public void showInputDialog(int position) {
        CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {

            @Override
            public boolean onInput(String input) {
                mViewModel.checkCode(input, position);
                return true;
            }
        });
    }

    public void showTipsDialog() {
        CommonDialogUtil.showSelectDialog(this,"提醒!", "原码将不能查询到历史操作记录.\n是否要继续执行替换?",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {

                    @Override
                    public void onRightClick() {
                        mViewModel.uploadIdentificationReplace();
                    }
                });
    }

    public void showUploadSuccessDialog() {
        CommonDialogUtil.showConfirmDialog(this, "替换成功", "请稍后核查替换的正确性", "确定"
                , new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onRightClick() {
                        finish();
                    }
                });
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (CheckCodeUtils.checkCode(code)) {
            //扫码根据当前页面填充情况 判断是旧码还是新码
            if (TextUtils.isEmpty(mBindingView.getData().getSourceCode())) {
                //原身份码为空 优先填充原身份码
                mViewModel.checkCode(code, POSITION_SOURCE);
            } else {
                //已有原身份码的情况下  只会填充新身份码
                mViewModel.checkCode(code, POSITION_TARGET);
            }
        } else {
            ScanCodeService.playError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不正确!请仔细核对", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        }
    }

    public void tryUpload() {
        //展示弹窗 并检查码是否为空
        if (TextUtils.isEmpty(mBindingView.getData().getSourceCode()) ||
                TextUtils.isEmpty(mBindingView.getData().getTargetCode())) {
            ToastUtils.showToast("身份码不能为空!");
            return;
        }
        showTipsDialog();
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
            Intent intent = new Intent(context, IdentificationReplaceActivity.class);
            context.startActivity(intent);
        }
    }

}
