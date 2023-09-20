package com.jgw.delingha.module.exchange_goods.base.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityExchangeGoodsSettingBinding;
import com.jgw.delingha.module.exchange_goods.base.viewmodel.ExchangeGoodsSettingViewModel;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.sql.entity.CustomerEntity;

public class ExchangeGoodsSettingActivity extends BaseActivity<ExchangeGoodsSettingViewModel, ActivityExchangeGoodsSettingBinding> {

    private static final int TYPE_CALL_IN_CUSTOMER = 101;
    private static final int TYPE_CALL_OUT_CUSTOMER = 102;

    @Override
    protected void initView() {
        setTitle("调货设置");
    }

    @Override
    protected void initData() {
        mViewModel.hasWaitUpload();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getWaitUploadLiveData().observe(this, booleanResource -> {
            switch (booleanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (booleanResource.getData()) {
                        showWaitUploadDialog();
                    }
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(booleanResource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getCallOutCustomerLivData().observe(this, customerEntityResource -> {
            switch (customerEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    CustomerEntity entity = customerEntityResource.getData();
                    mViewModel.setCallOutCustomerInfo(entity);
                    mBindingView.setData(mViewModel.getGoodsConfigurationEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(customerEntityResource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getCallInCustomerLivData().observe(this, customerEntityResource -> {
            switch (customerEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    CustomerEntity entity = customerEntityResource.getData();
                    mViewModel.setCallInCustomerInfo(entity);
                    mBindingView.setData(mViewModel.getGoodsConfigurationEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(customerEntityResource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getSaveConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = resource.getData();
                    ExchangeGoodsActivity.start(ExchangeGoodsSettingActivity.this, id);
                    finish();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });
    }

    public void showWaitUploadDialog() {
        CommonDialogUtil.showSelectDialog(this,"您有待执行任务未提交", "是否前往待执行页面",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        ExchangeGoodsWaitUploadActivity.start(ExchangeGoodsSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvEgInValue
                        , mBindingView.tvEgOutValue
                        , mBindingView.tvNextStep)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_eg_out_value) {// 调出客户选择
            CustomerListActivity.start(TYPE_CALL_OUT_CUSTOMER, this, CustomerListActivity.TYPE_UNDERLING);
        } else if (id == R.id.tv_eg_in_value) {//调入客户选择
            CustomerListActivity.start(TYPE_CALL_IN_CUSTOMER, this, CustomerListActivity.TYPE_UNDERLING);
        } else if (id == R.id.tv_next_step) {
            if (TextUtils.isEmpty(mBindingView.tvEgOutValue.getText().toString())) {
                ToastUtils.showToast("调出客户不能为空");
                return;
            }else if (TextUtils.isEmpty(mBindingView.tvEgInValue.getText().toString())) {
                ToastUtils.showToast("收货客户不能为空");
                return;
            }else if (TextUtils.equals(mBindingView.getData().getWareGoodsOutId(),mBindingView.getData().getWareGoodsInId())){
                ToastUtils.showToast("调出客户、收货客户不能相同");
                return;
            }
            mViewModel.getSaveConfigData(mViewModel.getGoodsConfigurationEntity());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        long customerId;
        switch (requestCode) {
            case TYPE_CALL_OUT_CUSTOMER:
                // 调出客户
                customerId = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCallOutCustomer(customerId);
                break;
            case TYPE_CALL_IN_CUSTOMER:
                // 调入客户
                customerId = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCallInCustomer(customerId);
                break;
            default:
        }
    }

    /**
     * 按返回键时弹窗
     */
    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mBindingView.tvEgInValue.getText())
                && TextUtils.isEmpty(mBindingView.tvEgOutValue.getText())) {
            super.onBackPressed();
        } else {
            CommonDialogUtil.showSelectDialog(this,"是否放弃对调货进行设置?", "", "否", "是", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    ExchangeGoodsSettingActivity.super.onBackPressed();
                }
            });
        }
    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ExchangeGoodsSettingActivity.class);
            context.startActivity(intent);
        }
    }

}
