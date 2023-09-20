package com.jgw.delingha.custom_module.delingha.order_stock_out.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.BuildConfig;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.adapter.OrderStockOutPDAListRecyclerAdapter;
import com.jgw.delingha.custom_module.delingha.order_stock_out.viewmodel.OrderStockOutPDAViewModel;
import com.jgw.delingha.databinding.ActivityOrderStockOutPdaBinding;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/23
 */
public class OrderStockOutPDAActivity extends BaseActivity<OrderStockOutPDAViewModel, ActivityOrderStockOutPdaBinding> {


    private OrderStockOutPDAListRecyclerAdapter mAdapter;
    private int mPosition;

    @Override
    protected void initView() {
        if (!TextUtils.equals(BuildConfig.BUILD_TYPE, "release")) {
            mBindingView.rvOrderStockInPda.setItemAnimator(null);
        }
        mBindingView.rvcOrderStockInPda.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("出库");
        setRight("重试");
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", -1);
        OrderStockOutDetailsBean.ListBean bean = MMKVUtils.getTempData(OrderStockOutDetailsBean.ListBean.class);
        mViewModel.setHeaderData(bean);
        mBindingView.layoutOrderStockOutHeader.setData(bean);

        mAdapter = new OrderStockOutPDAListRecyclerAdapter();
        mBindingView.rvOrderStockInPda.setAdapter(mAdapter);
        mAdapter.notifyRefreshList(bean.codeList);
        mViewModel.setDataList(mAdapter.getDataList());
        bean.codeList = mAdapter.getDataList();
        mViewModel.calculationTotal();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutStockActionFooter.tvScanBack)
                .addView(mBindingView.layoutStockActionFooter.tvSure)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void initLiveData() {
        mViewModel.getCheckOrderStockInCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateItemStatus(resource.getData());
                    break;
                case Resource.ERROR:
                    showCodeErrorDialog(resource.getData(), resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateTotal();
                    break;
                case Resource.ERROR:
                    break;
            }
        });
    }

    private void updateTotal() {
//        int size = mAdapter.getDataList().size();
//        Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
//        mBindingView.layoutStockActionFooter.tvCodeAmount.setText(spanned);
    }

    /**
     * 扫码校验接口 后台返回码错误
     */
    private void showCodeErrorDialog(OrderStockScanBean data, String errorMsg) {
        CommonDialogUtil.showConfirmDialog(this, "", errorMsg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                removeItem(data);
            }
        });
    }


    private void removeItem(OrderStockScanBean data) {
        List<OrderStockScanBean> dataList = mAdapter.getDataList();
        int index = dataList.indexOf(data);
        if (index == -1) {
            return;
        }
        mAdapter.notifyRemoveItem(index);
        mViewModel.calculationTotal();
    }

    private void updateItemStatus(OrderStockScanBean data) {
        List<OrderStockScanBean> dataList = mAdapter.getDataList();
        int index = dataList.indexOf(data);
        if (index == -1) {
            return;
        }
        dataList.set(index, data);
        mAdapter.notifyItemChanged(index);

        mBindingView.layoutOrderStockOutHeader.invalidateAll();
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
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvOrderStockInPda);
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
                int position = data.getIntExtra("position", -1);
                if (position == -1) {
                    return;
                }
                List<OrderStockScanBean> codeList = MMKVUtils.getTempDataList(OrderStockScanBean.class);
                mAdapter.notifyRefreshList(codeList);
                mBindingView.layoutOrderStockOutHeader.invalidateAll();

                updateTotal();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_scan_back) {
            OrderStockOutScanBackActivity.start(this, mPosition, 1, mAdapter.getDataList());
        } else if (id == R.id.tv_sure) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        saveDataAndFinish();
    }

    private void saveDataAndFinish() {
        List<OrderStockScanBean> temp = new ArrayList<>();
        for (int i = 0; i < mAdapter.getDataList().size(); i++) {
            OrderStockScanBean bean = mAdapter.getDataList().get(i);
            if (bean.codeStatus != CodeBean.STATUS_CODE_SUCCESS) {
                temp.add(bean);
            }
        }
        mAdapter.getDataList().removeAll(temp);
        MMKVUtils.saveTempData(mAdapter.getDataList());
        Intent intent = new Intent();
        intent.putExtra("position", mPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void start(Activity context, int position, int requestCode, OrderStockOutDetailsBean.ListBean bean) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(bean);
            Intent intent = new Intent(context, OrderStockOutPDAActivity.class);
            intent.putExtra("position", position);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
