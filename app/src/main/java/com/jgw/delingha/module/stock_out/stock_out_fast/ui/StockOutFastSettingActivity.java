package com.jgw.delingha.module.stock_out.stock_out_fast.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityStockOutFastSettingBinding;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.module.select_list.logistics_company.LogisticsCompanyListActivity;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.module.stock_out.stock_out_fast.viewmodel.StockOutFastSettingViewModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by xsw
 * on 2020/2/28
 * 直接出库设置界面
 */
public class StockOutFastSettingActivity extends BaseActivity<StockOutFastSettingViewModel, ActivityStockOutFastSettingBinding> {

    /**
     * 收货人
     */
    public static final int REQUEST_CODE_CONSIGNEE = 101;
    /**
     * 产品名称
     */
    public static final int REQUEST_CODE_PRODUCT_NAME = 102;
    /**
     * 产品批次
     */
    public static final int REQUEST_CODE_PRODUCT_BATCH = 103;
    /**
     * 选择物流公司
     */
    public static final int REQUEST_CODE_LOGISTICS_COMPANY = 104;
    /**
     * 设置
     */
    public static final int REQUEST_CODE_SETTING = 105;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("出库设置");
        mViewModel.hasWaitUpload();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CONSIGNEE:
                //选择收货人
                long customer_id = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCustomerInfo(customer_id);
                break;
            case REQUEST_CODE_PRODUCT_NAME:
                // 产品名称
                long id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
            case REQUEST_CODE_PRODUCT_BATCH:
                // 产品批次
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatchInfo(id);
                break;
            case REQUEST_CODE_LOGISTICS_COMPANY:
                //选择物流公司
                long logistics_company_id = data.getLongExtra(LogisticsCompanyListActivity.EXTRA_NAME, -1);
                mViewModel.getLogisticsCompanyInfo(logistics_company_id);
                break;
            case REQUEST_CODE_SETTING:
                //选择物流公司
                mViewModel.setPlanNumber(null);
                mViewModel.getConfigEntity().setId(0);
                mBindingView.etStockOutFastSettingPlanNumber.setText(null);
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
        mViewModel.getProductInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductInfoEntity entity = resource.getData();
                    mViewModel.setProductInfo(entity);
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getProductBatchInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductBatchEntity entity = resource.getData();
                    mViewModel.setProductBatchInfo(entity);
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
                    StockOutFastPDAActivity.start(StockOutFastSettingActivity.this, id, REQUEST_CODE_SETTING);
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
                        StockOutFastWaitUploadListActivity.start(StockOutFastSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvStockOutFastSettingReceiver)
                .addView(mBindingView.tvStockOutFastSettingProductName)
                .addView(mBindingView.tvStockOutFastSettingProductBatch)
                .addView(mBindingView.tvStockOutFastSettingLogisticsCompany)
                .addView(mBindingView.tvStockOutFastSettingNext)
                .submit();
        mBindingView.etStockOutFastSettingPlanNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                int planNumber = Integer.parseInt(text);
                mViewModel.setPlanNumber(planNumber);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.tv_stock_out_fast_setting_receiver) {
            CustomerListActivity.start(REQUEST_CODE_CONSIGNEE, this, CustomerListActivity.TYPE_UNDERLING);
        } else if (id == R.id.tv_stock_out_fast_setting_product_name) {
            ProductSelectListActivity.start(REQUEST_CODE_PRODUCT_NAME, this);
        } else if (id == R.id.tv_stock_out_fast_setting_product_batch) {
            if (TextUtils.isEmpty(mViewModel.getProductId())) {
                ToastUtils.showToast("请先选择产品");
                return;
            }
            ProductBatchSelectListActivity.start(mViewModel.getProductId(), REQUEST_CODE_PRODUCT_BATCH, this);
        } else if (id == R.id.tv_stock_out_fast_setting_logistics_company) {
            LogisticsCompanyListActivity.start(REQUEST_CODE_LOGISTICS_COMPANY, this);
        } else if (id == R.id.tv_stock_out_fast_setting_next) {
            next();
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;
        mBindingView.etStockOutFastSettingCode.setText(scanCode);
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

    private void next() {
        String receiver = mBindingView.tvStockOutFastSettingReceiver.getText().toString().trim();
        if (TextUtils.isEmpty(receiver)) {
            ToastUtils.showToast("请选择收货客户");
            return;
        }
        String productName = mBindingView.tvStockOutFastSettingProductName.getText().toString().trim();
        if (TextUtils.isEmpty(productName)) {
            ToastUtils.showToast("请选择产品");
            return;
        }
        String planNumber = mBindingView.etStockOutFastSettingPlanNumber.getText().toString().trim();
        if (TextUtils.isEmpty(planNumber)) {
            ToastUtils.showToast("请输入计划数量");
            return;
        }
        ConfigurationEntity data = mBindingView.getData();
        mViewModel.saveConfig(data);
    }

    public boolean inputIsEmpty() {
        String receiver = mBindingView.tvStockOutFastSettingReceiver.getText().toString();
        String productName = mBindingView.tvStockOutFastSettingProductName.getText().toString();
        String productBatch = mBindingView.tvStockOutFastSettingProductBatch.getText().toString();
        String logisticsCompany = mBindingView.tvStockOutFastSettingLogisticsCompany.getText().toString();
        String code = mBindingView.etStockOutFastSettingCode.getText().toString();
        String remark = mBindingView.etStockOutFastSettingRemark.getText().toString();
        return TextUtils.isEmpty(receiver)
                && TextUtils.isEmpty(productName)
                && TextUtils.isEmpty(productBatch)
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
            Intent intent = new Intent(context, StockOutFastSettingActivity.class);
            context.startActivity(intent);
        }
    }
}
