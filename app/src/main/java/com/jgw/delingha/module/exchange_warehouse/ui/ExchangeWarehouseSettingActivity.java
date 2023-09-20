package com.jgw.delingha.module.exchange_warehouse.ui;

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
import com.jgw.delingha.databinding.ActivityExchangeWarehouseSettingBinding;
import com.jgw.delingha.module.exchange_warehouse.viewmodel.ExchangeWarehouseSettingViewModel;
import com.jgw.delingha.module.select_list.store_place_list.StorePlaceSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

public class ExchangeWarehouseSettingActivity extends BaseActivity<ExchangeWarehouseSettingViewModel, ActivityExchangeWarehouseSettingBinding> {

    private static final int TYPE_CALL_IN_WAREHOUSE = 101;
    private static final int TYPE_CALL_OUT_WAREHOUSE = 102;
    private static final int TYPE_SAVE_WAREHOUSE = 103;

    @Override
    protected void initView() {
        setTitle("调仓设置");
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

        mViewModel.getCallOutWarehouseLivData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    WareHouseEntity entity = resource.getData();
                    mViewModel.setCallOutWarehouseInfo(entity);
                    mBindingView.setData(mViewModel.getWarehouseConfigurationEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getCallInWarehouseLivData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    WareHouseEntity entity = resource.getData();
                    mViewModel.setCallInWarehouseInfo(entity);
                    mBindingView.setData(mViewModel.getWarehouseConfigurationEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getStoreHouseLivData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    StorePlaceEntity entity = resource.getData();
                    mViewModel.setStoreHouseInfo(entity);
                    mBindingView.setData(mViewModel.getWarehouseConfigurationEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getSaveConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long id = resource.getData();
                    ExchangeWarehouseActivity.start(this, id);
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
                        ExchangeWarehouseWaitUploadActivity.start(ExchangeWarehouseSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvEwInValue
                        , mBindingView.tvEwOutValue
                        , mBindingView.tvEwSaveValue
                        , mBindingView.tvNextStep)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_ew_out_value) {// 调出仓库选择
            WareHouseSelectListActivity.start(TYPE_CALL_OUT_WAREHOUSE, this);
        } else if (id == R.id.tv_ew_in_value) {// 收货仓库选择
            WareHouseSelectListActivity.start(TYPE_CALL_IN_WAREHOUSE, this);
        } else if (id == R.id.tv_ew_save_value) {//存放库位选择
            if (TextUtils.isEmpty(mViewModel.getWarehouseConfigurationEntity().getInWareHouseId())) {
                ToastUtils.showToast("请先选择收货仓库");
                return;
            }
            StorePlaceSelectListActivity.start(mViewModel.getWarehouseConfigurationEntity().getInWareHouseId(), TYPE_SAVE_WAREHOUSE, this);
        } else if (id == R.id.tv_next_step) {
            if (TextUtils.isEmpty(mBindingView.tvEwOutValue.getText().toString())) {
                ToastUtils.showToast("调出仓库不能为空");
                return;
            } else if (TextUtils.isEmpty(mBindingView.tvEwInValue.getText().toString())) {
                ToastUtils.showToast("收货仓库不能为空");
                return;
            }else if (TextUtils.equals(mBindingView.getData().getInWareHouseId(),mBindingView.getData().getOutWareHouseId())){
                ToastUtils.showToast("调出仓库、收货仓库不能相同");
                return;
            }
            mViewModel.getSaveConfigData(mViewModel.getWarehouseConfigurationEntity());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case TYPE_CALL_OUT_WAREHOUSE:
                // 调出仓库
                long outWarehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getCallOutWarehouse(outWarehouseId);
                break;
            case TYPE_CALL_IN_WAREHOUSE:
                // 收货仓库
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getCallInWarehouse(warehouseId);
                break;
            case TYPE_SAVE_WAREHOUSE:
                // 存放库位
                long storePlaceId = data.getLongExtra(StorePlaceSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getStoreHouse(storePlaceId);
                break;
            default:
        }
    }

    /**
     * 按返回键时弹窗
     */
    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mBindingView.tvEwOutValue.getText())
                && TextUtils.isEmpty(mBindingView.tvEwInValue.getText())) {
            super.onBackPressed();
        } else {
            CommonDialogUtil.showSelectDialog(this,"是否放弃对调仓进行设置?", "", "否", "是", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    ExchangeWarehouseSettingActivity.super.onBackPressed();
                }
            });
        }
    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ExchangeWarehouseSettingActivity.class);
            context.startActivity(intent);
        }
    }

}
