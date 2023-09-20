package com.jgw.delingha.custom_module.delingha.order_stock_in.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockInListBean;
import com.jgw.delingha.custom_module.delingha.order_stock_in.adapter.OrderStockInDetailsRecyclerAdapter;
import com.jgw.delingha.custom_module.delingha.order_stock_in.viewmodel.OrderStockInDetailsViewModel;
import com.jgw.delingha.databinding.ActivityOrderStockInDetailsBinding;

/**
 * Created by XiongShaoWu
 * on 2020/5/18
 */
public class OrderStockInDetailsActivity extends BaseActivity<OrderStockInDetailsViewModel, ActivityOrderStockInDetailsBinding> {


    private OrderStockInDetailsRecyclerAdapter mAdapter;
    private OrderStockInListBean.ListBean inventoryListBean;

    @Override
    public void initView() {
        mBindingView.rvcOrderStockInDetails.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    public void initData() {
        setTitle("入库");
        boolean isDetails = getIntent().getBooleanExtra("isDetails", false);
        mBindingView.tvOrderStockInUpload.setVisibility(isDetails?View.GONE:View.VISIBLE);
        String json = getIntent().getStringExtra("data");
        inventoryListBean = JsonUtils.parseObject(json, OrderStockInListBean.ListBean.class);

        mAdapter = new OrderStockInDetailsRecyclerAdapter();
        mAdapter.setHeaderData(inventoryListBean);
        mBindingView.rvOrderStockInDetails.setAdapter(mAdapter);
        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.getOrderStockInDetails(inventoryListBean.inHouseList);

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

        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    showUploadFinishDialog();
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvOrderStockInUpload)
                .submit();
    }

    private void showUploadFinishDialog() {
        CommonDialogUtil.showConfirmDialog(this, "提示", "数据上传成功", "返回", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                setResult(RESULT_OK);
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.tv_order_stock_in_upload) {
            mViewModel.upload(inventoryListBean.inHouseList, inventoryListBean.wareHouseInId);
        }
    }

    public static void start(Activity context, OrderStockInListBean.ListBean bean, int requestCode) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderStockInDetailsActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            context.startActivityForResult(intent,requestCode);
        }
    }

    public static void start(Context context, OrderStockInListBean.ListBean bean,boolean isDetails) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderStockInDetailsActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            intent.putExtra("isDetails", isDetails);
            context.startActivity(intent);
        }
    }

}
