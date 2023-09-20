package com.jgw.delingha.module.stock_out.base.ui;

import android.app.Activity;
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
import com.jgw.delingha.databinding.ActivityStockOutSettingBinding;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.module.select_list.logistics_company.LogisticsCompanyListActivity;
import com.jgw.delingha.module.stock_out.base.viewmodel.StockOutSettingViewModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.utils.ConstantUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by XiongShaoWu
 * on 2019/11/25
 * 迁移版时为了减少数据传递改为同一Activity下Fragment
 */
public class StockOutSettingActivity extends BaseActivity<StockOutSettingViewModel, ActivityStockOutSettingBinding> {


    private boolean isEdit;

    public static final int REQUEST_CODE_CUSTOMER = 1;
    public static final int REQUEST_CODE_LOGISTICS_COMPANY = 2;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("出库设置");
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        isEdit = configId != -1;
        if (isEdit) {
            mViewModel.getConfigInfo(configId);
        } else {
            mViewModel.hasWaitUpload();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CUSTOMER:
                //选择收货人
                long customer_id = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCustomerInfo(customer_id);
                break;
            case REQUEST_CODE_LOGISTICS_COMPANY:
                //选择物流公司
                long logistics_company_id = data.getLongExtra(LogisticsCompanyListActivity.EXTRA_NAME, -1);
                mViewModel.getLogisticsCompanyInfo(logistics_company_id);
                break;
        }
    }

    @Override
    public void initLiveData() {
        mViewModel.getWaitUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (!resource.getData()) {
                        return;
                    }
                    showWaitUploadDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getConfigInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity entity = resource.getData();
                    mViewModel.setConfigInfo(entity);
                    mBindingView.setData(entity);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getCustomerInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    CustomerEntity entity = resource.getData();
                    mViewModel.setCustomerInfo(entity);
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getLogisticsCompanyInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    LogisticsCompanyEntity entity = resource.getData();
                    mViewModel.setLogisticsCompanyInfo(entity);
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getSaveConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = resource.getData();
                    StockOutPDAActivity.start(StockOutSettingActivity.this, id);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }

        });
        mViewModel.getUpdateConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = resource.getData();
                    Intent intent = new Intent();
                    intent.putExtra(ConstantUtil.CONFIG_ID, id);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }

        });
    }

    private void showWaitUploadDialog() {
        CommonDialogUtil.showSelectDialog(this,"您有待执行任务未提交", "是否前往待执行页面",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        StockOutWaitUploadListActivity.start(StockOutSettingActivity.this);
                        finish();
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

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;
        mBindingView.etStockOutSettingCode.setText(scanCode);
    }


    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvStockOutSettingNext)
                .addView(mBindingView.tvStockOutSettingReceiver)
                .addView(mBindingView.tvStockOutSettingLogisticsCompany)
                .submit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.tv_stock_out_setting_next) {
            next();
        } else if (id == R.id.tv_stock_out_setting_receiver) {
            CustomerListActivity.start(REQUEST_CODE_CUSTOMER, this, CustomerListActivity.TYPE_UNDERLING);
        } else if (id == R.id.tv_stock_out_setting_logistics_company) {
            LogisticsCompanyListActivity.start(REQUEST_CODE_LOGISTICS_COMPANY, this);
        }
    }

    private void next() {
        String receiver = mBindingView.tvStockOutSettingReceiver.getText().toString().trim();
        if (TextUtils.isEmpty(receiver)) {
            ToastUtils.showToast("请选择收货客户");
            return;
        }
        ConfigurationEntity data = mBindingView.getData();
        if (isEdit) {
            mViewModel.updateConfig(data);
        } else {
            mViewModel.saveConfig(data);
        }
    }

    /**
     * 检查是否未在本界面录入任何数据
     *
     * @return 是否未录入任何数据
     */
    public boolean inputIsEmpty() {
        String receiver = mBindingView.tvStockOutSettingReceiver.getText().toString();
        String logisticsCompany = mBindingView.tvStockOutSettingLogisticsCompany.getText().toString();
        String code = mBindingView.etStockOutSettingCode.getText().toString();
        String remark = mBindingView.etStockOutSettingRemark.getText().toString();
        return TextUtils.isEmpty(receiver)
                && TextUtils.isEmpty(logisticsCompany)
                && TextUtils.isEmpty(code)
                && TextUtils.isEmpty(remark);
    }

    @Override
    public void onBackPressed() {
        if (inputIsEmpty()) {
            finish();
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否放弃对出库进行设置?", "", "否", "是", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                finish();
            }
        });
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockOutSettingActivity.class);
            context.startActivity(intent);
        }
    }

    public static void start(Activity context, long configId, int requestCode) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockOutSettingActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
