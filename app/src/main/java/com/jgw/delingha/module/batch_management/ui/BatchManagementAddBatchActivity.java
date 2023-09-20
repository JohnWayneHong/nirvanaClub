package com.jgw.delingha.module.batch_management.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BatchManagementBean;
import com.jgw.delingha.databinding.ActivityBatchManagementAddBatchBinding;
import com.jgw.delingha.module.batch_management.viewmodel.BatchManagementAddBatchViewModel;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.utils.PickerUtils;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by XiongShaoWu
 * on 2020/12/21
 * 新增批次
 */
public class BatchManagementAddBatchActivity extends BaseActivity<BatchManagementAddBatchViewModel, ActivityBatchManagementAddBatchBinding> {

    private boolean isEdit;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        Serializable data = getIntent().getSerializableExtra("data");
        String title;
        BatchManagementBean bean;
        if (data instanceof BatchManagementBean) {
            isEdit = true;
            title = "编辑批次";
            bean = (BatchManagementBean) data;
        } else {
            title = "新建批次";
            bean = new BatchManagementBean();
        }
        mBindingView.setData(bean);
        mViewModel.setData(bean);
        setTitle(title);
    }

    @Override
    public void initLiveData() {
        mViewModel.getProductInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ProductInfoEntity entity = resource.getData();
                    mViewModel.setProductInfo(entity);
                    refreshData();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getAddBatchLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    ToastUtils.showToast("提交成功");
                    dismissLoadingDialog();
                    finish();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getEditBatchLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    ToastUtils.showToast("提交成功");
                    dismissLoadingDialog();
                    finish();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvBatchManagementProductName)
                .addView(mBindingView.tvBatchManagementMarketTime)
                .addView(mBindingView.tvBatchManagementProduceTime)
                .addView(mBindingView.tvBatchManagementSubmit)
                .addOnClickListener()
                .submit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                // 产品名称
                long id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
        }
    }

    private void refreshData() {
        mBindingView.invalidateAll();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_batch_management_product_name) {
            ProductSelectListActivity.start(1, this);
        } else if (id == R.id.tv_batch_management_market_time) {
            showTimePicker(this,1);
        } else if (id == R.id.tv_batch_management_produce_time) {
            showTimePicker(this,2);
        } else if (id == R.id.tv_batch_management_submit) {
            if (checkInput()) {
                return;
            }
            if (isEdit) {
                mViewModel.editBatch();
            } else {
                mViewModel.addBatch();
            }
        }
    }

    public void showTimePicker(Context context, int type) {
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.YEAR, 2100);
        endDate.set(Calendar.MONTH, 0);
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        PickerUtils.showTimePicker(null, endDate.getTime(), context, time -> {
            if (type == 1) {
                mViewModel.setMarketDate(time);
            } else if (type == 2) {
                mViewModel.setProduceDate(time);
            }
            BatchManagementAddBatchActivity.this.refreshData();
        });
    }


    private boolean checkInput() {
        BatchManagementBean data = mBindingView.getData();
        String batchName = data.batchName;
        if (TextUtils.isEmpty(batchName)) {
            ToastUtils.showToast("请输入批次名称");
            return true;
        }
        String productName = data.productName;
        if (TextUtils.isEmpty(productName)) {
            ToastUtils.showToast("请选择产品");
            return true;
        }
//        String marketDate = data.marketDate;
//        if (TextUtils.isEmpty(marketDate)) {
//            ToastUtils.showToast("请选择上市时间");
//            return true;
//        }
        return false;
    }

    //新增
    public static void start(Context context) {
        start(context, null);
    }

    //编辑
    public static void start(Context context, BatchManagementBean bean) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, BatchManagementAddBatchActivity.class);
            intent.putExtra("data", bean);
            context.startActivity(intent);
        }
    }
}
