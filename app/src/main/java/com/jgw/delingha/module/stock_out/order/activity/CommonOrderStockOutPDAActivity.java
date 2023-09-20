package com.jgw.delingha.module.stock_out.order.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.BR;
import com.jgw.delingha.BuildConfig;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.databinding.ActivityCommonOrderStockOutPdaBinding;
import com.jgw.delingha.module.scan_back.ui.CommonOrderProductScanBackActivity;
import com.jgw.delingha.module.stock_out.order.viewmodel.CommonOrderStockOutPDAViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.BaseOrderScanCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;
import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/23
 */
public class CommonOrderStockOutPDAActivity extends BaseActivity<CommonOrderStockOutPDAViewModel, ActivityCommonOrderStockOutPdaBinding> {


    private CodeEntityRecyclerAdapter<BaseOrderScanCodeEntity> mAdapter;
    private long productId;

    @Override
    protected void initView() {
        if (!TextUtils.equals(BuildConfig.BUILD_TYPE, "release")) {
            mBindingView.rvOrderStockOutPda.setItemAnimator(null);
        }
        mBindingView.rvcOrderStockOutPda.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("出库");
        setRight("重试");
        Intent intent = getIntent();
        productId = intent.getLongExtra("productId", -1);
        mViewModel.getProductInfo(productId);

        mAdapter = new CodeEntityRecyclerAdapter<>();
        mBindingView.rvOrderStockOutPda.setAdapter(mAdapter);
        mViewModel.calculationTotal();

    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutStockActionFooter.tvInputCode)
                .addView(mBindingView.layoutStockActionFooter.tvScanBack)
                .addView(mBindingView.layoutStockActionFooter.tvSure)
                .addOnClickListener()
                .submit();
        mBindingView.rvOrderStockOutPda.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.loadMoreList();
            }
        });
    }

    @Override
    public void initLiveData() {
        mViewModel.getProductInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    OrderStockOutProductInfoEntity entity = resource.getData();
                    mViewModel.setHeaderData(entity);
                    mBindingView.layoutOrderStockOutHeader.setData(entity);
                    dismissLoadingDialog();
                    mViewModel.setDataList(mAdapter.getDataList());
                    refreshList();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getCheckOrderStockOutCodeLiveData().observe(this, resource -> {
            OrderStockOutScanCodeEntity bean = resource.getData();
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCodeStatusItem(bean);
                    mViewModel.calculationTotal();
                    break;
                case Resource.ERROR:
                    showCodeErrorDialog(bean.getCode(), resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    updateCodeStatusItem(bean);
                    break;
            }
        });
        mViewModel.getPostGroupCheckCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    showUploadFinishDialog(resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getListDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (mViewModel.getPage() == 1) {
                        mAdapter.notifyRefreshList(resource.getData());
                    } else {
                        mAdapter.notifyAddListItem(resource.getData());
                    }
                    mViewModel.calculationTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    OrderStockOutProductInfoEntity data = mBindingView.layoutOrderStockOutHeader.getData();
                    if (data != null) {
                        data.notifyPropertyChanged(BR.totalCurrentSingleNumberText);
                        data.notifyPropertyChanged(BR.unCheckCodeNumberText);
                    }
                    updateTotal(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    /**
     * 获取到码信息成功更新 或网络错误 码更新为错误状态
     */
    private void updateCodeStatusItem(OrderStockOutScanCodeEntity data) {
        mAdapter.notifyRefreshItemStatus(data.getCode(), data.getCodeStatus(), data.getSingleNumber());
    }

    @Override
    protected void clickRight() {
        super.clickRight();
        mViewModel.tryAgainUpload();
    }

    private void showUploadFinishDialog(List<OrderStockScanBean> data) {
        refreshList();
        int success = 0;
        int error = 0;
        for (int i = 0; i < data.size(); i++) {
            OrderStockScanBean bean = data.get(i);
            if (bean.codeStatus == CodeBean.STATUS_CODE_SUCCESS) {
                success++;
            } else {
                error++;
            }
        }
        String details;
        if (error == 0) {
            details = "您成功校验了" + success + "条数据.";

        } else {
            details = "您成功校验了" + success + "条数据.\n其中失败" + error + "条";
        }
        CommonDialogUtil.showConfirmDialog(this,"数据校验", details, "知道了",null);
    }

    private void refreshList() {
        mViewModel.refreshListByProductId();
    }

    private void updateTotal(Long data) {
        long size = data == null ? 0 : data;
        Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
        mBindingView.layoutStockActionFooter.tvCodeAmount.setText(spanned);
    }

    /**
     * 扫码校验接口 后台返回码错误
     */
    private void showCodeErrorDialog(String code, String errorMsg) {
        mViewModel.deleteCode(code);
        removeItem(code);
        CommonDialogUtil.showConfirmDialog(this,"", errorMsg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                
            }
        });
    }

    private void removeItem(String code) {
        OrderStockOutScanCodeEntity entity = new OrderStockOutScanCodeEntity();
        entity.setOuterCode(code);
        mAdapter.notifyRemoveItem(entity);
        mViewModel.calculationTotal();
        mBindingView.invalidateAll();
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
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvOrderStockOutPda);
        } else {
            ScanCodeService.playError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case 1://反扫
                refreshList();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_input_code) {
            CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public boolean onInput(String input) {
                    AutoScanCodeUtils.autoScan(input);
                    return CommonDialog.OnButtonClickListener.super.onInput(input);
                }
            });
        } else if (id == R.id.tv_scan_back) {
            CommonOrderProductScanBackActivity.start(this, 1, productId, CommonOrderProductScanBackActivity.TYPE_STOCK_OUT);
        } else if (id == R.id.tv_sure) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mViewModel.checkScanCodeListCount()) {
            CommonDialogUtil.showSelectDialog(this,"有码未校验成功!", "未校验成功的码不会上传!",
                    "取消", "继续退出", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                        }

                        @Override
                        public void onRightClick() {
                            saveDataAndFinish();
                        }
                    });
        } else {
            saveDataAndFinish();
        }
    }

    private void saveDataAndFinish() {
        Intent intent = new Intent();
        intent.putExtra("productId", productId);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void start(Activity context, int requestCode, long productId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonOrderStockOutPDAActivity.class);
            intent.putExtra("productId", productId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
