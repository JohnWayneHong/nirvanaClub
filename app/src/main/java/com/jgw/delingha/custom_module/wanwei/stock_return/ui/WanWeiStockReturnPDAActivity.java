package com.jgw.delingha.custom_module.wanwei.stock_return.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
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
import com.jgw.delingha.custom_module.wanwei.stock_return.viewmodel.WanWeiStockReturnPDAViewModel;
import com.jgw.delingha.databinding.ActivityWanweiStockReturnPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.WanWeiStockReturnEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;


public class WanWeiStockReturnPDAActivity extends BaseActivity<WanWeiStockReturnPDAViewModel, ActivityWanweiStockReturnPdaBinding> {

    private static final int TYPE_SETTING = 110;
    private static final int TYPE_SCAN_BACK = 111;

    private CodeEntityRecyclerAdapter<WanWeiStockReturnEntity> mAdapter;
    private long mNewConfigId;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_return_pda_title));
        setRight(ResourcesUtils.getString(R.string.stock_in_PDA_title_right));
        mBindingView.rvcWanweiStockReturn.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        if (configId == -1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvWanweiStockReturn.setAdapter(mAdapter);

        mViewModel.getConfigData(configId);
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
                    mBindingView.layoutWanweiStockReturnHeader.setData(entity);
                    break;
                case Resource.ERROR:
                    break;
            }
        });

        mViewModel.getCountLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    Map<String, Long> map = resource.getData();
                    Long codeNumber1 = map.get("codeNumber");
                    long codeNumber = codeNumber1 == null ? 0 : codeNumber1;
                    Long singleNumber1 = map.get("singleNumber");
                    long singleNumber = singleNumber1 == null ? 0 : singleNumber1;
                    mViewModel.setScanSingleNumber(singleNumber);
                    Spanned text = Html.fromHtml("采集数:<font color='#03A9F4'>" + codeNumber + "</font>" +
                            "<br/>" +
                            "子码数量:<font color='#03A9F4'>" + singleNumber + "</font>");
                    mBindingView.layoutStockReturnFooter.tvCodeAmount.setText(text);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getStockReturnCodeLiveData().observe(this, resource -> {
            CodeBean code = resource.getData();
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshItemStatus(code.outerCodeId, CodeBean.STATUS_CODE_SUCCESS);
                    mViewModel.getCodeCount();
                    break;
                case Resource.ERROR:
                    mViewModel.deleteListItem(code.outerCodeId);
                    mViewModel.getRefreshListData();
                    CommonDialogUtil.showConfirmDialog(this,"", resource.getErrorMsg(), "知道了", new CommonDialog.OnButtonClickListener() {});
                    break;
                case Resource.NETWORK_ERROR:
                    mViewModel.updateItemStatus(code.outerCodeId, CodeBean.STATUS_CODE_FAIL);
                    mAdapter.notifyRefreshItemStatus(code.outerCodeId, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });

        mViewModel.getRefreshListDataLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(listResource.getData());
                    mViewModel.getCodeCount();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getLoadMoreLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyAddListItem(resource.getData());
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
                MainActivity.start(WanWeiStockReturnPDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(WanWeiStockReturnPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_RETURN, MainActivity.mMainActivityContext);
            }
        });
    }

    @Override
    protected void clickRight() {
        super.clickRight();
        WanWeiStockReturnSettingActivity.start(this, mViewModel.getConfigId(), TYPE_SETTING);
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
        mBindingView.rvWanweiStockReturn.addOnScrollListener(new OnLoadMoreListener() {
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
            CommonScanBackActivity.start(this, TYPE_SCAN_BACK, CommonScanBackActivity.TYPE_WANWEI_STOCK_RETURN, mViewModel.getConfigId());
        } else if (id == R.id.tv_sure) {//展示上传弹窗
            if (mAdapter.getDataList().isEmpty()) {
                ToastUtils.showToast("请先扫码!");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    if (mViewModel.isVerifyingId()) {
                        ToastUtils.showToast("还有码在校验中,请稍后再试");
                        return;
                    }
                    preUpload();
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
                mViewModel.getCodeCount();
                mViewModel.getConfigData(mNewConfigId);
            }

            @Override
            public void onRightClick() {
                preUpload();
            }
        });
    }

    private void preUpload() {
        ConfigurationEntity config = mBindingView.layoutWanweiStockReturnHeader.getData();
        if (mViewModel.getSingleNumber() == config.getPlanNumber()) {
            mViewModel.getTaskId();
        } else {
            String details;
            if (mViewModel.getSingleNumber() < config.getPlanNumber()) {
                details = "当前扫码数量尚未满足计划数量，是否确定继续上传？";
            } else {
                details = "当前扫码数量已超出计划数量，是否确定继续上传？";
            }
            CommonDialogUtil.showSelectDialog(this,"提示", details,
                    "返回", "确认上传", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {

                        }

                        @Override
                        public void onRightClick() {
                            mViewModel.getTaskId();
                        }
                    });
        }
    }

    /**
     * 扫码返回结果
     */
    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        if (!TextUtils.isEmpty(code)) {
            code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        }
        if (mViewModel.checkCodeExisted(code)) {
            onCodeError();
        } else if (CheckCodeUtils.checkCode(code)) {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvWanweiStockReturn);
        } else {
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
            onCodeError();
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
                        MainActivity.start(WanWeiStockReturnPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        preUpload();
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
            Intent intent = new Intent(context, WanWeiStockReturnPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }
}
