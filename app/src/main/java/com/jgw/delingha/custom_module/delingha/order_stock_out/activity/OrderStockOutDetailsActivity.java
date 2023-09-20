package com.jgw.delingha.custom_module.delingha.order_stock_out.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.adapter.OrderStockOutDetailsRecyclerAdapter;
import com.jgw.delingha.custom_module.delingha.order_stock_out.viewmodel.OrderStockOutDetailsViewModel;
import com.jgw.delingha.databinding.ActivityOrderStockOutDetailsBinding;
import com.jgw.delingha.view.dialog.input.InputCheckNumberDialog;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/5/18
 */
public class OrderStockOutDetailsActivity extends BaseActivity<OrderStockOutDetailsViewModel, ActivityOrderStockOutDetailsBinding> {


    private OrderStockOutDetailsRecyclerAdapter mAdapter;
    private boolean isDetails;

    @Override
    public void initView() {
        mBindingView.rvcOrderStockOutDetails.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    public void initData() {
        setTitle("发货");

        String json = getIntent().getStringExtra("data");
        isDetails = getIntent().getBooleanExtra("isDetails",false);
        OrderStockOutListBean.ListBean inventoryListBean = JsonUtils.parseObject(json, OrderStockOutListBean.ListBean.class);

        mAdapter = new OrderStockOutDetailsRecyclerAdapter();
        mAdapter.setHeaderData(inventoryListBean);
        mBindingView.rvOrderStockOutDetails.setAdapter(mAdapter);
        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.getOrderStockOutDetails(inventoryListBean);
        mBindingView.llOrderStockOutButton.setVisibility(isDetails? View.GONE:View.VISIBLE);
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
                    List<OrderStockOutDetailsBean.ListBean> data = resource.getData();
                    if (isDetails){
                        for (OrderStockOutDetailsBean.ListBean b:data){
                            b.isDetails=1;
                        }
                    }
                    mAdapter.notifyItemChanged(0);
                    mAdapter.notifyRefreshList(data);
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getSaveCheckLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    ToastUtils.showToast("保存成功");
                    saveAndFinish();
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
        mViewModel.getUploadAllLiveData().observe(this, resource -> {
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

    private void saveAndFinish() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvOrderStockOutSaveCheck)
                .addView(mBindingView.tvOrderStockOutUpload)
                .submit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case 1://扫码返回
                int position = data.getIntExtra("position", -1);
                if (position == -1) {
                    return;
                }
                OrderStockOutDetailsBean.ListBean listBean = mAdapter.getDataList().get(position - 1);
                List<OrderStockScanBean> codeList = MMKVUtils.getTempDataList(OrderStockScanBean.class);
                listBean.codeList.clear();
                if (codeList != null) {
                    listBean.codeList.addAll(codeList);
                }
                mAdapter.notifyItemChanged(position);
                break;
        }
    }

    private void showUploadFinishDialog() {
        CommonDialogUtil.showConfirmDialog(this, "提示", "数据上传成功", "返回", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                saveAndFinish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == mBindingView.tvOrderStockOutSaveCheck.getId()) {
            saveCheck();
        } else if (id == mBindingView.tvOrderStockOutUpload.getId()) {
            upload();
        }
    }

    private void saveCheck() {
        mViewModel.saveCheck();
    }

    private void upload() {
        CommonDialogUtil.showSelectDialog(this, "提示", "是否确认全部发货?", "取消", "确认", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                mViewModel.uploadAll();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        OrderStockOutDetailsBean.ListBean bean = mAdapter.getDataList().get(position - 1);
        int id = view.getId();
        if (id == R.id.tv_order_stock_out_details_scan_code) {
            OrderStockOutPDAActivity.start(this, position, 1, bean);
        } else if (id == R.id.tv_order_stock_out_details_input) {
            InputCheckNumberDialog inputDialog = InputCheckNumberDialog.newInstance(this);
            inputDialog.setData(bean);
            inputDialog.show();
            inputDialog.setOnButtonClickListener(() -> mAdapter.notifyItemChanged(position));
        }else if (id == R.id.tv_order_stock_out_details_execute) {
            CommonDialogUtil.showSelectDialog(this, "提示", "是否确认发货?", "取消", "确认", new CommonDialog.OnButtonClickListener() {
                @Override
                public void onRightClick() {
                    mViewModel.upload(bean);;
                }
            });
        }
    }

    public static void start(Activity context, OrderStockOutListBean.ListBean bean, int requestCode) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderStockOutDetailsActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            context.startActivityForResult(intent,requestCode);
        }
    }
    public static void start(Activity context, OrderStockOutListBean.ListBean bean, boolean isDetails) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderStockOutDetailsActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            intent.putExtra("isDetails", isDetails);
            context.startActivity(intent);
        }
    }

}
