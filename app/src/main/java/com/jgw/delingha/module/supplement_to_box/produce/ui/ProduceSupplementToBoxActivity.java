package com.jgw.delingha.module.supplement_to_box.produce.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.ProducePackageCodeInfoBean;
import com.jgw.delingha.databinding.ActivityProduceSupplementToBoxBinding;
import com.jgw.delingha.module.scan_back.ui.CommonJsonScanBackActivity;
import com.jgw.delingha.module.supplement_to_box.produce.adapter.ProduceSupplementToBoxRecyclerAdapter;
import com.jgw.delingha.module.supplement_to_box.produce.viewmodel.ProduceSupplementToBoxViewModel;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2021/03/08
 */
public class ProduceSupplementToBoxActivity extends BaseActivity<ProduceSupplementToBoxViewModel, ActivityProduceSupplementToBoxBinding> {

    private ProduceSupplementToBoxRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcSupplementToBoxSubCode.setEmptyLayout(R.layout.item_stock_action_no_data);
    }

    @Override
    protected void initData() {
        setTitle("生产补码");
        mAdapter = new ProduceSupplementToBoxRecyclerAdapter();
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
                    mViewModel.setParentCodeInfo(resource.getData());
                    updateParentCodeStatus(resource.getErrorMsg(), CodeBean.STATUS_CODE_SUCCESS, resource.getData());
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
                    showUploadDialog(resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void updateParentCodeStatus(String code, int status, ProducePackageCodeInfoBean data) {
        CodeBean codeBean = new CodeBean();
        codeBean.outerCodeId = code;
        codeBean.codeStatus = status;
        mBindingView.layoutSupplementToBoxUpperCode.setData(codeBean);
        switch (status) {
            case CodeBean.STATUS_CODE_VERIFYING:
                mBindingView.rlSupplementToBoxUpperCode.setVisibility(View.VISIBLE);
                mBindingView.layoutSupplementToBoxUpperCode.ivCheckOk.setVisibility(View.GONE);
                mBindingView.layoutSupplementToBoxUpperCode.tvChecking.setVisibility(View.VISIBLE);
                mBindingView.includeSupplementToBoxHeader.llSupplementToBoxHeader.setVisibility(View.GONE);
                break;
            case CodeBean.STATUS_CODE_SUCCESS:
                mBindingView.rlSupplementToBoxUpperCode.setVisibility(View.VISIBLE);
                mBindingView.layoutSupplementToBoxUpperCode.ivCheckOk.setVisibility(View.VISIBLE);
                mBindingView.layoutSupplementToBoxUpperCode.tvChecking.setVisibility(View.GONE);
                mBindingView.includeSupplementToBoxHeader.setData(data);
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
                        || mBindingView.layoutSupplementToBoxUpperCode.getData().codeStatus != CodeBean.STATUS_CODE_SUCCESS) {
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
        int index = mAdapter.getDataList().indexOf(bean);
        if (index == -1) {
            return;
        }
        if (status == CodeBean.STATUS_CODE_SUCCESS) {
            mAdapter.getDataList().get(index).codeStatus = CodeBean.STATUS_CODE_SUCCESS;
            mAdapter.notifyItemChanged(index);
        } else {
            mAdapter.notifyRemoveItem(bean);
            calculationTotal();
        }
    }

    public void showUploadDialog(String data) {
        JsonObject jsonObject = JsonUtils.parseObject(data);
        int successTotal = jsonObject.getInt("successTotal");
        int failTotal = jsonObject.getInt("failTotal");
        String msg = "您成功上传成功" + successTotal +
                "条数据.\n失败" + failTotal + "条数据";
        CommonDialogUtil.showConfirmDialog(this,"提示", msg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
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
            Intent intent = new Intent(context, ProduceSupplementToBoxActivity.class);
            context.startActivity(intent);
        }
    }
}
