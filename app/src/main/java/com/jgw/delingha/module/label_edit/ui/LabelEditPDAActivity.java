package com.jgw.delingha.module.label_edit.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;

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
import com.jgw.delingha.databinding.ActivityLabelEditPdaBinding;
import com.jgw.delingha.module.label_edit.viewmodel.LabelEditPDAViewModel;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.LabelEditEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/10/12
 */
public class LabelEditPDAActivity extends BaseActivity<LabelEditPDAViewModel, ActivityLabelEditPdaBinding> {


    private CodeEntityRecyclerAdapter<LabelEditEntity> mAdapter;
    private static final int REQUEST_CODE_SCAN_BACK = 101;

    @Override
    protected void initView() {
        mBindingView.rvcLabelEdit.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        if (configId == -1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        setTitle("标识纠错");
        mViewModel.initHeaderData(configId);

        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvLabelEdit.setAdapter(mAdapter);
        calculationTotal();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getRequestHeaderDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity entity = resource.getData();
                    mBindingView.layoutLabelEditHeader.setData(entity);
                    mViewModel.setConfigInfo(entity);
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
                    mViewModel.calculationTotal();
                    break;
                case Resource.NETWORK_ERROR:
                    //无网络校验
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
                    mViewModel.calculationTotal();
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
        mViewModel.getRequestTaskIdLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.uploadCodes();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("上传失败");
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
                    ToastUtils.showToast("上传失败");
                    dismissLoadingDialog();
                    break;
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
                MainActivity.start(LabelEditPDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(LabelEditPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_LABEL_EDIT, MainActivity.mMainActivityContext);
            }
        });
    }

    private void loadMore(List<LabelEditEntity> data) {
        mAdapter.notifyAddListItem(data);
    }

    private void refreshList(List<LabelEditEntity> list) {
        mAdapter.notifyRefreshList(list);
    }

    private void updateTotal(Long data) {
        long size = data == null ? 0 : data;
        mBindingView.layoutLabelEditFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
    }

    /**
     * 获取到码信息成功更新 或网络错误 码更新为错误状态
     */
    private void updateCodeStatusItem(LabelEditEntity entity) {
        mAdapter.notifyRefreshItem(entity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN_BACK) {//数据库版反扫
                mViewModel.refreshListByConfigId();
            }
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (!CheckCodeUtils.checkCode(code)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        } else if (mViewModel.checkCodeExisted(code)) {
            onCodeError();
        } else {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvLabelEdit);
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
        CommonDialogUtil.showConfirmDialog(this,"", errorMsg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                CommonDialog.OnButtonClickListener.super.onRightClick();
            }
        });
    }

    private void calculationTotal() {
        mViewModel.calculationTotal();
    }

    private void showInputDialog() {
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

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutLabelEditFooter.tvScanBack)
                .addView(mBindingView.layoutLabelEditFooter.tvInputCode)
                .addView(mBindingView.layoutLabelEditFooter.tvSure)
                .submit();
        mBindingView.rvLabelEdit.addOnScrollListener(new OnLoadMoreListener() {
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

    private void jumpScanBack() {
        if (codesIsEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        CommonScanBackActivity.start(this, REQUEST_CODE_SCAN_BACK, CommonScanBackActivity.TYPE_LABEL_EDIT, mViewModel.getConfigId());
    }

    private boolean codesIsEmpty() {
        return mAdapter.getDataList().isEmpty();
    }

    private void uploadCodes() {
        mViewModel.getTaskId();
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.getDataList().isEmpty()) {
            MainActivity.start(this, 0);
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否退出当前出库操作?", "若退出,您所操作的数据将被放入待执行中.",
                "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick() {
                        MainActivity.start(LabelEditPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        uploadCodes();
                    }
                });
    }

    public static void start(Activity context, long configId) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, LabelEditPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }


}
