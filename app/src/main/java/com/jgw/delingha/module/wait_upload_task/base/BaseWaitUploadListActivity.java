package com.jgw.delingha.module.wait_upload_task.base;

import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityOrderWaitUploadListBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.sql.entity.BaseOrderEntity;

public abstract class BaseWaitUploadListActivity<VM extends BaseWaitUploadViewModel, SV> extends BaseActivity<VM, ActivityOrderWaitUploadListBinding> {


    @Override
    protected void initView() {
        mBindingView.rvcOrderWaitUploadList.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        CustomRecyclerAdapter<BaseOrderEntity> adapter = getWaitUploadListAdapter();
        mBindingView.rvOrderWaitUploadList.setAdapter(adapter);
        mViewModel.setDataList(adapter.getDataList());
        mViewModel.loadList();
    }


    protected abstract CustomRecyclerAdapter<BaseOrderEntity> getWaitUploadListAdapter();

    @Override
    public void initLiveData() {
        mViewModel.getListDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    getWaitUploadListAdapter().notifyRefreshList(resource.getData());
                    calculationTotal();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getDeleteListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mViewModel.loadList();
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
        getWaitUploadListAdapter().setOnItemClickListener(this);
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutOrderWaitUploadBottom.tvWaitUploadListDelete)
                .addView(mBindingView.layoutOrderWaitUploadBottom.tvWaitUploadListBack)
                .submit();
    }

    public void calculationTotal() {
        int size = getWaitUploadListAdapter().getDataList().size();
        Spanned text = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
        mBindingView.layoutOrderWaitUploadBottom.tvWaitUploadListCodeAmount.setText(text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.loadList();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_wait_upload_list_delete) {
            deleteDataBySelect();
        } else if (id == R.id.tv_wait_upload_list_back) {
            super.onBackPressed();
        }
    }

    private void deleteDataBySelect() {
        if (mViewModel.checkSelectList()) {
            ToastUtils.showToast("请选择要删除的条目!");
            return;
        }
        mViewModel.deleteDataBySelect();
    }

    public void showUploadFinishDialog() {
        CommonDialogUtil.showSelectDialog(this, "提示", "数据上传成功", "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(BaseWaitUploadListActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(BaseWaitUploadListActivity.this, 0);
                TaskListActivity.start(getTaskType(), MainActivity.mMainActivityContext);
            }
        });
    }

    protected abstract int getTaskType();
}
