package com.jgw.delingha.module.stock_out.base.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.InputScanCodeDialogBean;
import com.jgw.delingha.databinding.ActivityStockOutPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.stock_out.base.viewmodel.StockOutPDAViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.StockOutEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.view.dialog.input.InputScanCodeDialog;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by XiongShaoWu
 * on 2019/11/25
 */
public class StockOutPDAActivity extends BaseActivity<StockOutPDAViewModel, ActivityStockOutPdaBinding> {

    private CodeEntityRecyclerAdapter<StockOutEntity> mAdapter;

    public static final int REQUEST_CODE_EDIT_CONFIG = 1;
    public static final int REQUEST_CODE_SCAN_BACK = 2;
    public static final int REQUEST_CODE_CONFIRM = 3;
    private InputScanCodeDialog inputScanCodeDialog;

    @Override
    protected void initView() {
        mBindingView.rvcStockOut.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long config_id = getIntent().getLongExtra("config_id", -1);
        setTitle("出库");
        setRight("设置");
        mViewModel.getConfigData(config_id);
        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvStockOut.setAdapter(mAdapter);
        calculationTotal();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getRequestHeaderDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mBindingView.layoutStockOutHeader.setData(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("获取设置信息失败");
                    break;
            }
        });
        mViewModel.getCheckCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    CodeBean bean = resource.getData();
                    mViewModel.updateCodeStatus(bean);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    showCodeErrorDialog(resource.getData().outerCodeId, resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    mViewModel.updateErrorCode(resource.getData().outerCodeId);
                    break;
            }
        });
        mViewModel.getUpdateCodeInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCodeStatusItem(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getUpdateErrorCodeStatusLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCodeStatusItem(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateTotal(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getRefreshListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    refreshList(resource.getData());
                    calculationTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }

        });
        mViewModel.getLoadMoreLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    loadMore(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    private void loadMore(List<StockOutEntity> data) {
        int size = mAdapter.getDataList().size();
        mAdapter.getDataList().addAll(data);
        mAdapter.notifyItemRangeInserted(size, data.size());
    }

    private void refreshList(List<StockOutEntity> list) {
        //此处不使用Adapter的原子操作 因为清空集合后 Adapter也会持有一个空界面的item 如果执行清空操作会报错
        //为了简化逻辑清空操作时重新绑定数据源来刷新
        mAdapter.notifyRemoveListItem();
        if (list != null) {
            mAdapter.notifyRefreshList(list);
        }
    }

    private void updateTotal(Long data) {
        long size = data == null ? 0 : data;
        mBindingView.layoutStockOutFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
    }

    /**
     * 获取到码信息成功更新 或网络错误 码更新为错误状态
     */
    private void updateCodeStatusItem(StockOutEntity entity) {
        int index = mAdapter.getDataList().indexOf(entity);
        if (index != -1) {
            mAdapter.getDataList().set(index, entity);
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_CONFIG:
                    //修改配置返回
                    long config_id = data.getLongExtra(ConstantUtil.CONFIG_ID, -1);
                    if (config_id != -1) {
                        mViewModel.getConfigData(config_id);
                    }
                    break;
                case REQUEST_CODE_SCAN_BACK:
                case REQUEST_CODE_CONFIRM:
                    //上传界面返回 刷新界面
                    //数据库版反扫
                    mViewModel.refreshListByConfigId();
                    break;
                default:
            }
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;
        if (!CheckCodeUtils.checkCode(scanCode)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this, "身份码不存在!", scanCode, "知道了", null);
            return;
        }
        if (inputScanCodeDialog != null && inputScanCodeDialog.isShowing()) {
            inputScanCode(scanCode);
            return;
        }
        realScanCode(scanCode);
    }

    private void inputScanCode(String scanCode) {
        InputScanCodeDialogBean data = inputScanCodeDialog.getData();
        if (!CheckCodeUtils.checkCode(data.getInput1())) {
            data.setInput1(scanCode);
        } else {
            data.setInput2(scanCode);
        }
    }

    private void realScanCode(String scanCode) {
        scanCode = CheckCodeUtils.getMatcherDeliveryNumber(scanCode);
        if (mViewModel.checkCodeExisted(scanCode)) {
            onCodeError();
        } else {
            mViewModel.requestCodeInfo(scanCode, mAdapter, mBindingView.rvStockOut);
            ScanCodeService.playSuccess();
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    /**
     * 扫码校验接口 后台返回码错误
     */
    private void showCodeErrorDialog(String code, String errorMsg) {
        mViewModel.deleteCode(code);
        mViewModel.refreshListByConfigId();
        CommonDialogUtil.showConfirmDialog(this, "", errorMsg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                CommonDialog.OnButtonClickListener.super.onRightClick();
            }
        });
    }

    private void calculationTotal() {
        mViewModel.calculationTotal();
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

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutStockOutFooter.tvScanBack)
                .addView(mBindingView.layoutStockOutFooter.tvInputCode)
                .addView(mBindingView.layoutStockOutFooter.tvSure)
                .submit();
        mBindingView.rvStockOut.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.loadMoreList();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.tv_scan_back) {
            jumpScanBack();
        } else if (id == R.id.tv_input_code) {
            showInputDialog();
        } else if (id == R.id.tv_sure) {
            uploadCodes();
        }
    }

    private Disposable disposable;

    private void showInputDialog() {
        inputScanCodeDialog = InputScanCodeDialog.newInstance(this);
        inputScanCodeDialog.setCancelable(false);
        inputScanCodeDialog.setCustomTitle("身份码")
                .setLeftButtonStr("取消")
                .setRightButtonStr("确定")
                .setOnButtonClickListener(new InputScanCodeDialog.OnButtonClickListener() {
                    @Override
                    public boolean onInput(String input) {
                        return AutoScanCodeUtils.checkAutoScanCode(inputScanCodeDialog.getData());
                    }

                    @Override
                    public void onRightClick() {
                        inputScanCodeDialog.dismiss();
                        AutoScanCodeUtils.startAutoScan(inputScanCodeDialog.getData(),new CustomObserver<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                super.onSubscribe(d);
                                disposable = d;
                            }

                            @Override
                            public void onNext(String s) {
                                EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(s));
                            }
                        });
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    protected void clickRight() {
        StockOutSettingActivity.start(this, mViewModel.getConfigId(), 1);
    }

    private void jumpScanBack() {
        if (codesIsEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        CommonScanBackActivity.start(this, 2, CommonScanBackActivity.TYPE_OUT, mViewModel.getConfigId());
    }

    private boolean codesIsEmpty() {
        return mAdapter.isEmpty();
    }

    private void uploadCodes() {
        if (codesIsEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        if (mViewModel.checkSuccessEmpty()) {
            ToastUtils.showToast("当前所有数据都未校验成功,请稍后前往待执行列表进行上传");
            return;
        }
        if (mViewModel.checkErrorCode()) {
            ToastUtils.showToast("未验证成功的码将放入待执行中");
        }
        StockOutConfirmActivity.start(REQUEST_CODE_CONFIRM, this, mViewModel.getConfigId());
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.getDataList().isEmpty()) {
            MainActivity.start(this, 0);
            return;
        }
        CommonDialogUtil.showSelectDialog(this, "是否退出当前出库操作?", "若退出,您所操作的数据将被放入待执行中.",
                "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick() {
                        MainActivity.start(StockOutPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        uploadCodes();
                    }
                });
    }



    public static void start(Activity context, long config_id) {
        if (config_id == -1) {
            ToastUtils.showToast("获取配置信息失败");
            return;
        }
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockOutPDAActivity.class);
            intent.putExtra("config_id", config_id);
            context.startActivity(intent);
        }
    }
}
