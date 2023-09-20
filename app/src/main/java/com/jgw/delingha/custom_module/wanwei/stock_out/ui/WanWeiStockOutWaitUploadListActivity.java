package com.jgw.delingha.custom_module.wanwei.stock_out.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StockOutFastWaitUploadListBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.custom_module.wanwei.stock_out.adapter.WanWeiStockOutWaitUploadListRecyclerAdapter;
import com.jgw.delingha.custom_module.wanwei.stock_out.viewmodel.WanWeiStockOutWaitUploadViewModel;
import com.jgw.delingha.databinding.ActivityStockOutFastWaitUploadBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;

/**
 * 出库待执行列表
 */
public class WanWeiStockOutWaitUploadListActivity extends BaseActivity<WanWeiStockOutWaitUploadViewModel, ActivityStockOutFastWaitUploadBinding> {

    private WanWeiStockOutWaitUploadListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcStockOutFastWaitUpload.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("待执行出库");
        mAdapter = new WanWeiStockOutWaitUploadListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvStockOutFastWaitUpload.setAdapter(mAdapter);
        calculationTotal();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getConfigGroupListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    calculationTotal();
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getRequestTaskIdLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.uploadConfigList();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    showUploadSuccessDialog(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvStockOutFastWaitUploadSubmit)
                .addView(mBindingView.tvStockOutFastWaitUploadDelete)
                .addView(mBindingView.tvStockOutFastWaitUploadBack)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_stock_out_fast_wait_upload_delete) {
            onDeleteClick();
        } else if (id == R.id.tv_stock_out_fast_wait_upload_back) {
            onBackPressed();
        } else if (id == R.id.tv_stock_out_fast_wait_upload_submit) {
            onUploadClick();
        }
    }

    private void onUploadClick() {
        if (mViewModel.isSelectEmpty()) {
            ToastUtils.showToast("请选择要上传的数据");
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.getTaskIdList();
            }
        });
    }

    private void onDeleteClick() {
        if (mViewModel.isSelectEmpty()) {
            ToastUtils.showToast("请选择要删除的条目");
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否确定删除?", "", "取消", "删除", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.deleteSelectItem();
                mAdapter.notifyRemoveListItem();
                mViewModel.getConfigGroupList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.getConfigGroupList();
    }

    @Override
    public void onItemClick(View view, int position) {
        StockOutFastWaitUploadListBean bean = mAdapter.getDataList().get(position);
        int id = view.getId();
        if (id == R.id.ll_stock_out_wait_upload_list_item) {
            CommonScanBackActivity.start(this, 1,  CommonScanBackActivity.TYPE_WANWEI_STOCK_OUT, bean.config_id);
        } else if (id == R.id.rl_stock_out_wait_upload_list_select) {
            bean.selected = !bean.selected;
            mAdapter.notifyItemChanged(position);
        }
    }

    public void showUploadSuccessDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";

        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showSelectDialog(this,"数据上传成功", details, "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(WanWeiStockOutWaitUploadListActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_OUT, WanWeiStockOutWaitUploadListActivity.this);
                finish();
            }
        });
    }

    private void calculationTotal() {
        int size = mAdapter.getDataList().size();
        mBindingView.tvStockOutFastWaitUploadAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, WanWeiStockOutWaitUploadListActivity.class);
            context.startActivity(intent);
        }
    }
}
