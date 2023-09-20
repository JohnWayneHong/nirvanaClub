package com.jgw.delingha.module.inventory.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InventoryDetailsBean;
import com.jgw.delingha.bean.InventoryListBean;
import com.jgw.delingha.databinding.ActivityInventoryDetailsBinding;
import com.jgw.delingha.module.inventory.adapter.InventoryDetailsRecyclerAdapter;
import com.jgw.delingha.module.inventory.viewmodel.InventoryDetailsViewModel;

/**
 * Created by XiongShaoWu
 * on 2020/5/18
 */
public class InventoryDetailsActivity extends BaseActivity<InventoryDetailsViewModel, ActivityInventoryDetailsBinding> {


    private InventoryDetailsRecyclerAdapter mAdapter;
    private String mHouseList;

    @Override
    public void initView() {
        mBindingView.rvcInventoryDetails.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    public void initData() {
        setTitle("盘点详情");

        String json = getIntent().getStringExtra("data");
        InventoryListBean.ListBean inventoryListBean = JsonUtils.parseObject(json, InventoryListBean.ListBean.class);

        mAdapter = new InventoryDetailsRecyclerAdapter();
        mAdapter.setHeaderData(inventoryListBean);
        mBindingView.rvInventoryDetails.setAdapter(mAdapter);

        mHouseList = inventoryListBean.houseList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.getInventoryDetails(mHouseList);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();

        mViewModel.getOrderDetails().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        InventoryDetailsBean.ListBean bean = mAdapter.getDataList().get(position - 1);
        int id = view.getId();
        if (id == R.id.tv_inventory_details_view) {
            InventoryFinishListActivity.start(this, bean.inventoryProductId);
        } else if (id == R.id.tv_inventory_details_scan_code) {
            InventoryPDAActivity.start(this, bean);
        }
    }

    public static void start(Context context, InventoryListBean.ListBean bean) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, InventoryDetailsActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            context.startActivity(intent);
        }
    }

}
