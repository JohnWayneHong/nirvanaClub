package com.jgw.delingha.module.packaging.stock_in_packaged.ui;

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
import com.jgw.delingha.databinding.ActivityStockInPackagedSettingBinding;
import com.jgw.delingha.module.packaging.stock_in_packaged.viewmodel.StockInPackagedSettingViewModel;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.module.select_list.store_place_list.StorePlaceSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

public class StockInPackagedSettingActivity extends BaseActivity<StockInPackagedSettingViewModel, ActivityStockInPackagedSettingBinding> {

    private static final int TYPE_PRODUCT_NAME = 101;
    private static final int TYPE_WARE_HOUSE_NAME = 103;
    private static final int TYPE_STORE_PLACE_NAME = 104;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_in_packaged_setting_title));
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
            }
        });

        mViewModel.getProductInfoLiveData().observe(this, productInfoEntityResource -> {
            switch (productInfoEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductInfoEntity entity = productInfoEntityResource.getData();
                    mViewModel.setProductInfo(entity);
                    mBindingView.setData(mViewModel.getConfigInfo());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(productInfoEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getWareHouseInfoLiveData().observe(this, wareHouseEntityResource -> {
            switch (wareHouseEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    WareHouseEntity entity = wareHouseEntityResource.getData();
                    mViewModel.setWareHouseInfo(entity);
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
                    mViewModel.setStorePlaceInfo(entity);
                    mBindingView.setData(mViewModel.getConfigInfo());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(storePlaceEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getSaveConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = resource.getData();
                    StockInPackagedPDAActivity.start(StockInPackagedSettingActivity.this, id);
                    finish();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });
    }

    private void showWaitUploadDialog() {
        CommonDialogUtil.showSelectDialog(this,"您有待执行任务未提交", "是否前往待执行页面",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        StockInPackagedWaitUploadListActivity.start(StockInPackagedSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvNextStep, mBindingView.tvStoreName,
                        mBindingView.tvProductName, mBindingView.tvStorePlaceName)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_product_name) {// 产品名称选择
            ProductSelectListActivity.start(TYPE_PRODUCT_NAME, this);
        } else if (id == R.id.tv_store_name) {// 仓库名称选择
            WareHouseSelectListActivity.start(TYPE_WARE_HOUSE_NAME, this);
        } else if (id == R.id.tv_store_place_name) {// 库位名称选择
            if (TextUtils.isEmpty(mViewModel.getConfigInfo().getWareHouseId())) {
                ToastUtils.showToast("请先选择仓库名称");
                return;
            }
            StorePlaceSelectListActivity.start(mViewModel.getConfigInfo().getWareHouseId(), TYPE_STORE_PLACE_NAME, this);
        } else if (id == R.id.tv_next_step) {
            trySubmit();
        }
    }

    private void trySubmit() {
        if (TextUtils.isEmpty(mViewModel.getConfigInfo().getWareHouseId())) {
            ToastUtils.showToast("请选择仓库");
            return;
        }
        ConfigurationEntity data = mBindingView.getData();
        mViewModel.getSaveConfig(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TYPE_PRODUCT_NAME:
                if (data == null) {
                    return;
                }
                // 产品名称
                long id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
            case TYPE_WARE_HOUSE_NAME:
                if (data == null) {
                    return;
                }
                // 仓库名称
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouseInfo(warehouseId);
                break;
            case TYPE_STORE_PLACE_NAME:
                if (data == null) {
                    return;
                }
                // 库位名称
                long storePlaceId = data.getLongExtra(StorePlaceSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getStorePlaceInfo(storePlaceId);
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mBindingView.tvProductName.getText())
                && TextUtils.isEmpty(mBindingView.tvStoreName.getText())
                && TextUtils.isEmpty(mBindingView.etRemarks.getText().toString())) {
            super.onBackPressed();
        } else {
            CommonDialogUtil.showSelectDialog(this,"是否放弃对入库进行设置?", "", "否", "是", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    StockInPackagedSettingActivity.super.onBackPressed();
                }
            });
        }
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            context.startActivity(new Intent(context, StockInPackagedSettingActivity.class));
        }
    }
}
