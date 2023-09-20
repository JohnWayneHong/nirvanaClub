package com.jgw.delingha.module.exchange_goods.order.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.Observer;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.bean.OrderExchangeGoodsResultBean;
import com.jgw.delingha.databinding.ActivityOrderExchangeGoodsDetailsBinding;
import com.jgw.delingha.module.exchange_goods.order.adapter.OrderExchangeGoodsDetailsRecyclerAdapter;
import com.jgw.delingha.module.exchange_goods.order.viewmodel.OrderExchangeGoodsDetailsViewModel;
import com.jgw.delingha.module.fail_log.ui.FailLogListActivity;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;

/**
 * Created by XiongShaoWu
 * on 2020/5/18
 */
public class OrderExchangeGoodsDetailsActivity extends BaseActivity<OrderExchangeGoodsDetailsViewModel, ActivityOrderExchangeGoodsDetailsBinding> {


    private OrderExchangeGoodsDetailsRecyclerAdapter mAdapter;

    @Override
    public void initView() {
        mBindingView.rvcOrderStockOut.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    public void initData() {
        setTitle("调货单详情");
        String data = getIntent().getStringExtra("data");
        mViewModel.setOrderCode(data);

        mAdapter = new OrderExchangeGoodsDetailsRecyclerAdapter();
        mViewModel.setList(mAdapter.getDataList());
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvOrderStockOut.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvOrderExchangeGoodsUpload)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();

        mViewModel.getOrderDetails().observe(this, new Observer<OrderExchangeGoodsDetailsBean>() {
            @Override
            public void onChanged(OrderExchangeGoodsDetailsBean bean) {
                mViewModel.setHouseList(bean.houseList);
                mAdapter.setOrderExchangeGoodsDetailsBean(bean);
                mAdapter.notifyItemChanged(0);
                mAdapter.notifyRefreshList(bean.products);
            }
        });

        mViewModel.getUploadLiveData().observe(this, new Observer<Resource<OrderExchangeGoodsResultBean>>() {
            @Override
            public void onChanged(Resource<OrderExchangeGoodsResultBean> result) {
                switch (result.getLoadingStatus()) {
                    case Resource.LOADING:
                        showLoadingDialog();
                        break;
                    case Resource.SUCCESS:
                        dismissLoadingDialog();
                        showUploadFinishDialog(result.getData());
                        break;
                    case Resource.ERROR:
                        dismissLoadingDialog();
                        break;
                }
            }
        });
    }

    private void showUploadFinishDialog(OrderExchangeGoodsResultBean data) {
        int errorSize = data.failureMessage == null ? 0 : data.failureMessage.size();
        String detail = "您成功上传了" + mViewModel.getAllSingleCodeCount() + "条数据.\n其中失败" + errorSize + "条";
        CommonDialogUtil.showSelectDialog(this,"数据上传成功", detail, "查看失败日志", "返回工作台", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(OrderExchangeGoodsDetailsActivity.this, 0);
                FailLogListActivity.start(OrderExchangeGoodsDetailsActivity.this, TaskListViewModel.TYPE_TASK_EXCHANGE_GOODS);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(OrderExchangeGoodsDetailsActivity.this, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String json;
            int index;
            switch (requestCode) {
                case 1:
                    //扫码返回
                case 2:
                    //查看反扫
                    if (data == null) {
                        return;
                    }
                    json = data.getStringExtra("data");
                    index = data.getIntExtra("index", -1);
                    mViewModel.updateOrderDetailsItem(index, json, mAdapter);
                    break;
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        OrderExchangeGoodsDetailsBean.ProductsBean bean;
        switch (view.getId()) {
            case R.id.tv_order_stock_out_details_delete:
                bean = mAdapter.getContentItemData(position);
                if (bean.actualSingleCodeNumber >= bean.singleCodeNumber) {
                    ToastUtils.showToast("该商品已完成发货,不可编辑");
                    return;
                }
                OrderExchangeGoodsScanBackActivity.start(OrderExchangeGoodsDetailsActivity.this, bean.codeList, position, 2);
                break;
            case R.id.tv_order_stock_out_details_scan_code:
                bean = mAdapter.getContentItemData(position);
                if (bean.actualSingleCodeNumber >= bean.singleCodeNumber) {
                    ToastUtils.showToast("该商品已完成发货,不可编辑");
                    return;
                }
                OrderExchangeGoodsScanActivity.start(OrderExchangeGoodsDetailsActivity.this, bean, position, 1);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_order_exchange_goods_upload:
                mViewModel.upload();
                break;
        }
    }

    public static void start(Context context, String orderCode) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderExchangeGoodsDetailsActivity.class);
            intent.putExtra("data", orderCode);
            context.startActivity(intent);
        }
    }

}
