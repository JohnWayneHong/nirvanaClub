package com.jgw.delingha.module.label_edit.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityLabelEditSettingBinding;
import com.jgw.delingha.module.label_edit.viewmodel.LabelEditSettingViewModel;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.utils.ConstantUtil;

/**
 * author : xsw
 * data : 2020/10/12
 * description : 标签纠错设置界面
 */
public class LabelEditSettingActivity extends BaseActivity<LabelEditSettingViewModel, ActivityLabelEditSettingBinding> {

    private static final int TYPE_PRODUCT_NAME = 101;
    private static final int TYPE_PRODUCT_BATCH_NAME = 102;

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
                id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
            case TYPE_PRODUCT_BATCH_NAME:
                // 产品批次
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatchInfo(id);
                break;
        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getCheckWaitUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (!resource.getData()) {
                        //无数据
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
        mViewModel.getSaveConfigLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    Long configId = resource.getData();
                    LabelEditPDAActivity.start(LabelEditSettingActivity.this, configId);
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
                        LabelEditWaitUploadListActivity.start(LabelEditSettingActivity.this);
                        finish();
                    }
                });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvLabelEditSettingProduct
                        , mBindingView.tvLabelEditSettingProductBatch
                        , mBindingView.tvLabelEditSettingNext)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_label_edit_setting_product) {// 产品名称选择
            ProductSelectListActivity.start(TYPE_PRODUCT_NAME, this);
        } else if (id == R.id.tv_label_edit_setting_product_batch) {// 产品批次选择
            if (TextUtils.isEmpty(mBindingView.tvLabelEditSettingProduct.getText())) {
                ToastUtils.showToast("请选择产品");
                return;
            }
            ProductBatchSelectListActivity.start(mViewModel.getProductId(), TYPE_PRODUCT_BATCH_NAME, this);
        } else if (id == R.id.tv_label_edit_setting_next) {
            onNextClick();
        }
    }

    private void onNextClick() {
        if (TextUtils.isEmpty(mBindingView.tvLabelEditSettingProduct.getText())) {
            ToastUtils.showToast("请选择产品");
            return;
        }
        ConfigurationEntity entity = mBindingView.getData();
        mViewModel.saveConfig(entity);
    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (MMKVUtils.getInt(ConstantUtil.CURRENT_CUSTOMER_TYPE)!=1){
            ToastUtils.showToast("该账号非总部账号，无权限操作");
            return;
        }
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, LabelEditSettingActivity.class);
            context.startActivity(intent);
        }
    }
}
