package com.jgw.delingha.module.scan_back.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.databinding.ActivityCommonScanBackBinding;
import com.jgw.delingha.module.scan_back.adapter.CommonScanBackJsonAdapter;
import com.jgw.delingha.module.scan_back.viewmodel.CommonJsonScanBackViewModel;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class CommonJsonScanBackActivity extends BaseActivity<CommonJsonScanBackViewModel, ActivityCommonScanBackBinding> {
    private CommonScanBackJsonAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.scan_back_title));
        mBindingView.rvcCommonScanBack.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        List<CodeBean> list = MMKVUtils.getTempDataList(CodeBean.class);
        mAdapter = new CommonScanBackJsonAdapter();
        mAdapter.notifyRefreshList(list);
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvCommonScanBack.setAdapter(mAdapter);
        calculationTotal();
    }

    @Override
    public void initLiveData() {
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
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutScanBackFooter.tvDeleteAll, mBindingView.layoutScanBackFooter.tvReturn)
                .addOnClickListener()
                .submit();
        mAdapter.setOnItemClickListener(this);
    }


    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        if (CheckCodeUtils.checkCode(code)) {
            code = CheckCodeUtils.getMatcherDeliveryNumber(code);
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter);
        } else {
            ScanCodeService.playError();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        mAdapter.notifyRemoveItem(position);
        calculationTotal();
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

    private void onDeleteAll() {
        if (mAdapter.isEmpty()) {
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
    }

    public void updateCountView(long count) {
        mBindingView.layoutScanBackFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + count + "</font>条"));
    }

    private void calculationTotal() {
        mViewModel.calculationTotal();
    }

    @Override
    public void onBackPressed() {
        MMKVUtils.saveTempData(mAdapter.getDataList());
        Intent intent = new Intent();
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

    public static void start(Activity context, List<CodeBean> list, int requestCode) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(list);
            Intent intent = new Intent(context, CommonJsonScanBackActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
