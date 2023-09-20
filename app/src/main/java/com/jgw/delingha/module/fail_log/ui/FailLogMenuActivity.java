package com.jgw.delingha.module.fail_log.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityFailLogMenuBinding;
import com.jgw.delingha.module.fail_log.adapter.FailLogMenuRecyclerAdapter;
import com.jgw.delingha.module.fail_log.viewmodel.FailLogMenuViewModel;


public class FailLogMenuActivity extends BaseActivity<FailLogMenuViewModel, ActivityFailLogMenuBinding> {

    private FailLogMenuRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.upload_fail_title));
        mBindingView.rvcFailLog.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new FailLogMenuRecyclerAdapter();
        mBindingView.rvFailLog.setAdapter(mAdapter);
        mBindingView.rvFailLog.setGridVerticalLayoutManager(2);
        mViewModel.requestFirstData();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getFailLogMeanBean().observe(this, listResource -> {
            if (listResource.getLoadingStatus() == Resource.SUCCESS) {
                mAdapter.notifyRefreshList(listResource.getData());
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            context.startActivity(new Intent(context, FailLogMenuActivity.class));
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        FailLogListActivity.start(this, mAdapter.getDataList().get(position).type);
    }

}
