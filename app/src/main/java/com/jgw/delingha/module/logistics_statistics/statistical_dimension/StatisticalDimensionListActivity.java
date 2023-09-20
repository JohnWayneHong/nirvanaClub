package com.jgw.delingha.module.logistics_statistics.statistical_dimension;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.databinding.ActivityCommonSelectListButtonBinding;

import java.util.List;

public class StatisticalDimensionListActivity extends BaseActivity<StatisticalDimensionListViewModel, ActivityCommonSelectListButtonBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "statistical_dimension_id";
    private StatisticalDimensionListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("统计维度");
        int type = getIntent().getIntExtra("type", -1);
        if (type == -1) {
            ToastUtils.showToast("数据异常");
            return;
        }
        mViewModel.setStatisticalDimensionType(type);
        mAdapter = new StatisticalDimensionListRecyclerAdapter();
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
                .addView(mBindingView.tvSelectCommonBottomButton)
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
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == mBindingView.tvSelectCommonBottomButton.getId()) {
            MMKVUtils.saveTempData(mViewModel.getSelectList());
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void loadList(List<StatisticsParamsBean.StatisticalDimension> newList) {
        int currentPage = mViewModel.getCurrentPage();
        if (currentPage == 1) {
            int size = mAdapter.getDataList().size();
            if (size == newList.size()) {
                mAdapter.notifyRefreshList(newList);
                return;
            } else {
                mAdapter.notifyRemoveListItem();
            }
        }
        mAdapter.notifyAddListItem(newList);
    }

    @Override
    public void onItemClick(View view, int position) {
        StatisticsParamsBean.StatisticalDimension bean = mAdapter.getDataList().get(position);
        bean.setSelect(!bean.select);
        mViewModel.updateSelectData(bean);
    }

    @Override
    public void onRefresh() {
        mViewModel.onRefresh();
    }

    public static void start(int requestCode, Activity context, int type) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StatisticalDimensionListActivity.class);
            intent.putExtra("type", type);
            context.startActivityForResult(intent, requestCode);
        }
    }

}
