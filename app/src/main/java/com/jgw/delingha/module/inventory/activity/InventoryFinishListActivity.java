package com.jgw.delingha.module.inventory.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InventoryCodeBean;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.inventory.adapter.InventoryFinishListRecyclerAdapter;
import com.jgw.delingha.module.inventory.viewmodel.InventoryFinishListViewModel;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/16
 * 盘点单据列表
 */
public class InventoryFinishListActivity extends BaseActivity<InventoryFinishListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private InventoryFinishListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请扫码或输入搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("查看");
        String inventoryProductId = getIntent().getStringExtra("inventoryProductId");
        mViewModel.setInventoryProductId(inventoryProductId);
        mAdapter = new InventoryFinishListRecyclerAdapter();
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

    private void loadList(List<InventoryCodeBean.ListBean> list) {
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context, String inventoryProductId) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, InventoryFinishListActivity.class);
            intent.putExtra("inventoryProductId", inventoryProductId);
            context.startActivity(intent);
        }
    }


}
