package com.jgw.delingha.custom_module.delingha.order_stock_out.activity;

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
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.adapter.OrderStockOutListRecyclerAdapter;
import com.jgw.delingha.custom_module.delingha.order_stock_out.viewmodel.OrderStockOutViewModel;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/16
 * 单据入库列表
 */
public class OrderStockOutListActivity extends BaseActivity<OrderStockOutViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private OrderStockOutListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请输入出库单号搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("发货单");
        mAdapter = new OrderStockOutListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());

        mBindingView.rvSelectCommon.setAdapter(mAdapter);

        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getOrderStockOutList().observe(this, resource -> {
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

    private void loadList(List<OrderStockOutListBean.ListBean> data) {
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
        if (id ==R.id.tv_order_stock_out_execute){
            OrderStockOutListBean.ListBean listBean = mAdapter.getDataList().get(position);
            OrderStockOutDetailsActivity.start(this, listBean,1);
        }else if (id ==R.id.tv_order_stock_out_details){
            OrderStockOutListBean.ListBean listBean = mAdapter.getDataList().get(position);
            OrderStockOutDetailsActivity.start(this, listBean,true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_OK){
            return;
        }
        onRefresh();
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderStockOutListActivity.class);
            context.startActivity(intent);
        }
    }


}
