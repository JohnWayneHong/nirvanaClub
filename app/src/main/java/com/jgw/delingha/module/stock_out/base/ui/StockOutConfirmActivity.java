package com.jgw.delingha.module.stock_out.base.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityStockOutConfirmBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.stock_out.base.adapter.StockOutConfirmNewRecyclerAdapter;
import com.jgw.delingha.module.stock_out.base.viewmodel.StockOutConfirmViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.utils.ConstantUtil;

/**
 * 出库
 */
public class StockOutConfirmActivity extends BaseActivity<StockOutConfirmViewModel, ActivityStockOutConfirmBinding> {

    private StockOutConfirmNewRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle("出库确认");
        mBindingView.rvcStockOutConfirm.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        long configId = intent.getLongExtra(ConstantUtil.CONFIG_ID, -1);
        mViewModel.setConfigId(configId);

        mAdapter = new StockOutConfirmNewRecyclerAdapter();
        mBindingView.rvStockOutConfirm.setAdapter(mAdapter);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getFormatListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("获取码信息失败");
                    break;
            }
        });
        mViewModel.getHeaderDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity configurationEntity = resource.getData();
                    mBindingView.layoutStockOutHeader.setData(configurationEntity);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("初始化信息失败,请重试");
                    break;
            }
        });
        mViewModel.getRequestTaskIdLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.uploadCodes();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    showUploadFinishDialog(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void showUploadFinishDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";

        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showSelectDialog(this,"数据上传成功", details, "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(StockOutConfirmActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(StockOutConfirmActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_OUT, StockOutConfirmActivity.this);
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvStockOutConfirmSubmit)
                .addView(mBindingView.tvStockOutConfirmPrevious)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_stock_out_confirm_submit) {
            mViewModel.getTaskId();
        } else if (id == R.id.tv_stock_out_confirm_previous) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mViewModel.clearSQLCache();
    }

    public static void start(int requestCode, Activity context, long configId) {
        Intent intent = new Intent(context, StockOutConfirmActivity.class);
        intent.putExtra(ConstantUtil.CONFIG_ID, configId);
        context.startActivityForResult(intent, requestCode);
    }
}
