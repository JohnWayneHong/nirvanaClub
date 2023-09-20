package com.jgw.delingha.module.supplement_to_box.base.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.PackageCodeInfoBean;
import com.jgw.delingha.databinding.ActivitySupplementToBoxBinding;
import com.jgw.delingha.module.scan_back.ui.CommonJsonScanBackActivity;
import com.jgw.delingha.module.supplement_to_box.base.viewmodel.SupplementToBoxViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/12/18
 */
public class SupplementToBoxActivity extends BaseActivity<SupplementToBoxViewModel, ActivitySupplementToBoxBinding> {

    private CodeEntityRecyclerAdapter<CodeBean> mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcSupplementToBoxSubCode.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("补码入箱");
        mAdapter = new CodeEntityRecyclerAdapter<>();
        mBindingView.rvSupplementToBoxSubCode.setAdapter(mAdapter);
        mViewModel.setListData(mAdapter.getDataList());
        calculationTotal();
    }

    @Override
    public void initLiveData() {
        mViewModel.getCheckParentCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    updateParentCodeStatus(resource.getErrorMsg(), CodeBean.STATUS_CODE_VERIFYING, resource.getData());
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    updateParentCodeStatus(resource.getErrorMsg(), CodeBean.STATUS_CODE_SUCCESS, resource.getData());
                    mViewModel.setParentCodeInfo(resource.getData());
                    break;
                case Resource.ERROR:
                    onCodeError();
                    dismissLoadingDialog();
                    updateParentCodeStatus(resource.getErrorMsg(), CodeBean.STATUS_CODE_FAIL, resource.getData());
                    mViewModel.resetParentCodeInfo();
                    break;
            }
        });
        mViewModel.getCheckSonCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    calculationTotal();
                    break;
                case Resource.SUCCESS:
                    updateCodeStatus(resource.getData(), CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    updateCodeStatus(resource.getData(), CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });
        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    showUploadDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void updateParentCodeStatus(String code, int status, PackageCodeInfoBean data) {
        CodeBean codeBean = new CodeBean();
        codeBean.outerCodeId = code;
        codeBean.codeStatus = status;
        mBindingView.layoutSupplementToBoxUpperCode.setData(codeBean);
        mBindingView.includeSupplementToBoxHeader.setData(data);
        switch (status) {
            case CodeBean.STATUS_CODE_VERIFYING:
                mBindingView.rlSupplementToBoxUpperCode.setVisibility(View.VISIBLE);
                mBindingView.includeSupplementToBoxHeader.llSupplementToBoxHeader.setVisibility(View.GONE);
                break;
            case CodeBean.STATUS_CODE_SUCCESS:
                mBindingView.rlSupplementToBoxUpperCode.setVisibility(View.VISIBLE);
                mBindingView.includeSupplementToBoxHeader.llSupplementToBoxHeader.setVisibility(View.VISIBLE);
                break;
            case CodeBean.STATUS_CODE_FAIL:
                mBindingView.rlSupplementToBoxUpperCode.setVisibility(View.GONE);
                mBindingView.includeSupplementToBoxHeader.llSupplementToBoxHeader.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvScanBack)
                .addView(mBindingView.tvSure)
                .submit();
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;
        scanCode = CheckCodeUtils.getMatcherDeliveryNumber(scanCode);
        if (!CheckCodeUtils.checkCode(scanCode)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", scanCode, "知道了", new CommonDialog.OnButtonClickListener() {});
        } else if (mViewModel.checkCodeExisted(scanCode)) {
            onCodeError();
        } else {
            if (!mViewModel.hasParentCode()) {
                mViewModel.onScanParentCode(scanCode);
            } else {
                if (mViewModel.checkBoxFull()) {
                    onCodeError();
                } else if (mBindingView.layoutSupplementToBoxUpperCode.getData() == null
                        || mBindingView.layoutSupplementToBoxUpperCode.getData().getCodeStatus() != CodeBean.STATUS_CODE_SUCCESS) {
                    onCodeError();
                    ToastUtils.showToast("上级码还在验证中,请稍后");
                } else {
                    mViewModel.onScanCode(scanCode, mAdapter, mBindingView.rvSupplementToBoxSubCode);
                }
            }
            ScanCodeService.playSuccess();
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_scan_back) {
            if (mAdapter.isEmpty()) {
                ToastUtils.showToast("请先扫码!");
                return;
            }
            CommonJsonScanBackActivity.start(this, mAdapter.getDataList(), 1);
        } else if (id == R.id.tv_sure) {
            mViewModel.upload();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (requestCode) {
                case 1:
                    List<CodeBean> codeBeans = MMKVUtils.getTempDataList(CodeBean.class);
                    mAdapter.notifyRefreshList(codeBeans);
                    calculationTotal();
                    break;
            }
        }
    }

    private void updateCodeStatus(String code, int status) {
        CodeBean bean = new CodeBean(code);
        mAdapter.notifyRefreshItemStatus(code, status);
        if (status == CodeBean.STATUS_CODE_FAIL) {
            mAdapter.notifyRemoveItem(bean);
            calculationTotal();
        }
    }

    public void showUploadDialog() {
        CommonDialogUtil.showSelectDialog(this,new SpannableString("数据上传成功"), Html.fromHtml("您成功上传了<font color='#03A9F4'>" + mAdapter.getDataList().size() +
                "</font>条数据.<br>请稍后查看<font color='#03A9F4'>任务状态</font>"), "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX, SupplementToBoxActivity.this);
                finish();
            }
        });
    }

    private void calculationTotal() {
        int size = mAdapter.getDataList().size();
        mBindingView.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
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
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, SupplementToBoxActivity.class);
            context.startActivity(intent);
        }
    }
}
