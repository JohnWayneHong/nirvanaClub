package com.jgw.delingha.module.query.flow.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FlowQueryBean;
import com.jgw.delingha.databinding.ActivityFlowQueryBinding;
import com.jgw.delingha.module.query.flow.adpter.FlowQueryListAdapter;
import com.jgw.delingha.module.query.flow.viewmodel.FlowQueryV3ViewModel;

public class FlowQueryActivity extends BaseActivity<FlowQueryV3ViewModel, ActivityFlowQueryBinding> {

    private FlowQueryListAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.flow_query_setting_title));
        mBindingView.rvcFlowQuery.setEmptyLayout(R.layout.item_empty);
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.refreshLayout.setEnabled(false);
    }

    @Override
    protected void initData() {
        String code = getIntent().getStringExtra("data");
        mAdapter = new FlowQueryListAdapter();
        mBindingView.rvFlowQuery.setAdapter(mAdapter);
        mViewModel.queryFlow(code);
    }

    @Override
    public void initLiveData() {
        mViewModel.getFlowInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.refreshLayout.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.refreshLayout.setRefreshing(false);
                    FlowQueryBean bean = resource.getData();
                    mBindingView.alFlowQueryHead.setVisibility(View.VISIBLE);
                    mBindingView.flowQueryHead.flowQueryBelongsContent.setText(bean.product == null ? "" : bean.product.organizationFullName);
                    mBindingView.flowQueryHead.flowQueryCardContent.setText(bean.outerCodeId);
                    mAdapter.setData(bean);
                    mAdapter.notifyItemChanged(0);
                    mAdapter.notifyRefreshList(bean.list);
                    break;
                case Resource.ERROR:
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
            }
        });
    }

    public static void start(Context context, String code) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, FlowQueryActivity.class);
            intent.putExtra("data", code);
            context.startActivity(intent);
        }
    }
}
