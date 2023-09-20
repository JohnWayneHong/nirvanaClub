package com.jgw.delingha.module.stock_in.base.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InputScanCodeDialogBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityStockInPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.stock_in.base.viewmodel.StockInPDAViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockInEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.view.dialog.input.InputScanCodeDialog;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.disposables.Disposable;


public class StockInPDAActivity extends BaseActivity<StockInPDAViewModel, ActivityStockInPdaBinding> {

    private static final int TYPE_SETTING = 110;
    private static final int TYPE_SCAN_BACK = 111;
    private CodeEntityRecyclerAdapter<StockInEntity> mAdapter;
    private InputScanCodeDialog inputScanCodeDialog;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_in_PDA_title));
        setRight(ResourcesUtils.getString(R.string.setting));
        mBindingView.rvcStockIn.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        long configId = intent.getLongExtra(ConstantUtil.CONFIG_ID, -1);
        mViewModel.getConfigData(configId);
        mViewModel.getCount();
        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvStockIn.setAdapter(mAdapter);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getConfigurationEntityLiveData().observe(this, configurationEntityResource -> {
            switch (configurationEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity entity = configurationEntityResource.getData();
                    mBindingView.layoutStockInHeader.setData(entity);
                    mViewModel.setConfigInfo(entity);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(configurationEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCountLiveData().observe(this, integerResource -> {
            switch (integerResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long size = integerResource.getData();
                    mBindingView.layoutStockInFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(integerResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getLoadMoreListLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyAddListItem(listResource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRefreshListDataLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.resetPage(listResource.getData());
                    mAdapter.notifyRefreshList(listResource.getData());
                    mViewModel.getCount();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRequestTaskIdLiveData().observe(this, uploadResultBeanResource -> {
            switch (uploadResultBeanResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.uploadData();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(uploadResultBeanResource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getUploadLiveData().observe(this, uploadResultBeanResource -> {
            switch (uploadResultBeanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    StockInPDAActivity.this.showUploadFinishDialog(uploadResultBeanResource.getData());
                    StockInPDAActivity.this.dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(uploadResultBeanResource.getErrorMsg());
                    StockInPDAActivity.this.dismissLoadingDialog();
                    break;
            }
        });
    }

    @Override
    protected void clickRight() {
        StockInSettingActivity.start(this, mViewModel.getConfigId(), TYPE_SETTING);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutStockInFooter.tvInputCode,
                        mBindingView.layoutStockInFooter.tvScanBack, mBindingView.layoutStockInFooter.tvSure)
                .addOnClickListener()
                .submit();
        mBindingView.rvStockIn.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
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
            mViewModel.handleScanQRCodeCommon(scanCode, mAdapter, mBindingView.rvStockIn);
            ScanCodeService.playSuccess();
        }
    }
    public void onCodeError() {
        ScanCodeService.playError();
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
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_input_code) {
            showInputDialog();
        } else if (id == R.id.tv_scan_back) {
            if (mViewModel.checkEmptyCode()) {
                return;
            }
            CommonScanBackActivity.start(this, TYPE_SCAN_BACK, CommonScanBackActivity.TYPE_IN, mViewModel.getConfigId());
        } else if (id == R.id.tv_sure) {
            uploadClick();
        }
    }

    public void uploadClick() {
        if (mViewModel.checkCode()) {
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {

            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                mViewModel.getTaskId();
            }
        });
    }

    private void showUploadFinishDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";
        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showSelectDialog(this,"数据上传成功", details, "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(StockInPDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(StockInPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_IN, StockInPDAActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case TYPE_SETTING:
                // 设置
                mViewModel.getConfigData(data.getLongExtra("sqlId", -1));
                break;
            case TYPE_SCAN_BACK:
                // 反扫
                mViewModel.getRefreshListData();
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.getDataList().isEmpty()) {
            MainActivity.start(this, 0);
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否退出当前入库操作?", "若退出,您所操作的数据将被放入待执行中.",
                "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick() {
                        MainActivity.start(StockInPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        uploadClick();
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

    public static void start(Context context, long configId) {
        if (configId==-1){
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockInPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }
}
