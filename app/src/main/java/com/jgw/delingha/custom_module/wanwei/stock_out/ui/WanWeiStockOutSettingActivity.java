package com.jgw.delingha.custom_module.wanwei.stock_out.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.custom_module.wanwei.stock_out.viewmodel.WanWeiStockOutSettingViewModel;
import com.jgw.delingha.databinding.ActivityWanweiStockOutSettingBinding;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.module.select_list.logistics_company.LogisticsCompanyListActivity;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;

/**
 * Created by xsw
 * on 2020/2/28
 * 直接出库设置界面
 */
public class WanWeiStockOutSettingActivity extends BaseActivity<WanWeiStockOutSettingViewModel, ActivityWanweiStockOutSettingBinding> {

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
            case 1:
                //选择收货人
                long customer_id = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCustomerInfo(customer_id);
                break;
            case 2:
                // 产品名称
                long id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
            case 3:
                // 产品批次
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatchInfo(id);
                break;
            case 4:
                //选择物流公司
                long logistics_company_id = data.getLongExtra(LogisticsCompanyListActivity.EXTRA_NAME, -1);
                mViewModel.getLogisticsCompanyInfo(logistics_company_id);
                break;
            case 5:
                //选择物流公司
                mViewModel.setPlanNumber(null);
                mViewModel.getConfigEntity().setId(0);
                mBindingView.etWanweiStockOutSettingPlanNumber.setText(null);
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
                    WanWeiStockOutPDAActivity.start(WanWeiStockOutSettingActivity.this, id,5);
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
                        WanWeiStockOutWaitUploadListActivity.start(WanWeiStockOutSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvWanweiStockOutSettingReceiver)
                .addView(mBindingView.tvWanweiStockOutSettingProductName)
                .addView(mBindingView.tvWanweiStockOutSettingProductBatch)
                .addView(mBindingView.tvWanweiStockOutSettingNext)
                .submit();
        mBindingView.etWanweiStockOutSettingPlanNumber.addTextChangedListener(new TextWatcher() {
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
        if (id == R.id.tv_wanwei_stock_out_setting_receiver) {
            CustomerListActivity.start(1, this, CustomerListActivity.TYPE_UNDERLING);
        } else if (id == R.id.tv_wanwei_stock_out_setting_product_name) {
            ProductSelectListActivity.start(2, this);
        } else if (id == R.id.tv_wanwei_stock_out_setting_product_batch) {
            if (TextUtils.isEmpty(mViewModel.getProductId())) {
                ToastUtils.showToast("请先选择产品");
                return;
            }
            ProductBatchSelectListActivity.start(mViewModel.getProductId(), 3, this);
        }  else if (id == R.id.tv_wanwei_stock_out_setting_next) {
            next();
        }
    }

    private void next() {
        String receiver = mBindingView.tvWanweiStockOutSettingReceiver.getText().toString().trim();
        if (TextUtils.isEmpty(receiver)) {
            ToastUtils.showToast("请选择收货客户");
            return;
        }
        String productName = mBindingView.tvWanweiStockOutSettingProductName.getText().toString().trim();
        if (TextUtils.isEmpty(productName)) {
            ToastUtils.showToast("请选择产品");
            return;
        }
        String planNumber = mBindingView.etWanweiStockOutSettingPlanNumber.getText().toString().trim();
        if (TextUtils.isEmpty(planNumber)) {
            ToastUtils.showToast("请输入计划数量");
            return;
        }
        ConfigurationEntity data = mBindingView.getData();
        mViewModel.saveConfig(data);
    }

    public boolean inputIsEmpty() {
        String receiver = mBindingView.tvWanweiStockOutSettingReceiver.getText().toString();
        String productName = mBindingView.tvWanweiStockOutSettingProductName.getText().toString();
        String productBatch = mBindingView.tvWanweiStockOutSettingProductBatch.getText().toString();
        String planNumber = mBindingView.etWanweiStockOutSettingPlanNumber.getText().toString();
        String remark = mBindingView.etWanweiStockOutSettingRemark.getText().toString();
        return TextUtils.isEmpty(receiver)
                && TextUtils.isEmpty(productName)
                && TextUtils.isEmpty(productBatch)
                && TextUtils.isEmpty(planNumber)
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
            Intent intent = new Intent(context, WanWeiStockOutSettingActivity.class);
            context.startActivity(intent);
        }
    }
}
