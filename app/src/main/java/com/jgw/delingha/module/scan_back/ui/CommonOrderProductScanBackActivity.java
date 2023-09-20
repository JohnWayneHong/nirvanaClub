package com.jgw.delingha.module.scan_back.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.databinding.ActivityCommonScanBackBinding;
import com.jgw.delingha.module.scan_back.adapter.CommonScanBackAdapter;
import com.jgw.delingha.module.scan_back.viewmodel.CommonOrderProductScanBackViewModel;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CommonOrderProductScanBackActivity extends BaseActivity<CommonOrderProductScanBackViewModel, ActivityCommonScanBackBinding> {

    public static final int TYPE_STOCK_IN = 300;
    public static final int TYPE_STOCK_OUT = 301;
    public static final int TYPE_EXCHANGE_WAREHOUSE_OUT = 302;
    public static final int TYPE_EXCHANGE_WAREHOUSE_IN = 303;
    private CommonScanBackAdapter mAdapter;
    private long productId;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.scan_back_title));
        mBindingView.rvcCommonScanBack.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();

        int type = intent.getIntExtra("type", -1);
        productId = intent.getLongExtra("productId", -1);
        if (type == -1 || productId == -1) {
            ToastUtils.showToast("初始化数据失败");
            return;
        }
        mViewModel.setDatabaseDataById(type, productId);

        mAdapter = new CommonScanBackAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvCommonScanBack.setAdapter(mAdapter);
        calculationTotal();

        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        mViewModel.getCodeListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (mViewModel.getCurrentPage() == 1) {
                        mAdapter.notifyRemoveListItem();
                    }
                    mAdapter.notifyAddListItem(resource.getData());
                    calculationTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCountView(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getDeleteCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mAdapter.notifyRemoveItem(new CodeBean(resource.getData()));
                    calculationTotal();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getDeleteAllLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mViewModel.refreshList();
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
        ClickUtils.register(this)
                .addView(mBindingView.layoutScanBackFooter.tvDeleteAll, mBindingView.layoutScanBackFooter.tvReturn)
                .addOnClickListener()
                .submit();
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvCommonScanBack.addOnScrollListener(new com.jgw.common_library.listener.OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String outerCodeId = mAdapter.getDataList().get(position).getCode();
        mViewModel.handleScanQRCode(outerCodeId);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_delete_all) {
            onDeleteAll();
        } else if (id == R.id.tv_return) {
            onBackPressed();
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        if (CheckCodeUtils.checkCode(code)) {
            code = CheckCodeUtils.getMatcherDeliveryNumber(code);
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code);
        } else {
            ScanCodeService.playError();
        }
    }

    private void onDeleteAll() {
        if (mAdapter.isEmpty()) {
            ToastUtils.showToast("没有码可以删除");
            return;
        }
        CommonDialogUtil.showSelectDialog(this, "确定全部删除吗?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {


            @Override
            public void onRightClick() {
                mViewModel.deleteAll();
            }
        });
    }

    public void updateCountView(long count) {
        mBindingView.layoutScanBackFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + count + "</font>条"));
    }

    private void calculationTotal() {
        mViewModel.calculationTotal();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("productId", productId);
        setResult(RESULT_OK, intent);
        finish();
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

    public static void start(Activity context, int requestCode, long productId, int type) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonOrderProductScanBackActivity.class);
            intent.putExtra("productId", productId);
            intent.putExtra("type", type);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
