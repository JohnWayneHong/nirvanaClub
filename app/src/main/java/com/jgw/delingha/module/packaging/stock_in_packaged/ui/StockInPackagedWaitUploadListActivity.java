package com.jgw.delingha.module.packaging.stock_in_packaged.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.click_utils.listener.OnItemSingleClickListener;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ConfigurationSelectBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityWaitUploadListBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.packaging.stock_in_packaged.adapter.StockInPackagedPendingConfigRecyclerAdapter;
import com.jgw.delingha.module.packaging.stock_in_packaged.viewmodel.StockInPackagedWaitUploadListViewModel;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;

/**
 * 生产入库待执行任务列表
 */
public class StockInPackagedWaitUploadListActivity extends BaseActivity<StockInPackagedWaitUploadListViewModel, ActivityWaitUploadListBinding> implements OnItemSingleClickListener {

    public static final int REQUEST_TASK_LIST = 235;
    private StockInPackagedPendingConfigRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_in_packaged_pending_list_title));
        mBindingView.rvcWaitUploadList.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new StockInPackagedPendingConfigRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvWaitUploadList.setAdapter(mAdapter);
        mViewModel.getConfigList();
    }


    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getSelectBeanList().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(listResource.getData());
                    countTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRequestTaskIdLiveData().observe(this, uploadResultBeanResource -> {
            switch (uploadResultBeanResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.uploadData();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(uploadResultBeanResource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getUploadLiveData().observe(this, uploadResultBeanResource -> {
            switch (uploadResultBeanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    showUploadFinishDialog(uploadResultBeanResource.getData());
                    mAdapter.notifyRemoveListItem();
                    mViewModel.getConfigList();
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(uploadResultBeanResource.getErrorMsg());
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
                MainActivity.start(StockInPackagedWaitUploadListActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(StockInPackagedWaitUploadListActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_IN, StockInPackagedWaitUploadListActivity.this);
            }
        });
    }

    public void countTotal() {
        int size = mAdapter.getDataList().size();
        mBindingView.layoutWaitUploadBottom.tvWaitUploadListCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutWaitUploadBottom.tvWaitUploadListBack)
                .addView(mBindingView.layoutWaitUploadBottom.tvWaitUploadListDelete)
                .addView(mBindingView.layoutWaitUploadBottom.tvWaitUploadListUpload)
                .submit();
    }

    @Override
    public void onItemClick(View view, int position) {
        ConfigurationSelectBean bean = mAdapter.getDataList().get(position);
        if (view.getId() == R.id.iv_select) {
            bean.selected = !bean.selected;
            mAdapter.notifyItemChanged(position);
        } else if (view.getId() == R.id.ll_pending_item_root) {
            CommonScanBackActivity.start(this, REQUEST_TASK_LIST,CommonScanBackActivity.TYPE_IN_PACKAGED, bean.configId);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_wait_upload_list_back) {
            onBackPressed();
        } else if (id == R.id.tv_wait_upload_list_delete) {
            if (mViewModel.getNoSelectedConfigList()) {
                ToastUtils.showToast("请选择要删除的条目");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"是否确定删除", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    mViewModel.deleteConfigs(mAdapter);
                    mViewModel.getConfigList();
                }
            });
        } else if (id == R.id.tv_wait_upload_list_upload) {
            if (mViewModel.getNoSelectedConfigList()) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_TASK_LIST) {
            mAdapter.notifyRemoveListItem();
            mViewModel.getConfigList();
        }
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockInPackagedWaitUploadListActivity.class);
            context.startActivity(intent);
        }
    }
}

