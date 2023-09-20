package com.jgw.delingha.module.tools_table.ui;

import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.listener.OnItemSingleClickListener;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.bean.ToolsTableHeaderBean;
import com.jgw.delingha.databinding.FragmentToolsTableBinding;
import com.jgw.delingha.module.tools_table.ToolsTableUtils;
import com.jgw.delingha.module.tools_table.adapter.ToolsTableRecyclerAdapter;
import com.jgw.delingha.module.tools_table.viewmodel.ToolsTableViewModel;


public class ToolsTableFragment extends BaseFragment<ToolsTableViewModel, FragmentToolsTableBinding> implements SwipeRefreshLayout.OnRefreshListener, OnItemSingleClickListener {

    private ToolsTableRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.srlToolsTable.setColorSchemeResources(R.color.main_color);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getToolsTableHeaderData();
        mViewModel.clearEmptyBoxCode();
    }

    @Override
    protected void initFragmentData() {
        mAdapter = new ToolsTableRecyclerAdapter();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setHeaderData(new ToolsTableHeaderBean());
        mBindingView.rvToolsTable.setAdapter(mAdapter);
        mViewModel.getToolsTableListData();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getToolsTableHeaderLiveData().observe(this, toolsTableHeaderBeanResource -> {
            switch (toolsTableHeaderBeanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.setHeaderData(toolsTableHeaderBeanResource.getData());
                    mAdapter.notifyItemChanged(0);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(toolsTableHeaderBeanResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getToolsTableListLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.srlToolsTable.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.srlToolsTable.setRefreshing(false);
                    if (listResource.getData().equals(mAdapter.getDataList())) {
                        return;
                    }
                    LogUtils.xswShowLog("");
                    mAdapter.notifyRefreshList(listResource.getData());
                    break;
                case Resource.ERROR:
                    mBindingView.srlToolsTable.setRefreshing(false);
                    break;
            }
        });
    }


    @Override
    protected void initListener() {
        super.initListener();
        mBindingView.srlToolsTable.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(View view, int groupPosition, int subPosition) {
        ToolsOptionsBean.MobileFunsBean bean = mAdapter.getDataList().get(groupPosition - 1).mobileFuns.get(subPosition);
        ToolsTableUtils.jumpToolTableOptionDetails(context, bean.appAuthCode);
    }

    @Override
    public void onRefresh() {
        mViewModel.getToolsTableListData();
    }

    public void refreshSystemExpireTime() {
        if (mAdapter != null && mAdapter.getItemCount() > 0) {
            mAdapter.notifyItemChanged(0);
        }
    }
}
