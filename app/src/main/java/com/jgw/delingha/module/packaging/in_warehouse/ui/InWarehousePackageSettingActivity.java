package com.jgw.delingha.module.packaging.in_warehouse.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.SelectDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.databinding.ActivityInWarehousePackageSettingBinding;
import com.jgw.delingha.module.packaging.in_warehouse.viewmodel.InWarehousePackageSettingViewModel;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_packaging_list.ProductPackagingSelectListActivity;
import com.jgw.delingha.module.select_list.store_place_list.StorePlaceSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

import java.util.List;

/**
 * author : xsw
 * data : 2020/09/21
 * description : 在库关联设置界面
 */
public class InWarehousePackageSettingActivity extends BaseActivity<InWarehousePackageSettingViewModel, ActivityInWarehousePackageSettingBinding> {

    private static final int TYPE_PRODUCT_NAME = 101;
    private static final int TYPE_PRODUCT_BATCH_NAME = 102;
    private static final int TYPE_WAREHOUSE = 103;
    private static final int TYPE_STORE_HOUSE = 104;

    @Override
    protected void initView() {
        setTitle("设置");
    }

    @Override
    protected void initData() {
        mViewModel.checkWaitUpload();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        long id;
        switch (requestCode) {
            case TYPE_PRODUCT_NAME:
                // 产品名称
                id = data.getLongExtra(ProductPackagingSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
            case TYPE_PRODUCT_BATCH_NAME:
                // 产品批次
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatchInfo(id);
                break;
            case TYPE_WAREHOUSE:
                // 仓库
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouse(warehouseId);
                break;
            case TYPE_STORE_HOUSE:
                // 库位
                long storePlaceId = data.getLongExtra(StorePlaceSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getStorePlace(storePlaceId);
                break;
        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getCheckWaitUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (resource.getData().isEmpty()) {
                        return;
                    }
                    showWaitUploadDialog();
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
                    mViewModel.getProductPackageInfo(entity.getProductId());
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getProductPackageInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> list = resource.getData();
                    if (list.isEmpty()) {
                        ToastUtils.showToast("请求产品包装关联级别失败");
                        return;
                    }
                    mViewModel.setProductPackageInfoList(list);
                    setProductPackageInfo(list.get(0));
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
                    mViewModel.setProductBatchInfo(resource.getData());
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getWareHouseInfoLiveData().observe(this, wareHouseEntityResource -> {
            switch (wareHouseEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    WareHouseEntity entity = wareHouseEntityResource.getData();
                    if (entity == null) {
                        ToastUtils.showToast("仓库信息获取失败");
                        return;
                    }
                    mViewModel.setWareHouse(entity);
                    mBindingView.setData(mViewModel.getConfigEntity());
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
                    if (entity == null) {
                        ToastUtils.showToast("库位信息获取失败");
                        return;
                    }
                    mViewModel.setStorePlace(entity);
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(storePlaceEntityResource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getSaveConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    Long configId = resource.getData();
                    InWarehousePackagePDAActivity.start(InWarehousePackageSettingActivity.this, configId);
                    finish();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    private void setProductPackageInfo(ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean) {
        if (TextUtils.isEmpty(bean.lastNumberCode) || TextUtils.isEmpty(bean.firstNumberCode)) {
            ToastUtils.showToast("该产品的包装规格缺少必要字段，请检查该产品");
            return;
        }
        mViewModel.setProductPackageInfo(bean);
        mBindingView.setData(mViewModel.getConfigEntity());
    }

    private void showWaitUploadDialog() {
        CommonDialogUtil.showSelectDialog(this,"您有待执行任务未提交", "是否前往待执行页面",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        InWarehousePackageWaitUploadListActivity.start(InWarehousePackageSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvInWarehousePackageSettingAssociationLevel
                        , mBindingView.tvInWarehousePackageSettingProductBatch
                        , mBindingView.tvInWarehousePackageSettingProduct
                        , mBindingView.llInWarehousePackageSettingSelectSpec
                        , mBindingView.tvInWarehousePackageSettingWarehouse
                        , mBindingView.tvInWarehousePackageSettingStoreHouse
                        , mBindingView.tvInWarehousePackageSettingNext)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_in_warehouse_package_setting_product) {// 产品名称选择
            ProductPackagingSelectListActivity.start(TYPE_PRODUCT_NAME, this, true);
        } else if (id == R.id.tv_in_warehouse_package_setting_product_batch) {// 产品批次选择
            onProductBatchClick();
        } else if (id == R.id.tv_in_warehouse_package_setting_association_level) {//关联级别
            onAssociationLevelClick();
        } else if (id == R.id.ll_in_warehouse_package_setting_select_spec) {
            updatePackagingImage();
        } else if (id == R.id.tv_in_warehouse_package_setting_warehouse) {
            WareHouseSelectListActivity.start(TYPE_WAREHOUSE, this);
        } else if (id == R.id.tv_in_warehouse_package_setting_store_house) {
            String wareHouse = mBindingView.tvInWarehousePackageSettingWarehouse.getText().toString().trim();
            if (TextUtils.isEmpty(wareHouse)) {
                ToastUtils.showToast("请先选择收货仓库");
                return;
            }
            StorePlaceSelectListActivity.start(mViewModel.getWareHouseId(), TYPE_STORE_HOUSE, this);
        } else if (id == R.id.tv_in_warehouse_package_setting_next) {
            onNextClick();
        }
    }

    /**
     * 更改是否零箱包装
     */
    public void updatePackagingImage() {
        if (!mViewModel.hasSelectProduct()) {
            ToastUtils.showToast("请先选择产品");
            return;
        }
        boolean enabled = mBindingView.etInWarehousePackageSettingSpec.isEnabled();
        mBindingView.etInWarehousePackageSettingSpec.setEnabled(!enabled);

        boolean selected = mBindingView.ivInWarehousePackageSettingSelectSpec.isSelected();
        mBindingView.ivInWarehousePackageSettingSelectSpec.setSelected(!selected);
        Drawable drawable = !selected ? ResourcesUtils.getDrawable(R.drawable.item_edittext_background) : null;
        mBindingView.etInWarehousePackageSettingSpec.setBackground(drawable);

        if (selected) {
            String productPackageNumber = mViewModel.getProductPackageNumber() + "";
            mBindingView.etInWarehousePackageSettingSpec.setText(productPackageNumber);
        }
    }

    private void onNextClick() {
        if (checkEmpty()) {
            return;
        }
        PackageConfigEntity entity = mBindingView.getData();
        mViewModel.saveConfig(entity);
    }

    /**
     * 点击下一步时检查必填项
     */
    public boolean checkEmpty() {
        if (TextUtils.isEmpty(mBindingView.tvInWarehousePackageSettingProduct.getText())) {
            ToastUtils.showToast("请选择产品名称");
            return true;
        }

        if (TextUtils.isEmpty(mBindingView.tvInWarehousePackageSettingAssociationLevel.getText())) {
            ToastUtils.showToast("请选择关联级别");
            return true;
        }

        if (TextUtils.isEmpty(mBindingView.etInWarehousePackageSettingSpec.getText().toString()) || Integer.parseInt(mBindingView.etInWarehousePackageSettingSpec.getText().toString()) == 0) {
            ToastUtils.showToast("包装规格不能为空");
            return true;
        }

        int productPackageNumber = mViewModel.getProductPackageNumber();
        if (productPackageNumber < Integer.parseInt(mBindingView.etInWarehousePackageSettingSpec.getText().toString())) {
            ToastUtils.showToast("包装规格不能大于最大值 " + productPackageNumber);
            return true;
        }

        if (TextUtils.isEmpty(mBindingView.tvInWarehousePackageSettingWarehouse.getText())) {
            ToastUtils.showToast("请选择仓库");
            return true;
        }
        return false;
    }

    private void onProductBatchClick() {
        if (!mViewModel.hasSelectProduct()) {
            ToastUtils.showToast("请先选择产品");
            return;
        }
        ProductBatchSelectListActivity.start(mViewModel.getProductId(), TYPE_PRODUCT_BATCH_NAME, this);
    }

    private void onAssociationLevelClick() {
        if (!mViewModel.hasSelectProduct()) {
            ToastUtils.showToast("请先选择产品");
            return;
        }
        if (!mViewModel.hasProductAssociationLevel()) {
            ToastUtils.showToast("该产品暂无关联级别");
            return;
        }
        SelectDialogUtil.showSelectDialog(this, mViewModel.getProductAssociationLevelList(), (view1, position, string, dialog) -> {
            ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean = mViewModel.getProductPackageSelected(position);
            setProductPackageInfo(bean);
        });
    }


    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, InWarehousePackageSettingActivity.class);
            context.startActivity(intent);
        }
    }
}
