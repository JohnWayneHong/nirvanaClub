package com.jgw.delingha.module.stock_in.base.ui;

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
import com.jgw.delingha.databinding.ActivityStockInSettingBinding;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.module.select_list.store_place_list.StorePlaceSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.module.stock_in.base.viewmodel.StockInSettingViewModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

public class StockInSettingActivity extends BaseActivity<StockInSettingViewModel, ActivityStockInSettingBinding> {

    private static final int TYPE_PRODUCT_NAME = 101;
    private static final int TYPE_PRODUCT_BATCH_NAME = 102;
    private static final int TYPE_WARE_HOUSE_NAME = 103;
    private static final int TYPE_STORE_PLACE_NAME = 104;
    private boolean isEdit;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_in_setting_title));
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        long sqlId = intent.getLongExtra("sqlId", -1);
        isEdit = sqlId != -1;
        if (isEdit) {
            mViewModel.getGetConfigData(sqlId);
//            mBindingView.tvStockInSettingProduct.setEnabled(false);
//            mBindingView.tvStockInSettingProductBatch.setEnabled(false);
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
                    if (entity == null) {
                        ToastUtils.showToast("获取配置信息错误");
                        return;
                    }
                    mViewModel.setConfigInfo(entity);
                    mBindingView.setData(entity);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(configurationEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getProductInfoLiveData().observe(this, productInfoEntityResource -> {
            switch (productInfoEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductInfoEntity entity = productInfoEntityResource.getData();
                    mViewModel.setProductMessage(entity);
                    mBindingView.setData(mViewModel.getNowConfigData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("获取商品信息失败");
                    break;
            }
        });

        mViewModel.getProductBatchInfoLiveData().observe(this, productBatchEntityResource -> {
            switch (productBatchEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductBatchEntity entity = productBatchEntityResource.getData();
                    mViewModel.setProductBatchMessage(entity);
                    mBindingView.setData(mViewModel.getNowConfigData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("获取商品批次信息失败");
                    break;
            }
        });
        mViewModel.getWareHouseInfoLiveData().observe(this, wareHouseEntityResource -> {
            switch (wareHouseEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    WareHouseEntity entity = wareHouseEntityResource.getData();
                    mViewModel.setWareHouseMessage(entity);
                    mBindingView.setData(mViewModel.getNowConfigData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("仓库获取失败");
                    break;
            }
        });

        mViewModel.getStorePlaceInfoLiveData().observe(this, storePlaceEntityResource -> {
            switch (storePlaceEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    StorePlaceEntity entity = storePlaceEntityResource.getData();
                    mViewModel.setStorePlaceMessage(entity);
                    mBindingView.setData(mViewModel.getNowConfigData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("库位获取失败");
                    break;
            }
        });

        mViewModel.getUpdateConfigLiveData().observe(this, longResource -> {
            switch (longResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = longResource.getData();
                    Intent intent = new Intent();
                    intent.putExtra("sqlId", id);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(longResource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getSaveConfigLiveData().observe(this, longResource -> {
            switch (longResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = longResource.getData();
                    if (isEdit) {
                        Intent intent = new Intent();
                        intent.putExtra("sqlId", id);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        StockInPDAActivity.start(StockInSettingActivity.this, id);
                    }
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(longResource.getErrorMsg());
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
                        StockInWaitUploadListActivity.start(StockInSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvStockInSettingNext, mBindingView.tvStockInSettingWareHouse,
                        mBindingView.tvStockInSettingProduct, mBindingView.tvStockInSettingProductBatch,
                        mBindingView.tvStockInSettingStorePlace)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_stock_in_setting_product) {// 产品名称选择
            ProductSelectListActivity.start(TYPE_PRODUCT_NAME, this);
        } else if (id == R.id.tv_stock_in_setting_product_batch) {// 产品批次选择
            if (checkProductEmpty()) {
                return;
            }
            ProductBatchSelectListActivity.start(mViewModel.getNowConfigData().getProductId(), TYPE_PRODUCT_BATCH_NAME, this);
        } else if (id == R.id.tv_stock_in_setting_ware_house) {// 仓库名称选择
            WareHouseSelectListActivity.start(TYPE_WARE_HOUSE_NAME, this);
        } else if (id == R.id.tv_stock_in_setting_store_place) {// 库位名称选择
            if (checkWareHouseEmpty()) {
                return;
            }
            StorePlaceSelectListActivity.start(mViewModel.getNowConfigData().getWareHouseId(), TYPE_STORE_PLACE_NAME, this);
        } else if (id == R.id.tv_stock_in_setting_next) {
            trySubmit();
        }
    }

    private Boolean checkProductEmpty() {
        if (TextUtils.isEmpty(mBindingView.tvStockInSettingProduct.getText().toString().trim())) {
            ToastUtils.showToast("请选择产品名称");
            return true;
        }
        return false;
    }

    private Boolean checkWareHouseEmpty() {
        if (TextUtils.isEmpty(mBindingView.tvStockInSettingWareHouse.getText().toString().trim())) {
            ToastUtils.showToast("请选择仓库");
            return true;
        }
        return false;
    }

    private void trySubmit() {
        if (checkProductEmpty() || checkWareHouseEmpty()) {
            return;
        }
        ConfigurationEntity date = mBindingView.getData();
        if (isEdit) {
            mViewModel.updateConfig(date);
        } else {
            mViewModel.getSaveConfigData(date);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case TYPE_PRODUCT_NAME:
                // 产品名称
                long id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfoData(id);
                break;
            case TYPE_PRODUCT_BATCH_NAME:
                // 产品批次
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatchData(id);
                break;
            case TYPE_WARE_HOUSE_NAME:
                // 仓库名称
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouseData(warehouseId);
                break;
            case TYPE_STORE_PLACE_NAME:
                // 库位名称
                long storePlaceId = data.getLongExtra(StorePlaceSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getStoreHouseData(storePlaceId);
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mBindingView.tvStockInSettingProduct.getText().toString().trim())
                && TextUtils.isEmpty(mBindingView.tvStockInSettingWareHouse.getText().toString().trim())
                && TextUtils.isEmpty(mBindingView.etStockInSettingRemark.getText().toString().trim())) {
            super.onBackPressed();
        } else {
            CommonDialogUtil.showSelectDialog(this,"是否放弃对入库进行设置?", "", "否", "是", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    StockInSettingActivity.super.onBackPressed();
                }
            });
        }
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            context.startActivity(new Intent(context, StockInSettingActivity.class));
        }
    }

    public static void start(Activity context, long sqlId, int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockInSettingActivity.class);
            intent.putExtra("sqlId", sqlId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
