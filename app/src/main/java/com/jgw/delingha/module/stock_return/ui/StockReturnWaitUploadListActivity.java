package com.jgw.delingha.module.stock_return.ui;

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
import com.jgw.delingha.bean.StockReturnPendingConfigurationBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityWaitUploadListBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.stock_return.adapter.StockReturnPendingConfigurationAdapter;
import com.jgw.delingha.module.stock_return.viewmodel.StockReturnWaitUploadViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;


/**
 * author : Cxz
 * data : 2020/3/3
 * description : 退货待执行码配置页面
 */
public class StockReturnWaitUploadListActivity extends BaseActivity<StockReturnWaitUploadViewModel, ActivityWaitUploadListBinding> {

    private StockReturnPendingConfigurationAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle("待执行退货");
        mBindingView.rvcWaitUploadList.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new StockReturnPendingConfigurationAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvWaitUploadList.setAdapter(mAdapter);
        mViewModel.getLoadListData();
    }

    @Override
    public void initLiveData() {
        mViewModel.getLoadListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyAddListItem(resource.getData());
                    updateCountView();
                    break;
                case Resource.ERROR:
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
                    mViewModel.uploadData();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getUpLoadLiveData().observe(this, resource -> {
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

    public void updateCountView() {
        mBindingView.layoutWaitUploadBottom.tvWaitUploadListCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + mAdapter.getDataList().size() + "</font>条"));
    }

    @Override
    protected void initListener() {
        super.initListener();

        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.getRoot()
                        , mBindingView.layoutWaitUploadBottom.tvWaitUploadListDelete
                        , mBindingView.layoutWaitUploadBottom.tvWaitUploadListUpload
                        , mBindingView.layoutWaitUploadBottom.tvWaitUploadListBack)
                .submit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyRemoveListItem();
        mViewModel.getLoadListData();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_wait_upload_list_delete) {
            if (mViewModel.isDataNoSelect()) {
                ToastUtils.showToast("请选择要删除的条目");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"是否确定删除?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    mViewModel.deleteDataBySelect();
                    mAdapter.notifyRemoveListItem();
                    mViewModel.getLoadListData();
                }
            });
        } else if (id == R.id.tv_wait_upload_list_upload) {
            if (mViewModel.isDataNoSelect()) {
                ToastUtils.showToast("请选择要上传的数据");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    mViewModel.getTaskIdList();
                }
            });
        } else if (id == R.id.tv_wait_upload_list_back) {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id == R.id.iv_select) {
            StockReturnPendingConfigurationBean selectBean = mAdapter.getDataList().get(position);
            selectBean.isSelect = !selectBean.isSelect;//修改Item按钮在Adapter中设置
            mAdapter.notifyItemChanged(position);
        } else if (id == R.id.fl_configuration) {
            CommonScanBackActivity.start(this, 1, CommonScanBackActivity.TYPE_RETURN,mAdapter.getDataList().get(position).id);
        }
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
                MainActivity.start(StockReturnWaitUploadListActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(StockReturnWaitUploadListActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_RETURN, StockReturnWaitUploadListActivity.this);
            }
        });
    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockReturnWaitUploadListActivity.class);
            context.startActivity(intent);
        }
    }

}
