package com.jgw.delingha.module.query.flow.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityFlowQuerySettingBinding;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class FlowQuerySettingActivity extends BaseActivity<BaseViewModel, ActivityFlowQuerySettingBinding> implements EditText.OnKeyListener, TextWatcher {

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.flow_query_setting_title));
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.ivClear)
                .addOnClickListener()
                .submit();
        mBindingView.etInput.setOnKeyListener(this);
        mBindingView.etInput.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.iv_clear) {
            mBindingView.etInput.setText("");
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            ScanCodeService.playSuccess();
            FlowQueryActivity.start(this, code);
        } else {
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
            ScanCodeService.playError();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String str = editable.toString();
        if (!TextUtils.isEmpty(str)) {
            mBindingView.ivClear.setVisibility(View.VISIBLE);
        } else {
            mBindingView.ivClear.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            String code = mBindingView.etInput.getText().toString();
            EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(code));
        }
        return false;
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
            Intent intent = new Intent(context, FlowQuerySettingActivity.class);
            context.startActivity(intent);
        }
    }
}
