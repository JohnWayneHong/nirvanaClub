package com.jgw.delingha.module.packaging.stock_in_packaged.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
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
import com.jgw.delingha.databinding.ActivityStockInPackagedPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.packaging.stock_in_packaged.viewmodel.StockInPackagedPDAViewModel;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.StockInPackagedEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class StockInPackagedPDAActivity extends BaseActivity<StockInPackagedPDAViewModel, ActivityStockInPackagedPdaBinding> {

    private static final int TYPE_SCAN_BACK = 111;
    private CodeEntityRecyclerAdapter<StockInPackagedEntity> mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.stock_in_packaged_PDA_title));
        mBindingView.rvcStockIn.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        mViewModel.getConfigData(configId);
        mViewModel.getCount();
        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvStockIn.setAdapter(mAdapter);
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getConfigurationEntityLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity entity = resource.getData();
                    mViewModel.setConfigIdEntity(entity);
                    mBindingView.layoutStockInHeader.setData(entity);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCountLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    calculationTotal(resource);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCheckCodeLiveData().observe(this, resource -> {
            String code = resource.getData().outerCodeId;
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.updateCodeSuccess(code);
                    updateItemStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    mViewModel.removeCode(code);
                    mViewModel.refreshListData();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    updateItemStatus(code, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });

        mViewModel.getRefreshListDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<StockInPackagedEntity> entities = resource.getData();
                    mAdapter.notifyRefreshList(entities);
                    mViewModel.getCount();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getLoadMoreListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<StockInPackagedEntity> entities = resource.getData();
                    mAdapter.notifyAddListItem(entities);
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

        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    showUploadFinishDialog(resource.getData());
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
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

    private void calculationTotal(Resource<Long> resource) {
        long size = resource.getData();
        mBindingView.layoutStockInFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
    }

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
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvStockIn);
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    private void updateItemStatus(String code, int status) {
        mAdapter.notifyRefreshItemStatus(code,status);
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
            CommonScanBackActivity.start(this, TYPE_SCAN_BACK, CommonScanBackActivity.TYPE_IN_PACKAGED, mViewModel.getConfigId());
        } else if (id == R.id.tv_sure) {
            tryUpload();
        }
    }

    public void tryUpload() {
        if (mViewModel.checkCode()) {
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

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
                MainActivity.start(StockInPackagedPDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(StockInPackagedPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_IN, StockInPackagedPDAActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == TYPE_SCAN_BACK) {// 反扫
            mViewModel.refreshListData();
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
                        MainActivity.start(StockInPackagedPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        tryUpload();
                    }
                });
    }


    public void showInputDialog() {
        CommonDialogUtil.showInputDialog(this,"身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

           @Override
            public boolean onInput(String input) {
                AutoScanCodeUtils.autoScan(input);
                return CommonDialog.OnButtonClickListener.super.onInput(input);
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
            Intent intent = new Intent(context, StockInPackagedPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }
}
