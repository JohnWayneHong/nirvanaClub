package com.jgw.delingha.module.logistics_statistics.statistics_list;

import android.content.Context;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.bean.StatisticsResultBean;
import com.jgw.delingha.databinding.ActivityCommonStatisticsListBinding;

import java.util.List;


/**
 * Created by xsw
 * on 2020/2/28
 * 统计列表
 */
public class StatisticsListActivity extends BaseActivity<StatisticsListViewModel, ActivityCommonStatisticsListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final int STATISTICAL_DIMENSION_STOCK_IN = 1;
    public static final int STATISTICAL_DIMENSION_STOCK_OUT = 2;
    public static final int STATISTICAL_DIMENSION_STOCK_RETURN = 3;
    public static final int STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE = 4;
    public static final int STATISTICAL_DIMENSION_EXCHANGE_GOODS = 5;
    private StatisticalListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.srlCommonStatistics.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcCommonStatistics.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        int type = getIntent().getIntExtra("type", -1);
        if (type == -1) {
            ToastUtils.showToast("数据异常");
            return;
        }
        StatisticsParamsBean tempData = MMKVUtils.getTempData(StatisticsParamsBean.class);
        mBindingView.setData(tempData);
        mViewModel.setType(type);
        mViewModel.setDate(tempData);
        String title = mViewModel.getTitle(type);
        setTitle(title);

        mAdapter = new StatisticalListRecyclerAdapter();
        mAdapter.setType(type);
        mBindingView.rvCommonStatistics.setAdapter(mAdapter);
        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.loadList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.srlCommonStatistics.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.srlCommonStatistics.setRefreshing(false);
                    loadList(resource.getData());
                    break;
                case Resource.ERROR:
                    mBindingView.srlCommonStatistics.setRefreshing(false);
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBindingView.rvCommonStatistics.addOnScrollListener(new com.jgw.common_library.listener.OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
        mBindingView.srlCommonStatistics.setOnRefreshListener(this);
    }

    private void loadList(List<StatisticsResultBean> newList) {
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
    public void onRefresh() {
        mViewModel.onRefresh();
    }

    public static void start(Context context, StatisticsParamsBean data, int type) {
        if (BaseActivity.isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(data);
            Intent intent = new Intent(context, StatisticsListActivity.class);
            intent.putExtra("type", type);
            context.startActivity(intent);
        }
    }

}
