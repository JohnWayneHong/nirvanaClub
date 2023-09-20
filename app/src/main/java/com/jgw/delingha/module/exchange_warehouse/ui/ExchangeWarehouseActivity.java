package com.jgw.delingha.module.exchange_warehouse.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
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
import com.jgw.delingha.databinding.ActivityExchangeWarehouseBinding;
import com.jgw.delingha.module.exchange_warehouse.viewmodel.ExchangeWarehouseViewModel;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ExchangeWarehouseConfigurationEntity;
import com.jgw.delingha.sql.entity.ExchangeWarehouseEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

public class ExchangeWarehouseActivity extends BaseActivity<ExchangeWarehouseViewModel, ActivityExchangeWarehouseBinding> {

    private static final int TYPE_SCAN_BACK =  111;
    private CodeEntityRecyclerAdapter<ExchangeWarehouseEntity> mAdapter;

    @Override
    protected void initView() {
        setTitle("调仓");
        setRight("手输");
        mBindingView.rvcExchangeWarehouse.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        mViewModel.getConfigData(configId);
        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvExchangeWarehouse.setAdapter(mAdapter);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getConfigurationEntityLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ExchangeWarehouseConfigurationEntity entity = resource.getData();
                    mViewModel.setConfigEntity(entity);
                    mBindingView.layoutExchangeWarehouseHeader.setData(entity);
                    mViewModel.getCodeCount();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
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
                    Spanned text = Html.fromHtml("采集数:<font color='#03A9F4'>" + codeNumber + "</font>" +
                            "<br/>" +
                            "子码数量:<font color='#03A9F4'>" + singleNumber + "</font>");
                    mBindingView.layoutExchangeWarehouseFooter.tvCodeAmount.setText(text);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                default:
            }
        });

        mViewModel.getCheckCodeLiveData().observe(this, resource -> {
            CodeBean code = resource.getData();
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.updateCode(code, mAdapter);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    CommonDialogUtil.showConfirmDialog(this,"", resource.getErrorMsg(), "知道了", new CommonDialog.OnButtonClickListener() {});
                    mViewModel.deleteCode(code.outerCodeId,mAdapter);
                    break;
                case Resource.NETWORK_ERROR:
                    mViewModel.updateErrorCode(code.outerCodeId, mAdapter);
                    break;
                default:
            }
            mViewModel.getCodeCount();
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
                default:
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
                default:
            }
        });

        mViewModel.getUploadLiveData().observe(this, uploadResultBeanResource -> {
            switch (uploadResultBeanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    showUploadFinishDialog(uploadResultBeanResource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(uploadResultBeanResource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
                default:
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

    private void loadMore(List<ExchangeWarehouseEntity> data) {
        int size = mAdapter.getDataList().size();
        mAdapter.getDataList().addAll(data);
        mAdapter.notifyItemRangeInserted(size, data.size());
    }

    @Override
    protected void clickRight() {
        super.clickRight();
        CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

           @Override
            public boolean onInput(String input) {
                AutoScanCodeUtils.autoScan(input);
                return CommonDialog.OnButtonClickListener.super.onInput(input);
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutExchangeWarehouseFooter.tvDeleteAll,
                        mBindingView.layoutExchangeWarehouseFooter.tvUpload)
                .addOnClickListener()
                .submit();
        mBindingView.rvExchangeWarehouse.addOnScrollListener(new OnLoadMoreListener() {
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
        if (id == R.id.tv_delete_all) {
            if (mAdapter.getDataList().isEmpty()) {
                ToastUtils.showToast("请先扫码");
                return;
            }
            CommonScanBackActivity.start(this, TYPE_SCAN_BACK, CommonScanBackActivity.TYPE_EXCHANGE_WAREHOUSE, mViewModel.getConfigId());
        } else if (id == R.id.tv_upload) {
            if (mAdapter.getDataList().isEmpty()) {
                ToastUtils.showToast("请先扫码!");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    mViewModel.getTaskId();
                }
            });
        }
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
                MainActivity.start(ExchangeWarehouseActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(ExchangeWarehouseActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_EXCHANGE_WAREHOUSE, MainActivity.mMainActivityContext);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode || data == null) {
            ToastUtils.showToast("数据返回失败");
            return;
        }
        if (requestCode == TYPE_SCAN_BACK) {
            mViewModel.getRefreshListData();
        }
    }

    /**
     * 扫码返回结果
     */
    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (!CheckCodeUtils.checkCode(code)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        }else if (mViewModel.checkCodeExisted(code)){
            onCodeError();
        }else {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvExchangeWarehouse);
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
        if (!mAdapter.getDataList().isEmpty()) {
            CommonDialogUtil.showSelectDialog(this,"是否退出当前调仓操作?", "若退出,您所操作的数据将被放入待执行中",
                    "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                            MainActivity.start(ExchangeWarehouseActivity.this, 0);
                        }

                        @Override
                        public void onRightClick() {
                            mViewModel.getTaskId();
                        }
                    });
        } else {
            MainActivity.start(this, 0);
        }
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
     * 跳转函数 离线版跳转
     */
    public static void start(Context context, long configId) {
        if (configId == -1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ExchangeWarehouseActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }
}
