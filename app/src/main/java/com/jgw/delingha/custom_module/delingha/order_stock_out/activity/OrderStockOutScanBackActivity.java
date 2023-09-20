package com.jgw.delingha.custom_module.delingha.order_stock_out.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.adapter.OrderStockOutScanBackRecyclerAdapter;
import com.jgw.delingha.databinding.ActivityCommonScanBackBinding;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/11/16
 * 盘点单据列表
 */
public class OrderStockOutScanBackActivity extends BaseActivity<BaseViewModel, ActivityCommonScanBackBinding> {

    private OrderStockOutScanBackRecyclerAdapter mAdapter;
    private int mPosition;

    @Override
    protected void initView() {
        mBindingView.rvcCommonScanBack.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("删除");
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", -1);
        List<OrderStockScanBean> list = MMKVUtils.getTempDataList(OrderStockScanBean.class);

        mAdapter = new OrderStockOutScanBackRecyclerAdapter();
        mBindingView.rvCommonScanBack.setAdapter(mAdapter);
        mAdapter.notifyRefreshList(list);
        calculationTotal();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutScanBackFooter.tvReturn)
                .addView(mBindingView.layoutScanBackFooter.tvDeleteAll)
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_delete_all) {
            if (mAdapter.getDataList().size() <= 0) {
                ToastUtils.showToast("没有码可以删除");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"确定全部删除吗?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    mAdapter.notifyRemoveListItem();
                    calculationTotal();
                }
            });
        } else if (id == R.id.tv_return) {
            onBackPressed();
        }
    }

    private void calculationTotal() {
        int size = mAdapter.getDataList().size();
        Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
        mBindingView.layoutScanBackFooter.tvCodeAmount.setText(spanned);
    }

    @Override
    public void onBackPressed() {
        MMKVUtils.saveTempData(mAdapter.getDataList());
        Intent intent = new Intent();
        intent.putExtra("position", mPosition);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            ScanCodeService.playSuccess();
            deleteItem(code);
        } else {
            ScanCodeService.playError();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        OrderStockScanBean bean = mAdapter.getDataList().get(position);
        deleteItem(bean.outerCodeId);
    }

    private void deleteItem(String code) {
        List<OrderStockScanBean> dataList = mAdapter.getDataList();
        int index = dataList.indexOf(new OrderStockScanBean(code));
        if (index == -1) {
            return;
        }
        dataList.remove(index);
        mAdapter.notifyItemRemoved(index);
        mAdapter.notifyItemRangeChanged(index, dataList.size());
        calculationTotal();
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

    public static void start(Activity context, int position, int requestCode, List<OrderStockScanBean> codeList) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(codeList);
            Intent intent = new Intent(context, OrderStockOutScanBackActivity.class);
            intent.putExtra("position", position);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
