package com.jgw.delingha.module.wait_upload_task.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.WaitUploadMenuBean;
import com.jgw.delingha.databinding.ActivityWaitUploadMenuBinding;
import com.jgw.delingha.module.tools_table.ToolsTableUtils;
import com.jgw.delingha.module.wait_upload_task.adapter.WaitUploadMenuRecyclerAdapter;
import com.jgw.delingha.module.wait_upload_task.viewmodel.WaitUploadMenuViewModel;

public class WaitUploadMenuActivity extends BaseActivity<WaitUploadMenuViewModel, ActivityWaitUploadMenuBinding> {
    private WaitUploadMenuRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.pending_task_title));
        mBindingView.rvcPending.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new WaitUploadMenuRecyclerAdapter();
        mBindingView.rvPending.setAdapter(mAdapter);
        mBindingView.rvPending.setGridVerticalLayoutManager(2);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getLoadListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("获取待执行菜单失败");
                    break;
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        WaitUploadMenuBean bean = mAdapter.getDataList().get(position);
        onJumpWaitUploadTask(bean);
    }

    public void onJumpWaitUploadTask(WaitUploadMenuBean bean) {
        Intent intent = new Intent(this, bean.getFunctionClass());
        switch (bean.getAppAuthCode()) {
            case ToolsTableUtils.DanGeChaiJie:
                intent.putExtra("isSingleDisassemble", true);
                break;
            case ToolsTableUtils.ZhengZuChaiJie:
                intent.putExtra("isSingleDisassemble", false);
                break;
            case ToolsTableUtils.HunHeBaoZhuang:
                intent.putExtra("isMix", true);
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.loadList();
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            context.startActivity(new Intent(context, WaitUploadMenuActivity.class));
        }
    }
}
