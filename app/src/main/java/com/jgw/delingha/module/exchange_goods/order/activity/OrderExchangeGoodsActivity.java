package com.jgw.delingha.module.exchange_goods.order.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsBean;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.exchange_goods.order.viewmodel.OrderExchangeGoodsViewModel;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 * 订单调货 订单列表
 */
public class OrderExchangeGoodsActivity extends BaseActivity<OrderExchangeGoodsViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请扫码或输入订单号搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("调货单");
        CustomRecyclerAdapter<OrderExchangeGoodsBean.ListBean> adapter = mViewModel.initAdapter();
        mBindingView.rvSelectCommon.setAdapter(adapter);

        mViewModel.refreshList();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBindingView.refreshLayout.setOnRefreshListener(this);

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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
//        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
//        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
        ScanCodeService.playSuccess();
        if (searchView != null) {
            searchView.setText(event.mCode);
        }
//        } else {
//            ScanCodeService.playError();
//        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getOrderList().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.refreshLayout.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.refreshLayout.setRefreshing(false);
                    mViewModel.setOrderList(resource.getData().list);
                    break;
                case Resource.ERROR:
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
            }
        });
        mViewModel.getClickItemLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String orderCode) {
                OrderExchangeGoodsDetailsActivity.start(OrderExchangeGoodsActivity.this, orderCode);
            }
        });
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderExchangeGoodsActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }
}
