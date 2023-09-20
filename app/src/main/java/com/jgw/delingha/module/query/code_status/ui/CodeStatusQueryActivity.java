package com.jgw.delingha.module.query.code_status.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCodeStatusQueryBinding;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author : J-T
 * @date : 2022/7/21 10:01
 * description : 码状态查询Activity
 */
public class CodeStatusQueryActivity extends BaseActivity<BaseViewModel, ActivityCodeStatusQueryBinding> {

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.code_status_query));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvCodeStatusQueryInputIdCode)
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_code_status_query_input_id_code) {
            showInputDialog();
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (CheckCodeUtils.checkCode(code)) {
            CodeStatusQueryDetailsActivity.start(this, code);
        } else {
            ScanCodeService.playError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不正确!请仔细核对", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        }
    }

    public void showInputDialog() {
        CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            @Override
            public boolean onInput(String input) {
                EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(input));
                return true;
            }
        });
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
            Intent intent = new Intent(context, CodeStatusQueryActivity.class);
            context.startActivity(intent);
        }
    }
}
