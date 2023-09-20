package com.jgw.delingha.module.stock_return.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityStockReturnPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.stock_return.viewmodel.StockReturnPDAViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockReturnEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class StockReturnPDAActivity extends BaseActivity<StockReturnPDAViewModel, ActivityStockReturnPdaBinding> {

    private static final int TYPE_SETTING = 110;
    private static final int TYPE_SCAN_BACK = 111;

    private CodeEntityRecyclerAdapter<StockReturnEntity> mAdapter;
    private long mNewConfigId;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_return_pda_title));
        setRight(ResourcesUtils.getString(R.string.stock_in_PDA_title_right));
        mBindingView.rvcStockReturn.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        mViewModel.getConfigData(configId);

        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvStockReturn.setAdapter(mAdapter);
        mViewModel.getCodeCount();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getConfigurationLiveData().observe(this, configurationEntityResource -> {
            ConfigurationEntity entity = configurationEntityResource.getData();
            switch (configurationEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.setConfigurationEntity(entity);
                    if (TextUtils.isEmpty(entity.getStockHouseName())) {
                        mBindingView.layoutStockReturnHeader.llStoreHouse.setVisibility(View.GONE);
                    } else {
                        mBindingView.layoutStockReturnHeader.llStoreHouse.setVisibility(View.VISIBLE);
                        mBindingView.layoutStockReturnHeader.tvStoreHouseContent.setText(entity.getStockHouseName());
                    }
                    mBindingView.layoutStockReturnHeader.tvCustomerContent.setText(entity.getCustomerName());
                    mBindingView.layoutStockReturnHeader.tvWarehouseContent.setText(entity.getWareHouseName());
                    break;
                case Resource.ERROR:
                    break;
            }
        });

        mViewModel.getCountLiveData().observe(this, longResource -> {
            switch (longResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mBindingView.layoutStockReturnFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + longResource.getData() + "</font>条"));
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(longResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getStockReturnCodeLiveData().observe(this, v3CodeCheckReturnResultBeanResource -> {
            String code = v3CodeCheckReturnResultBeanResource.getData();
            switch (v3CodeCheckReturnResultBeanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.updateItemStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    updateItemStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    mViewModel.deleteListItem(code);
                    mViewModel.getRefreshListData();
                    CommonDialogUtil.showConfirmDialog(this,"", v3CodeCheckReturnResultBeanResource.getErrorMsg(), "知道了", new CommonDialog.OnButtonClickListener() {});
                    break;
                case Resource.NETWORK_ERROR:
                    mViewModel.updateItemStatus(code, CodeBean.STATUS_CODE_FAIL);
                    updateItemStatus(code, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });

        mViewModel.getLoadListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (mViewModel.getPage() == 1) {
                        mAdapter.notifyRefreshList(resource.getData());
                    } else {
                        loadMore(resource.getData());
                    }
                    mViewModel.getCodeCount();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRequestTaskIdLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.uploadData();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });

        mViewModel.getUploadLiveData().observe(this, uploadResultBeanResource -> {
            if (mViewModel.getIsUpdateCustomerId()) {
                mViewModel.getConfigData(mNewConfigId);
            }
            switch (uploadResultBeanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (mViewModel.getIsUpdateCustomerId()) {
                        showUploadToast(uploadResultBeanResource.getData());
                    } else {
                        showUploadFinishDialog(uploadResultBeanResource.getData());
                    }
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    if (mViewModel.getIsUpdateCustomerId()) {
                        ToastUtils.showToast("上传失败,数据暂存待执行");
                    } else {
                        ToastUtils.showToast("上传失败");
                    }
                    dismissLoadingDialog();
                    break;
            }
        });

    }

    private void loadMore(List<StockReturnEntity> data) {
        mAdapter.notifyAddListItem(data);
    }

    private void updateItemStatus(String outerCodeId, int status) {
        mAdapter.notifyRefreshItemStatus(outerCodeId, status);
    }

    private void showUploadToast(UploadResultBean data) {
        ToastUtils.showToast("您成功上传了" + data.success + "条数据.\n请稍后查看任务状态");
        mViewModel.setIsUpdateCustomerId(false);
        mAdapter.notifyRemoveListItem();
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
                MainActivity.start(StockReturnPDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(StockReturnPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_RETURN, StockReturnPDAActivity.this);
            }
        });
    }

    @Override
    protected void clickRight() {
        super.clickRight();
        StockReturnSettingActivity.start(this, mViewModel.getConfigId(), TYPE_SETTING);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutStockReturnFooter.tvInputCode,
                        mBindingView.layoutStockReturnFooter.tvScanBack,
                        mBindingView.layoutStockReturnFooter.tvSure)
                .addOnClickListener()
                .submit();
        mBindingView.rvStockReturn.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.loadMoreList();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_input_code) {//展示手动输入框
            CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public boolean onInput(String input) {
                    AutoScanCodeUtils.autoScan(input);
                    return CommonDialog.OnButtonClickListener.super.onInput(input);
                }
            });
        } else if (id == R.id.tv_scan_back) {//跳转到反扫界面
            if (mAdapter.getDataList().isEmpty()) {
                ToastUtils.showToast("请先扫码");
                return;
            }
            CommonScanBackActivity.start(this, TYPE_SCAN_BACK, CommonScanBackActivity.TYPE_RETURN, mViewModel.getConfigId());
        } else if (id == R.id.tv_sure) {//展示上传弹窗
            if (mAdapter.getDataList().isEmpty()) {
                ToastUtils.showToast("请先扫码!");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    if (mViewModel.hasVerifyingCode()) {
                        ToastUtils.showToast("还有码在校验中,请稍后再试");
                        return;
                    }
                    mViewModel.getTaskId();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TYPE_SETTING:
                    if (data == null) {
                        return;
                    }
                    long id = data.getLongExtra(ConstantUtil.CONFIG_ID, -1);
                    if (mViewModel.isUpdateUpload(id)) {
                        mNewConfigId = id;
                        onChangeConfig();
                    } else {
                        mViewModel.getConfigData(id);
                    }
                    break;
                case TYPE_SCAN_BACK:
                    mViewModel.getRefreshListData();
                    break;
            }
        }

    }

    public void onChangeConfig() {
        CommonDialogUtil.showSelectDialog(this,"修改了收货人将会清空列表", "是否上传之前数据\n或将其删除", "删除数据", "上传数据", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                mAdapter.notifyRemoveListItem();
                mViewModel.clearList();
                mViewModel.getConfigData(mNewConfigId);
                mViewModel.getCodeCount();
            }

            @Override
            public void onRightClick() {
                mViewModel.getTaskId();
            }
        });
    }

    /**
     * 扫码返回结果
     */
    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;
        scanCode = CheckCodeUtils.getMatcherDeliveryNumber(scanCode);
        if (!CheckCodeUtils.checkCode(scanCode) ) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", scanCode, "知道了", new CommonDialog.OnButtonClickListener() {});
        }else if (mViewModel.checkCodeExisted(scanCode)){
            onCodeError();
        }else {
            mViewModel.handleScanQRCode(scanCode, mAdapter, mBindingView.rvStockReturn);
            ScanCodeService.playSuccess();
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    /**
     * 按返回键时弹窗
     */
    @Override
    public void onBackPressed() {
        if (mAdapter.getDataList().isEmpty()) {
            MainActivity.start(this, 0);
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否退出当前退货操作?", "若退出,您所操作的数据将被放入待执行中.",
                "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick() {
                        MainActivity.start(StockReturnPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        mViewModel.getTaskId();
                    }
                });
    }

    /**
     * 注册EventBus
     */
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

    /**
     * 跳转函数 正式版本 含数据库
     */
    public static void start(Context context, Long configId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StockReturnPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }
}
