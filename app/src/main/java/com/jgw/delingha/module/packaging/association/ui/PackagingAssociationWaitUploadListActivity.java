package com.jgw.delingha.module.packaging.association.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityWaitUploadListBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.packaging.association.adpter.PackagingAssociationWaitUploadAdapter;
import com.jgw.delingha.module.packaging.association.viewmodel.PackagingAssociationWaitUploadViewModel;
import com.jgw.delingha.module.scan_back.ui.CommonPackageScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.entity.PackageConfigEntity;

import java.util.List;

/**
 * author : Cxz
 * data : 2020/3/4
 * description :  包装关联待执行配置列表界面
 */
public class PackagingAssociationWaitUploadListActivity extends BaseActivity<PackagingAssociationWaitUploadViewModel, ActivityWaitUploadListBinding> {
    private PackagingAssociationWaitUploadAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle("待执行包装关联");
        mBindingView.rvcWaitUploadList.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new PackagingAssociationWaitUploadAdapter();
        mBindingView.rvWaitUploadList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mViewModel.setDataList(mAdapter.getDataList());

        boolean isMix = getIntent().getBooleanExtra("isMix", false);
        mViewModel.setIsMix(isMix);

        mViewModel.loadConfigCodeList();
        updateCountView();
    }

    @Override
    public void initLiveData() {
        mViewModel.getConfigCodeListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    updateCountView();
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getDeleteItemLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.loadConfigCodeList();
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
                    mViewModel.uploadCodeList();
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
                .addOnClickListener()
                .addView(mBindingView.layoutWaitUploadBottom.tvWaitUploadListDelete)
                .addView(mBindingView.layoutWaitUploadBottom.tvWaitUploadListUpload)
                .addView(mBindingView.layoutWaitUploadBottom.tvWaitUploadListBack)
                .submit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.loadConfigCodeList();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_wait_upload_list_delete) {
            onDeleteClick();
        } else if (id == R.id.tv_wait_upload_list_upload) {
            onUploadClick();
        } else if (id == R.id.tv_wait_upload_list_back) {
            onBackPressed();
        }
    }

    private void onUploadClick() {
        if (mViewModel.isDataNoSelect()) {
            ToastUtils.showToast("请选择要上传的数据");
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.tryUploadCodeList();
            }
        });
    }

    private void onDeleteClick() {
        if (mViewModel.isDataNoSelect()) {
            ToastUtils.showToast("请选择要删除的条目");
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否确定删除?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.deleteSelectItem();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        List<PackageConfigEntity> dataList = mAdapter.getDataList();
        PackageConfigEntity entity = dataList.get(position);
        if (view.getId() == R.id.iv_packaging_association_wait_upload_select) {
            entity.setSelect(!entity.isSelect());//修改Item按钮在Adapter中设置
            mAdapter.notifyItemChanged(position);
        } else {
            CommonPackageScanBackActivity.start(this,0,entity.getId(),
                    CommonPackageScanBackActivity.TYPE_PACKAGE_ASSOCIATION);
        }
    }

    private void showUploadSuccessDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";

        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showSelectDialog(this,"数据上传成功", details, "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(PackagingAssociationWaitUploadListActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_PACKAGING_ASSOCIATION, PackagingAssociationWaitUploadListActivity.this);
                finish();
            }
        });
    }

    public void updateCountView() {
        int size = mAdapter.getDataList().size();
        Spanned text = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
        mBindingView.layoutWaitUploadBottom.tvWaitUploadListCodeAmount.setText(text);
    }

    /**
     * 跳转函数
     */
    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackagingAssociationWaitUploadListActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 不实际调用 仅代表此类入参
     * @param context 上下文
     * @param isMix 是否混合包装
     */
    public static void start(Context context,boolean isMix) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackagingAssociationWaitUploadListActivity.class);
            intent.putExtra("isMix",isMix);
            context.startActivity(intent);
        }
    }
}
