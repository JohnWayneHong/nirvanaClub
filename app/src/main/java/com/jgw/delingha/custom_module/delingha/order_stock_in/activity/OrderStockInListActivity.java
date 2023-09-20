package com.jgw.delingha.custom_module.delingha.order_stock_in.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockInListBean;
import com.jgw.delingha.custom_module.delingha.order_stock_in.adapter.OrderStockInListRecyclerAdapter;
import com.jgw.delingha.custom_module.delingha.order_stock_in.viewmodel.OrderStockInViewModel;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/16
 * 索契(在线)单据入库列表
 */
public class OrderStockInListActivity extends BaseActivity<OrderStockInViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private OrderStockInListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请输入入库单号搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("入库单");
        mAdapter = new OrderStockInListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());

        mBindingView.rvSelectCommon.setAdapter(mAdapter);

        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getInventoryList().observe(this, resource -> {
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
    }

    private void loadList(List<OrderStockInListBean.ListBean> data) {
        if (mViewModel.getCurrentPage() == 1) {
            mAdapter.notifyRemoveListItem();
        }
        mAdapter.notifyAddListItem(data);
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
                    mViewModel.setSearchText(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        mBindingView.rvSelectCommon.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id ==R.id.tv_order_stock_in_details){
            OrderStockInListBean.ListBean listBean = mAdapter.getDataList().get(position);
            OrderStockInDetailsActivity.start(this, listBean,true);
        }else if (id ==R.id.tv_order_stock_in_execute){
            OrderStockInListBean.ListBean listBean = mAdapter.getDataList().get(position);
            OrderStockInDetailsActivity.start(this, listBean,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_OK){
            return;
        }
        mViewModel.refreshList();
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderStockInListActivity.class);
            context.startActivity(intent);
        }
    }


}
