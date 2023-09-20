package com.jgw.delingha.module.select_list.organization_list;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrganizationBean;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;

import java.util.List;

public class OrganizationListActivity extends BaseActivity<OrganizationListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME_ID = "organization_id";
    public static final String EXTRA_NAME_NAME = "organization_name";
    private OrganizationListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("企业列表");

        mAdapter = new OrganizationListRecyclerAdapter();
        mBindingView.rvSelectCommon.setAdapter(mAdapter);

        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.loadList();
    }

    @Override
    public void initLiveData() {
        mViewModel.getListDataLiveData().observe(this, resource -> {
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
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    private void loadList(List<OrganizationBean.ListBean> newList) {
        int currentPage = mViewModel.getCurrentPage();
        int size = mAdapter.getDataList().size();
        int newListSize = newList.size();
        if (currentPage == 1) {
            mAdapter.clearDataList();
            if (size != 0) {
                mAdapter.notifyItemRangeRemoved(0, size);
            } else if (newListSize > 0) {
                mAdapter.notifyItemRemoved(0);
            } else {
                mAdapter.notifyItemChanged(0);
            }
        }
        mAdapter.getDataList().addAll(newList);
        mAdapter.notifyItemRangeInserted(size, newListSize);
    }

    @Override
    protected void initListener() {
        super.initListener();
        EditText searchView = getSearchView();
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
                    String str = s.toString();
                    mViewModel.doSearch(str);
                }
            });
        }
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.getRoot())
                .submit();
        mBindingView.refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvSelectCommon.addOnScrollListener(new com.jgw.common_library.listener.OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        OrganizationBean.ListBean listBean = mAdapter.getDataList().get(position);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NAME_ID, listBean.organizationId);
        intent.putExtra(EXTRA_NAME_NAME, listBean.organizationFullName);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRefresh() {
        mViewModel.onRefresh();
    }

    public static void start(int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrganizationListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }

}
