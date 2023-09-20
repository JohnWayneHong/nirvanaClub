package com.jgw.delingha.module.exchange_goods.order.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.databinding.ActivityOrderExchangeGoodsScanBackBinding;
import com.jgw.delingha.module.exchange_goods.order.adapter.OrderExchangeGoodsScanBackAdapter;
import com.jgw.delingha.module.exchange_goods.order.viewmodel.OrderExchangeGoodsScanBackViewModel;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class OrderExchangeGoodsScanBackActivity extends BaseActivity<OrderExchangeGoodsScanBackViewModel, ActivityOrderExchangeGoodsScanBackBinding> {

    private int mIndex;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.scan_back_title));
        mBindingView.rvcStockIn.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        mIndex = intent.getIntExtra("index", -1);
        List<OrderExchangeGoodsCodeBean> list = JsonUtils.parseArray(json, OrderExchangeGoodsCodeBean.class);
        OrderExchangeGoodsScanBackAdapter adapter = mViewModel.initAdapter();
        mBindingView.rvStockIn.setAdapter(adapter);
        mViewModel.setCodeList(list);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutScanBackFooter.tvDeleteAll, mBindingView.layoutScanBackFooter.tvReturn)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void initLiveData() {
        mViewModel.getCodeCountLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + integer + "</font>条");
                mBindingView.layoutScanBackFooter.tvCodeAmount.setText(spanned);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_delete_all:
                CommonDialogUtil.showSelectDialog(this,"确定全部删除吗?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        mViewModel.clearList();
                    }
                });
                break;
            case R.id.tv_return:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("data", JsonUtils.toJsonString(mViewModel.getList()));
        intent.putExtra("index", mIndex);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(event.mCode);
        } else {
            ScanCodeService.playError();
        }
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


    public static void start(Activity context, @NonNull List<OrderExchangeGoodsCodeBean> list, int requestCode) {
        start(context, list, -1, requestCode);
    }

    public static void start(Activity context, @NonNull List<OrderExchangeGoodsCodeBean> list, int index, int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderExchangeGoodsScanBackActivity.class);
            intent.putExtra("json", JsonUtils.toJsonString(list));
            intent.putExtra("index", index);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
