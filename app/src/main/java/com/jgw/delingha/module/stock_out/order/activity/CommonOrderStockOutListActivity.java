package com.jgw.delingha.module.stock_out.order.activity;

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
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CommonOrderStockOutListBean;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.stock_out.order.adapter.CommonOrderStockOutListRecyclerAdapter;
import com.jgw.delingha.module.stock_out.order.viewmodel.CommonOrderStockOutViewModel;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/16
 * 冈本(离线版)出库订单列表
 */
public class CommonOrderStockOutListActivity extends BaseActivity<CommonOrderStockOutViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private CommonOrderStockOutListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请扫码或输入单据号搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("单据出库");
        mAdapter = new CommonOrderStockOutListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());

        mBindingView.rvSelectCommon.setAdapter(mAdapter);

        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getOrderListLiveData().observe(this, resource -> {
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

    private void loadList(List<CommonOrderStockOutListBean> data) {
        if (mViewModel.getCurrentPage() == 1) {
            mAdapter.notifyRefreshList(data);
        } else {
            mAdapter.notifyAddListItem(data);
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
        CommonOrderStockOutListBean listBean = mAdapter.getDataList().get(position);
        CommonOrderStockOutDetailsActivity.start(this, listBean);
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        ScanCodeService.playSuccess();
        if (searchView != null) {
            searchView.setText(event.mCode);
        }
    }


    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.refreshList();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonOrderStockOutListActivity.class);
            context.startActivity(intent);
        }
    }


}
