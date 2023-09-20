package com.jgw.delingha.module.exchange_goods.order.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.databinding.ActivityOrderExchangeGoodsScanBinding;
import com.jgw.delingha.module.exchange_goods.order.adapter.OrderExchangeGoodsScanRecyclerAdapter;
import com.jgw.delingha.module.exchange_goods.order.viewmodel.OrderExchangeGoodsScanViewModel;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/10
 */
public class OrderExchangeGoodsScanActivity extends BaseActivity<OrderExchangeGoodsScanViewModel, ActivityOrderExchangeGoodsScanBinding> {

    private int mIndex;

    @Override
    protected void initView() {
        mBindingView.rvcOrderExchangeGoodsScan.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("调货");
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        OrderExchangeGoodsDetailsBean.ProductsBean bean = JsonUtils.parseObject(data, OrderExchangeGoodsDetailsBean.ProductsBean.class);
        mBindingView.layoutExchangeGoodsHeader.setData(bean);

        OrderExchangeGoodsScanRecyclerAdapter adapter = mViewModel.initAdapter(bean);
        mBindingView.rvOrderExchangeGoodsScan.setAdapter(adapter);
        mViewModel.updateCountView();
        mIndex = intent.getIntExtra("index", -1);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutStockOutFooter.tvInputCode)
                .addView(mBindingView.layoutStockOutFooter.tvScanBack)
                .addView(mBindingView.layoutStockOutFooter.tvSure)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void initLiveData() {
        mViewModel.getCodeCountLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + integer + "</font>条");
                mBindingView.layoutStockOutFooter.tvCodeAmount.setText(spanned);
                OrderExchangeGoodsDetailsBean.ProductsBean headerData = mBindingView.layoutExchangeGoodsHeader.getData();
                headerData.scanSingleCodeNumber = integer;
                mBindingView.layoutExchangeGoodsHeader.setData(headerData);
            }
        });

        mViewModel.getCodeInfoLiveData().observe(this, new Observer<Resource<OrderExchangeGoodsCodeBean>>() {
            @Override
            public void onChanged(Resource<OrderExchangeGoodsCodeBean> bean) {
                mViewModel.updateCodeStatus(bean);
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
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (!CheckCodeUtils.checkCode(code)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        } else if (mViewModel.checkCodeExisted(code)) {
            onCodeError();
        } else {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code);
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    List<OrderExchangeGoodsCodeBean> list = null;
                    if (data != null) {
                        String json = data.getStringExtra("data");
                        list = JsonUtils.parseArray(json, OrderExchangeGoodsCodeBean.class);
                    }
                    mViewModel.onScanBackResult(list);
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_input_code:
                CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public boolean onInput(String input) {
                        EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(input));
                        return CommonDialog.OnButtonClickListener.super.onInput(input);
                    }
                });
                break;
            case R.id.tv_scan_back:
                if (!mViewModel.getList().isEmpty()) {
                    OrderExchangeGoodsScanBackActivity.start(this, mViewModel.getList(), 1);
                } else {
                    ToastUtils.showToast("请先扫码");
                }
                break;
            case R.id.tv_sure:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!mViewModel.checkScanCodeListStatus()) {
            ToastUtils.showToast("还有码在校验中,请稍候");
            return;
        }
        if (!mViewModel.checkScanCodeListCount()) {
            CommonDialogUtil.showSelectDialog(this,"扫码数量不满足订单要求！", "是否要继续？",
                    "继续扫码", "确定保存", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                        }

                        @Override
                        public void onRightClick() {
                            finishAndSaveData();
                        }
                    });
        } else {
            ToastUtils.showToast("扫码数量已满足!");
            finishAndSaveData();
        }

    }

    private void finishAndSaveData() {
        Intent intent = new Intent();
        intent.putExtra("data", JsonUtils.toJsonString(mViewModel.getList()));
        intent.putExtra("index", mIndex);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void start(Activity context, OrderExchangeGoodsDetailsBean.ProductsBean bean, int index, int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, OrderExchangeGoodsScanActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            intent.putExtra("index", index);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
