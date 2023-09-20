package com.jgw.delingha.module.stock_out.order.activity;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CommonOrderStockOutListBean;
import com.jgw.delingha.databinding.ActivityCommonOrderDetailsBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonOrderProductScanBackActivity;
import com.jgw.delingha.module.stock_out.order.adapter.CommonOrderStockOutDetailsRecyclerAdapter;
import com.jgw.delingha.module.stock_out.order.viewmodel.CommonOrderStockOutDetailsViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;

/**
 * Created by XiongShaoWu
 * on 2020/5/18
 */
public class CommonOrderStockOutDetailsActivity extends BaseActivity<CommonOrderStockOutDetailsViewModel, ActivityCommonOrderDetailsBinding> {


    private CommonOrderStockOutDetailsRecyclerAdapter mAdapter;
    private int mClickProductPosition;
    private int mItemClickType;

    @Override
    public void initView() {
        mBindingView.rvcCommonOrderDetails.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    public void initData() {
        setTitle("单据出库详情");
        mAdapter = new CommonOrderStockOutDetailsRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());


        String json = getIntent().getStringExtra("data");
        long orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId != -1) {
            mViewModel.getLocalOrderStockOutInfo(orderId);
        } else {
            CommonOrderStockOutListBean orderBean = JsonUtils.parseObject(json, CommonOrderStockOutListBean.class);
            mViewModel.getOrderStockOutInfo(orderBean);
        }
    }


    @Override
    public void initLiveData() {
        super.initLiveData();

        mViewModel.getOrderStockOutLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    OrderStockOutEntity data = resource.getData();
                    mViewModel.setOrderData(data);
                    boolean canScanCode = data.getId() > 0;
                    mAdapter.setHeaderData(data);
                    mBindingView.rvCommonOrderDetails.setAdapter(mAdapter);
                    if (!canScanCode || data.getIsInvalid()) {
                        mViewModel.getProductList(data);
                        return;
                    }
                    mViewModel.refreshProductCodeNumber(data);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getProductListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mAdapter.notifyRefreshList(resource.getData());
                    if (mAdapter.getHeaderBean().getIsInvalid()) {
                        showOrderInfoChangeDialog();
                    }
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getInsertOrderDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mAdapter.notifyRefreshList(resource.getData());
                    OrderStockOutProductInfoEntity entity = mAdapter.getDataList().get(mClickProductPosition - 1);
                    long id = entity.getId();
                    if (id == 0) {
                        ToastUtils.showToast("获取商品信息失败");
                        return;
                    }
                    if (mItemClickType == 1) {
                        checkScanCodeNumber(entity);
                    } else if (mItemClickType == 2) {
                        checkScanCodeNumber(entity);
                    } else if (mItemClickType == 3) {
                        mViewModel.inputNumber(entity);
                    } else {
                        ToastUtils.showToast("状态异常");
                    }

                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getLocalOrderStockOutLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    OrderStockOutEntity data = resource.getData();
                    mViewModel.setOrderData(data);
                    mAdapter.setHeaderData(data);
                    mBindingView.rvCommonOrderDetails.setAdapter(mAdapter);
                    mViewModel.refreshProductCodeNumber(data);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getRefreshProductCodeNumberLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.getLocalProductList();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getLocalProductListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mAdapter.notifyRefreshList(resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getOrderInfoChangeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getProductInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    updateProductInfo(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getInputNumberLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    OrderStockOutProductInfoEntity data = resource.getData();
                    updateProductInfo(data);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getCheckScanCodeNumberLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    OrderStockOutProductInfoEntity entity = resource.getData();
                    long id = entity.getId();
                    if (id == 0) {
                        ToastUtils.showToast("获取商品信息失败");
                        return;
                    }
                    if (mItemClickType == 1) {
                        jumpScanCode(id);
                    } else if (mItemClickType == 2) {
                        jumpScanBack(id);
                    } else {
                        ToastUtils.showToast("状态异常");
                    }

                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
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

    private void updateProductInfo(OrderStockOutProductInfoEntity data) {
        mAdapter.notifyRefreshItem(data);
    }

    private void showOrderInfoChangeDialog() {
        CommonDialogUtil.showConfirmDialog(this, "提示", "订单信息有变化,请重新确认订单信息!", "确认", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                mViewModel.onOrderInfoChange();
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvCommonOrderDetailsUpload)
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
            case 2://反扫
                long productId = data.getLongExtra("productId", -1);
                if (productId == -1) {
                    return;
                }
                mViewModel.getProductInfo(productId);
                break;
        }
    }

    private void showUploadFinishDialog() {
        CommonDialogUtil.showSelectDialog(this, "提示", "数据上传成功", "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(CommonOrderStockOutDetailsActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(CommonOrderStockOutDetailsActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_OUT, MainActivity.mMainActivityContext);
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == mBindingView.tvCommonOrderDetailsUpload.getId()) {
            mViewModel.upload();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mViewModel.checkScanCodeListEmpty()) {
            CommonDialogUtil.showSelectDialog(this, "是否退出当前操作?", "若退出,您所操作的数据将被存入本地.",
                    "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                            finish();
                        }

                        @Override
                        public void onRightClick() {
                            mViewModel.upload();
                        }
                    });
        } else {
            finish();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        OrderStockOutProductInfoEntity bean = mAdapter.getDataList().get(position - 1);
        mClickProductPosition = position;
        long id = bean.getId();

        mItemClickType = 0;//0意外 1扫码 2编辑
        int viewId = view.getId();
        if (viewId == R.id.tv_order_stock_out_details_input) {
            mItemClickType = 3;
            inputFirstNumber(bean);
        } else if (viewId == R.id.tv_order_stock_out_details_scan_code) {
            mItemClickType = 1;
            if (id == 0) {
                mViewModel.insertOrderData(mAdapter.getHeaderBean());
            } else {
                checkScanCodeNumber(bean);
            }
        } else if (viewId == R.id.tv_order_stock_out_details_edit) {
            mItemClickType = 2;
            if (id == 0) {
                mViewModel.insertOrderData(mAdapter.getHeaderBean());
            } else {
                checkScanCodeNumber(bean);
            }
        }
    }

    private void checkScanCodeNumber(OrderStockOutProductInfoEntity entity) {
        mViewModel.checkScanCodeNumber(entity);
    }

    private void jumpScanBack(long id) {
        CommonOrderProductScanBackActivity.start(this, 2, id, CommonOrderProductScanBackActivity.TYPE_STOCK_OUT);
    }

    private void jumpScanCode(long id) {
        CommonOrderStockOutPDAActivity.start(this, 1, id);
    }


    private void inputFirstNumber(OrderStockOutProductInfoEntity productBean) {
        int currentInputSingleNumber = productBean.getCurrentInputSingleNumber();
        String hint = currentInputSingleNumber == 0 ? "手工录入数量" : currentInputSingleNumber + "";
        CommonDialogUtil.showInputDialog(this, "手输", hint, "取消", "确定", InputType.TYPE_CLASS_NUMBER, new CommonDialog.OnButtonClickListener() {
            int inputNumber = 0;

            @Override
            public boolean onInput(String input) {
                if (TextUtils.isEmpty(input)) {
                    return false;
                }
                if (input.length() > 9) {
                    ToastUtils.showToast("录入数量过大!");
                    return false;
                }
                inputNumber = Integer.parseInt(input);
                productBean.setTempInputSingleNumber(inputNumber);
                return true;
            }

            @Override
            public void onRightClick() {
                if (productBean.getId() == 0) {
                    mViewModel.insertOrderData(mAdapter.getHeaderBean());
                } else {
                    mViewModel.inputNumber(productBean);
                }
            }
        });
    }

    /**
     *
     * @param context
     * @param bean
     */
    public static void start(Context context, CommonOrderStockOutListBean bean) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonOrderStockOutDetailsActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            context.startActivity(intent);
        }
    }

    /**
     * 待执行跳转直接获取本地数据
     * @param context
     * @param orderId
     */
    public static void start(Context context, long orderId) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonOrderStockOutDetailsActivity.class);
            intent.putExtra("orderId", orderId);
            context.startActivity(intent);
        }
    }

}
