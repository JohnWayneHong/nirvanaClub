package com.jgw.delingha.module.stock_in.package_stock_in.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.SelectDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.common_library.widget.selectDialog.SelectDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.databinding.ActivityPackageStockInSettingBinding;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_packaging_list.ProductPackagingSelectListActivity;
import com.jgw.delingha.module.select_list.store_place_list.StorePlaceSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.module.stock_in.package_stock_in.viewmodel.PackageStockInSettingViewModel;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

import java.util.List;

/**
 * author : xsw
 * data : 2020/4/27
 * description : 包装入库设置
 */
public class PackageStockInSettingActivity extends BaseActivity<PackageStockInSettingViewModel, ActivityPackageStockInSettingBinding> {

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
        mViewModel.hasWaitUpload();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getWaitUploadLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (listResource.getData().isEmpty()) {
                        return;
                    }
                    returnPending();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getProductInfoLiveData().observe(this, productInfoEntityResource -> {
            switch (productInfoEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductInfoEntity entity = productInfoEntityResource.getData();
                    mViewModel.setProductInfo(entity);
                    mViewModel.getProductWareMessage(entity.getProductId());
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(productInfoEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getProductWareInfoLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> list = listResource.getData();
                    if (list.isEmpty()) {
                        ToastUtils.showToast("请求产品包装关联级别失败");
                        return;
                    }
                    mViewModel.setProductWareMessageList(list);
                    setProductWareMessage(list.get(0));
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getProductBatchInfoLiveData().observe(this, productBatchEntityResource -> {
            switch (productBatchEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductBatchEntity entity = productBatchEntityResource.getData();
                    if (entity == null) {
                        ToastUtils.showToast("批次信息获取失败");
                        return;
                    }
                    mViewModel.setProductBatch(entity);
                    mBindingView.setData(mViewModel.getConfigEntity());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(productBatchEntityResource.getErrorMsg());
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

        mViewModel.getSaveConfigLiveData().observe(this, longResource -> {
            switch (longResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    Long configId = longResource.getData();
                    PackageStockInPDAActivity.start(this, configId);
                    finish();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(longResource.getErrorMsg());
                    break;
            }
        });
    }

    public void returnPending() {
        CommonDialogUtil.showSelectDialog(this,"您有待执行任务未提交", "是否前往待执行页面",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        PackageStockInWaitUploadActivity.start(PackageStockInSettingActivity.this);
                        finish();
                    }
                });
    }

    public void setProductWareMessage(ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean) {
        if (TextUtils.isEmpty(bean.lastNumberCode) || TextUtils.isEmpty(bean.firstNumberCode)) {
            ToastUtils.showToast("该产品的包装规格缺少必要字段，请检查该产品");
            return;
        }
        mViewModel.setAssociationLevelSpecifications(bean);
        mBindingView.setData(mViewModel.getConfigEntity());
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvPackageStockInSettingProduct)
                .addView(mBindingView.tvPackageStockInSettingProductBatch)
                .addView(mBindingView.tvPackageStockInSettingAssociationLevel)
                .addView(mBindingView.tvPackageStockInSettingAssociationLevel)
                .addView(mBindingView.llPackageStockInSettingSelectSpec)
                .addView(mBindingView.tvPackageStockInSettingWarehouse)
                .addView(mBindingView.tvPackageStockInSettingStoreHouse)
                .addView(mBindingView.tvPackageStockInSettingNext)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_package_stock_in_setting_product) {// 产品名称选择
            ProductPackagingSelectListActivity.start(TYPE_PRODUCT_NAME, this, true);
        } else if (id == R.id.tv_package_stock_in_setting_product_batch) {// 产品批次选择
            if (mViewModel.noSelectProduct()) {
                ToastUtils.showToast("请先选择产品");
                return;
            }
            ProductBatchSelectListActivity.start(mViewModel.getProductId(), TYPE_PRODUCT_BATCH_NAME, this);
        } else if (id == R.id.tv_package_stock_in_setting_association_level) {//关联级别
            if (mViewModel.noSelectProduct()) {
                ToastUtils.showToast("请先选择产品");
                return;
            }
            if (mViewModel.noAssociationLevel()) {
                ToastUtils.showToast("该产品暂无关联级别");
                return;
            }
            SelectDialogUtil.showSelectDialog(PackageStockInSettingActivity.this, mViewModel.getProductAssociationLevelList(), new SelectDialog.OnSelectDialogItemClickListener() {
                @Override
                public void onSingleItemClick(View view1, int position, String string, SelectDialog dialog) {
                    PackageStockInSettingActivity.this.setProductWareMessage(mViewModel.getProductPackageSelected(position));
                }
            });
        } else if (id == R.id.ll_package_stock_in_setting_select_spec) {
            updateProductPackageNumber();
        } else if (id == R.id.tv_package_stock_in_setting_warehouse) {
            WareHouseSelectListActivity.start(TYPE_WAREHOUSE, this);
        } else if (id == R.id.tv_package_stock_in_setting_store_house) {
            if (mViewModel.noWareHouse()) {
                ToastUtils.showToast("请先选择收货仓库");
                return;
            }
            StorePlaceSelectListActivity.start(mViewModel.getWareHouseId(), TYPE_STORE_HOUSE, this);
        } else if (id == R.id.tv_package_stock_in_setting_next) {
            if (checkEmpty()) {
                return;
            }
            PackageConfigEntity entity = mBindingView.getData();
            mViewModel.getSaveConfig(entity);
        }
    }

    /**
     * 更改零箱包装带来的更新
     */
    public void updateProductPackageNumber() {
        if (mViewModel.noSelectProduct()) {
            ToastUtils.showToast("请先选择产品");
            return;
        }
        boolean selected = mBindingView.ivPackageStockInSettingSelectSpec.isSelected();
        mBindingView.ivPackageStockInSettingSelectSpec.setSelected(!selected);
        mBindingView.etPackageStockInSettingSpec.setEnabled(!selected);
        if (selected) {
            //取消选中时
            mBindingView.etPackageStockInSettingSpec.setText(mViewModel.getProductPackageNumber());
        }
    }

    /**
     * 下一步之前检查必选是否为空
     */
    public boolean checkEmpty() {
        if (TextUtils.isEmpty(mBindingView.tvPackageStockInSettingProduct.getText())) {
            ToastUtils.showToast("请选择产品名称");
            return true;
        }

        if (TextUtils.isEmpty(mBindingView.tvPackageStockInSettingAssociationLevel.getText())) {
            ToastUtils.showToast("请选择关联级别");
            return true;
        }

        String spec = mBindingView.etPackageStockInSettingSpec.getText().toString();
        if (TextUtils.isEmpty(spec) || Integer.parseInt(spec) == 0) {
            ToastUtils.showToast("包装规格不能为空");
            return true;
        }

        int productPackageNumber = Integer.parseInt(mViewModel.getProductPackageNumber());
        if (productPackageNumber < Integer.parseInt(spec)) {
            ToastUtils.showToast("包装规格不能大于最大值 " + productPackageNumber);
            return true;
        }
        String warehouse = mBindingView.tvPackageStockInSettingWarehouse.getText().toString().trim();
        if (TextUtils.isEmpty(warehouse)) {
            ToastUtils.showToast("请选择仓库");
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        long id;
        switch (requestCode) {
            case TYPE_PRODUCT_NAME:
                // 产品名称
                if (data == null) {
                    return;
                }
                long package_product_id = data.getLongExtra(ProductPackagingSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(package_product_id);
                break;
            case TYPE_PRODUCT_BATCH_NAME:
                // 产品批次
                if (data == null) {
                    return;
                }
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatch(id);
                break;
            case TYPE_WAREHOUSE:
                // 仓库
                if (data == null) {
                    return;
                }
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouse(warehouseId);
                break;
            case TYPE_STORE_HOUSE:
                // 库位
                if (data == null) {
                    return;
                }
                long storePlaceId = data.getLongExtra(StorePlaceSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getStorePlace(storePlaceId);
                break;
        }
    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStockInSettingActivity.class);
            context.startActivity(intent);
        }
    }
}
