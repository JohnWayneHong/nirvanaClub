package com.jgw.delingha.module.stock_return.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityStockReturnSettingBinding;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.module.select_list.store_place_list.StorePlaceSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.module.stock_return.viewmodel.StockReturnSettingViewModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.utils.ConstantUtil;

/**
 * 退货
 */
public class StockReturnSettingActivity extends BaseActivity<StockReturnSettingViewModel, ActivityStockReturnSettingBinding> {

    private static final int REQUEST_CODE_CUSTOMER = 101;
    private static final int REQUEST_CODE_WAREHOUSE = 102;
    private static final int REQUEST_CODE_PLACE = 103;
    private boolean isEdit;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_return_setting_title));
    }

    @Override
    protected void initData() {
        long id = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        isEdit = id != -1;
        if (isEdit) {
            mViewModel.setConfigId(id);
        } else {
            mViewModel.hasWaitUpload();
        }
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
            }
        });

        mViewModel.getConfigLiveData().observe(this, configurationEntityResource -> {
            switch (configurationEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity entity = configurationEntityResource.getData();
                    mViewModel.setConfigInfo(entity);
                    mBindingView.setData(mViewModel.getConfigInfo());
                    break;
                case Resource.ERROR:
                    if (configurationEntityResource.getData() == null) {
                        return;
                    }
                    break;
            }
        });

        mViewModel.getCustomerInfoLiveData().observe(this, customerEntityResource -> {
            switch (customerEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    CustomerEntity entity = customerEntityResource.getData();
                    mViewModel.setCustomerData(entity);
                    mBindingView.setData(mViewModel.getConfigInfo());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(customerEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getWareHouseInfoLiveData().observe(this, wareHouseEntityResource -> {
            switch (wareHouseEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    WareHouseEntity entity = wareHouseEntityResource.getData();
                    mViewModel.setWareHouseData(entity);
                    mBindingView.setData(mViewModel.getConfigInfo());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(wareHouseEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getStorePlaceInfoLiveData().observe(this, storePlaceEntityResource -> {
            switch (storePlaceEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    StorePlaceEntity entity = storePlaceEntityResource.getData();
                    mViewModel.setStorePlaceData(entity);
                    mBindingView.setData(mViewModel.getConfigInfo());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(storePlaceEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getUpdateConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = resource.getData();
                    Intent intent = new Intent();
                    //修改配置表中的信息
                    intent.putExtra(ConstantUtil.CONFIG_ID, id);
                    setResult(RESULT_OK, intent);
                    finish();
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
                    //传入配置表的id
                    StockReturnPDAActivity.start(StockReturnSettingActivity.this, id);
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
                        StockReturnWaitUploadListActivity.start(StockReturnSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvReturnSettingNext,
                        mBindingView.tvStockReturnCustomerContent,
                        mBindingView.tvStockReturnReceivingWarehouseContent,
                        mBindingView.tvStockReturnStoreHouseContent)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_return_setting_next) {//下一步
            trySubmit();
        } else if (id == R.id.tv_stock_return_customer_content) {//退货客户
            CustomerListActivity.start(REQUEST_CODE_CUSTOMER, this, CustomerListActivity.TYPE_ADMIN_ALL);
        } else if (id == R.id.tv_stock_return_receiving_warehouse_content) {//收货仓库
            WareHouseSelectListActivity.start(REQUEST_CODE_WAREHOUSE, this);
        } else if (id == R.id.tv_stock_return_store_house_content) {//库位列表
            String trim = mBindingView.tvStockReturnReceivingWarehouseContent.getText().toString();
            if (TextUtils.isEmpty(trim)) {
                ToastUtils.showToast("请先选择仓库");
                return;
            }
            StorePlaceSelectListActivity.start(mViewModel.getConfigInfo().getWareHouseId(), REQUEST_CODE_PLACE, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (-1 != resultCode || data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CUSTOMER:
                long customerId = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCustomerInfoData(customerId);
                break;
            case REQUEST_CODE_WAREHOUSE:
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouseData(warehouseId);
                break;
            case REQUEST_CODE_PLACE:
                long storePlaceId = data.getLongExtra(StorePlaceSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getStorePlaceData(storePlaceId);
                break;
        }
    }

    /**
     * 跳转退货pda界面
     */
    public void trySubmit() {
        if (TextUtils.isEmpty(mBindingView.tvStockReturnReceivingWarehouseContent.getText().toString())) {
            ToastUtils.showToast("收货仓库不能为空");
            return;
        }
        if (isEdit) {
            mViewModel.updateConfig(mViewModel.getConfigInfo());
        } else {
            //得到数据库储存后的id
            mViewModel.saveConfigInfo(mViewModel.getConfigInfo());
        }
    }

    /**
     * 按返回键时弹窗
     */
    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mBindingView.tvStockReturnReceivingWarehouseContent.getText())
                && TextUtils.isEmpty(mBindingView.tvStockReturnCustomerContent.getText())) {
            super.onBackPressed();
        } else {
            CommonDialogUtil.showSelectDialog(this,"是否放弃对退货进行设置?", "", "否", "是", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    StockReturnSettingActivity.super.onBackPressed();
                }
            });
        }
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            context.startActivity(new Intent(context, StockReturnSettingActivity.class));
        }
    }

    public static void start(Activity context, Long id, int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockReturnSettingActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, id);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
