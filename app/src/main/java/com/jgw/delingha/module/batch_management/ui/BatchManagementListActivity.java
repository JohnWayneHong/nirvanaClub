package com.jgw.delingha.module.batch_management.ui;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BatchManagementBean;
import com.jgw.delingha.databinding.ActivityCommonSelectListButtonBinding;
import com.jgw.delingha.module.batch_management.adapter.BatchManagementListRecyclerAdapter;
import com.jgw.delingha.module.batch_management.viewmodel.BatchManagementListViewModel;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 批次管理列表
 */
public class BatchManagementListActivity extends BaseActivity<BatchManagementListViewModel, ActivityCommonSelectListButtonBinding> implements SwipeRefreshLayout.OnRefreshListener {


    private EditText searchView;
    private BatchManagementListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请输入批次搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);

        mBindingView.tvSelectCommonBottomButton.setText("新建批次");
    }

    @Override
    protected void initData() {
        setTitle("批次管理");
        mAdapter = new BatchManagementListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.loadList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getBatchManagementLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.refreshLayout.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.refreshLayout.setRefreshing(false);
                    loadList(resource.getData());
                    break;
                case Resource.ERROR:
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
            }
        });
        mViewModel.getDeleteLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mViewModel.refreshList();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void loadList(List<BatchManagementBean> list) {
        int currentPage = mViewModel.getCurrentPage();
        if (currentPage == 1) {
            mAdapter.notifyRefreshList(list);
        } else {
            mAdapter.notifyAddListItem(list);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBindingView.refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);

        if (searchView != null) {
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mViewModel.setSearchText(s.toString());
                }
            });
        }
        mBindingView.rvSelectCommon.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
        ClickUtils.register(this)
                .addView(mBindingView.tvSelectCommonBottomButton)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.tv_select_common_bottom_button) {
            BatchManagementAddBatchActivity.start(this);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        BatchManagementBean bean = mAdapter.getDataList().get(position);
        int id = view.getId();
        if (id == R.id.tv_batch_management_delete) {
            //删除批次
            mViewModel.deleteBatch(bean.id);
        } else if (id == R.id.tv_batch_management_edit) {
            BatchManagementAddBatchActivity.start(this, bean);
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        ScanCodeService.playSuccess();
        if (searchView != null) {
            searchView.setText(event.mCode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.refreshList();
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, BatchManagementListActivity.class);
            context.startActivity(intent);
        }
    }


}
