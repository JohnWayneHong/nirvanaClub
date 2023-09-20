package com.jgw.delingha.module.scan_back.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonScanBackBinding;
import com.jgw.delingha.module.scan_back.adapter.CommonScanBackAdapter;
import com.jgw.delingha.module.scan_back.viewmodel.CommonScanBackViewModel;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class CommonScanBackActivity extends BaseActivity<CommonScanBackViewModel, ActivityCommonScanBackBinding> {
    public static final int TYPE_IN = 300;
    public static final int TYPE_OUT = 301;
    public static final int TYPE_RETURN = 302;
    public static final int TYPE_IN_PACKAGED = 303;
    public static final int TYPE_STOCK_OUT_FAST = 304;
    public static final int TYPE_EXCHANGE_WAREHOUSE = 305;
    public static final int TYPE_EXCHANGE_GOODS = 306;
    public static final int TYPE_LABEL_EDIT = 307;
    public static final int TYPE_DISASSEMBLE_ALL = 308;
    public static final int TYPE_WANWEI_STOCK_OUT = 309;
    public static final int TYPE_WANWEI_STOCK_RETURN = 310;
    public static final int TYPE_GANGBEN_STOCK_RETURN = 311;

    public static final int TYPE_DELINGHA_BREED_RETURN = 312;
    /**
     * 单个拆解
     */
    public static final int TYPE_DISASSEMBLE_SINGLE = 311;
    /**
     * 整组拆解
     */
    public static final int TYPE_DISASSEMBLE_GROUP = 312;

    private CommonScanBackAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.scan_back_title));
        mBindingView.rvcCommonScanBack.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        boolean fromConfig = intent.getBooleanExtra("fromConfig", false);
        int type = intent.getIntExtra("type", -1);
        if (fromConfig) {
            long configId = intent.getLongExtra(ConstantUtil.CONFIG_ID, -1);
            if (type == -1 || configId == -1) {
                ToastUtils.showToast("初始化数据失败");
                return;
            }
            mViewModel.setDatabaseDataByConfigId(type, configId);
        } else {
            if (type == -1) {
                ToastUtils.showToast("初始化数据失败");
                return;
            }
            mViewModel.setDatabaseDataByType(type);
        }
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
                        List<BaseCodeEntity> data = resource.getData();
                        mAdapter.notifyRefreshList(data);
                        mBindingView.rvCommonScanBack.scrollToPosition(0);
                    } else {
                        mAdapter.notifyAddListItem(resource.getData());
                    }
                    calculationTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
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
                default:
            }
        });
        mViewModel.getDeleteCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRemoveItem(resource.getData()[0]);
                    if (resource.getData()[1] != null) {
                        mAdapter.addAndNotifyLastItem(resource.getData()[1]);
                    }
                    calculationTotal();
                    ToastUtils.showToast("删除成功");
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });
        mViewModel.getDeleteAllLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.refreshList();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
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
        CommonDialogUtil.showSelectDialog(this,"确定全部删除吗?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

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

    public static void start(Activity context, int requestCode, int type, long configId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonScanBackActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            intent.putExtra("type", type);
            intent.putExtra("fromConfig", true);
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void start(Activity context, int requestCode, int type) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonScanBackActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("fromConfig", false);
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void start(Fragment fragment, int requestCode, int type) {
        if (fragment == null || fragment.getActivity() == null) {
            return;
        }
        FragmentActivity context = fragment.getActivity();
        Intent intent = new Intent(context, CommonScanBackActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("fromConfig", false);
        context.startActivityForResult(intent, requestCode);
    }
}
