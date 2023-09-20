package com.jgw.delingha.module.stock_in.package_stock_in.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

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
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityPackageStockInPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonPackageScanBackActivity;
import com.jgw.delingha.module.stock_in.package_stock_in.viewmodel.PackageStockInPDAViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.PackageStockInEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * author : xsw
 * data : 2020/4/27
 * description : 装箱入库扫码界面
 */
public class PackageStockInPDAActivity extends BaseActivity<PackageStockInPDAViewModel, ActivityPackageStockInPdaBinding> {

    private static final int TYPE_SCAN_BACK = 101;
    private CodeEntityRecyclerAdapter<PackageStockInEntity> mAdapter;
    private boolean onFullBox;

    @Override
    protected void initView() {
        setTitle("包装入库");
        setRight("输入");
        mBindingView.rvcPackageStockIn.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        if (configId == -1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        mViewModel.getConfig(configId);

        mAdapter = new CodeEntityRecyclerAdapter<>();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvPackageStockIn.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == TYPE_SCAN_BACK) {
                String boxCode = mViewModel.getCurrentBoxCode();
                if (mViewModel.isRepeatBoxCode(boxCode)) {
                    mViewModel.refreshCurrentBoxList();
                } else {
                    updateBoxCodeStatus(boxCode, Resource.ERROR);
                    mAdapter.notifyRemoveListItem();
                    if (!TextUtils.isEmpty(boxCode)) {
                        mViewModel.resetBoxCode();
                        mViewModel.removeBoxCode(boxCode);
                    }
                    mViewModel.getCountTotal();
                }
            }
        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getConfigLiveData().observe(this, PackageConfigEntityResource -> {
            switch (PackageConfigEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.setConfigEntity(PackageConfigEntityResource.getData());
                    setConfigEntity(mViewModel.getConfigEntity());
                    mViewModel.getCountTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(PackageConfigEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCountTotalLiveData().observe(this, longResource -> {
            switch (longResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCountTotal(longResource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(longResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCurrentCountLiveData().observe(this, aLong -> updateCodeCountView());

        mViewModel.getCheckBoxCodeLiveData().observe(this, stringResource -> {
            String code = stringResource.getData();
            if (mViewModel.isCurrentBoxCode(code)) {
                updateBoxCodeStatus(code, stringResource.getLoadingStatus());
            }
            switch (stringResource.getLoadingStatus()) {
                case Resource.LOADING:
                    mViewModel.getCountTotal();
                    break;
                case Resource.SUCCESS:
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    showBoxCodeErrorDialog(code, stringResource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });

        mViewModel.getCheckCodeLiveData().observe(this, packageStockInEntityResource -> {
            PackageStockInEntity entity = packageStockInEntityResource.getData();
            String code = entity.getOuterCode();
            if (mViewModel.isCurrentBoxCode(entity.getParentOuterCodeId())) {
                int status = packageStockInEntityResource.getLoadingStatus() == Resource.SUCCESS ? CodeBean.STATUS_CODE_SUCCESS : CodeBean.STATUS_CODE_FAIL;
                updateCodeStatus(entity.getOuterCode(), status);
            }
            switch (packageStockInEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    showCodeErrorDialog(code, packageStockInEntityResource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });

        mViewModel.getOnFullBoxLiveData().observe(this, aLong -> {
            mAdapter.notifyRemoveListItem();
            updateBoxCodeStatus(mViewModel.getCurrentBoxCode(), Resource.ERROR);
            mViewModel.resetBoxCode();
            mViewModel.getCountTotal();

            onFullBox = true;
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this, "亲,该箱已装满!", "该箱码数据存在'查看/删除里'", "确定", new CommonDialog.OnButtonClickListener() {
                @Override
                public void onRightClick() {
                    onFullBox = false;
                }
            });
        });

        mViewModel.getShowBoxListLiveData().observe(this, packageStockInEntityResource -> {
            switch (packageStockInEntityResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    //箱码对象储存了子码列表
                    updateShowBoxList(packageStockInEntityResource);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(packageStockInEntityResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRefreshBoxListLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(listResource.getData());
                    updateCodeCountView();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getTryUploadLiveData().observe(this, stringResource -> {
            switch (stringResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    //箱码对象储存了子码列表
                    String notFullBoxCode = stringResource.getData();
                    if (TextUtils.isEmpty(notFullBoxCode)) {
                        mViewModel.getTaskId();
                    } else {
                        showNotFullBoxDialog(notFullBoxCode);
                    }
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(stringResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRequestTaskIdLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.realUploadCode();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    ToastUtils.showToast("网络连接错误，请稍后再试");
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
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    private void showNotFullBoxDialog(String boxCode) {
        String firstNumberName = mViewModel.getFirstNumberName();
        String lastNumberName = mViewModel.getLastNumberName();
        String title = "该" + firstNumberName + "码下的" + lastNumberName + "码还未满\n请先填满";
        CommonDialogUtil.showConfirmDialog(this, title, boxCode, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {

            }
        });
    }

    public void setConfigEntity(PackageConfigEntity entity) {
        mBindingView.layoutPackageStockInHeader.setData(entity);
        boolean empty = TextUtils.isEmpty(entity.getProductBatchName());
        mBindingView.layoutPackageStockInHeader.llProductBatch.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    public void updateCountTotal(long boxCount) {
        updateCodeCountView();
        updateBoxCountView(boxCount);
    }

    public void updateCodeCountView() {
        Spanned codeCount = mViewModel.getCodeCount();
        mBindingView.layoutPackageStockInFooter.tvCodeAmount.setText(codeCount);
    }

    public void updateBoxCountView(long boxCount) {
        Spanned boxCodeCount = mViewModel.getBoxCodeCount(boxCount);
        mBindingView.layoutPackageStockInFooter.tvBoxAmount.setText(boxCodeCount);
    }

    private void updateBoxCodeStatus(String code, int status) {
        int boxCodeItemVisible = View.GONE;
        boolean isSuccess = false;
        PackageStockInEntity entity = new PackageStockInEntity();
        entity.setOuterCode(code);
        switch (status) {
            case Resource.LOADING:
                boxCodeItemVisible = View.VISIBLE;
                entity.setCodeStatus(CodeBean.STATUS_CODE_VERIFYING);
                break;
            case Resource.SUCCESS:
                boxCodeItemVisible = View.VISIBLE;
                entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
                isSuccess = true;
                break;
            case Resource.ERROR:
                boxCodeItemVisible = View.GONE;
                break;
            case Resource.NETWORK_ERROR:
                boxCodeItemVisible = View.VISIBLE;
                entity.setCodeStatus(CodeBean.STATUS_CODE_FAIL);
                break;
            default:
        }
        if (boxCodeItemVisible == View.GONE) {
            mBindingView.layoutPackageStockInHeader.flItemPda.setVisibility(View.GONE);
        } else {
            mBindingView.layoutPackageStockInHeader.flItemPda.setVisibility(View.VISIBLE);
            mBindingView.layoutPackageStockInHeader.layoutBoxCode.setData(entity);

            mBindingView.layoutPackageStockInHeader.layoutBoxCode.tvChecking.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
            mBindingView.layoutPackageStockInHeader.layoutBoxCode.ivCheckOk.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        }
    }

    public void showBoxCodeErrorDialog(String outerCodeId, String msg) {
        CommonDialogUtil.showConfirmDialog(this,"", msg, "知道了", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onRightClick() {
            mViewModel.removeBoxCode(outerCodeId);
            mViewModel.getCountTotal();
        }});
    }

    private void showCodeErrorDialog(String outerCodeId, String msg) {
        CommonDialogUtil.showConfirmDialog(this, "", msg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                mViewModel.removeCode(outerCodeId);
            }
        });
    }

    private void updateShowBoxList(Resource<PackageStockInEntity> resource) {
        PackageStockInEntity entity = resource.getData();
        int status = Resource.NETWORK_ERROR;
        switch (entity.getCodeStatus()) {
            case CodeBean.STATUS_CODE_SUCCESS:
                status = Resource.SUCCESS;
                break;
            case CodeBean.STATUS_CODE_VERIFYING:
                status = Resource.LOADING;
                break;
            case CodeBean.STATUS_CODE_FAIL:
                status = Resource.NETWORK_ERROR;
                break;
        }
        updateBoxCodeStatus(entity.getOuterCode(), status);
        List<PackageStockInEntity> sonList = entity.getSonList();
        mAdapter.notifyRemoveListItem();
        if (sonList != null) {
            mAdapter.notifyRefreshList(sonList);
        }
        mViewModel.getCountTotal();
    }

    private void updateCodeStatus(String code, int status) {
        if (status == Resource.ERROR) {
            int index = -1;
            List<PackageStockInEntity> dataList = mAdapter.getDataList();
            for (int i = 0; i < dataList.size(); i++) {
                if (TextUtils.equals(dataList.get(i).getOuterCode(), code)) {
                    index = i;
                }
            }
            if (index == -1) {
                return;
            }
            mAdapter.notifyRemoveItem(index);
        } else {
            mAdapter.notifyRefreshItemStatus(code, status);
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
                MainActivity.start(PackageStockInPDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(PackageStockInPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_PACKAGE_STOCK_IN, PackageStockInPDAActivity.this);
            }
        });
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

    /**
     * 扫码返回结果
     */
    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        if (onFullBox) {
            onCodeError();
            return;
        }
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (!CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        }else if (mViewModel.isRepeatCode(code)){
            onCodeError();
        }else {
            ScanCodeService.playSuccess();
            if (mViewModel.isInputBoxCode()) {
                mViewModel.checkBoxCode(code);
            } else {
                mViewModel.checkSonCode(code, mAdapter, mBindingView.rvPackageStockIn);
            }
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutPackageStockInFooter.tvScanBack,
                        mBindingView.layoutPackageStockInFooter.tvSure)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_scan_back) {
            if (!mViewModel.hasWaitUpload()) {
                ToastUtils.showToast("请先扫码");
                return;
            }
            CommonPackageScanBackActivity.start(this, TYPE_SCAN_BACK, mViewModel.getConfigId(),
                    CommonPackageScanBackActivity.TYPE_PACKAGE_STOCK_IN);
        } else if (id == R.id.tv_sure) {
            if (!mViewModel.hasWaitUpload()) {
                ToastUtils.showToast("请先扫码");
                return;
            }
            CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                

                @Override
                public void onRightClick() {
                    mViewModel.tryUploadCode();
                }
            });
        }
    }

    /**
     * 按返回键时弹窗
     */
    @Override
    public void onBackPressed() {
        if (mViewModel.hasWaitUpload()) {
            CommonDialogUtil.showSelectDialog(this,"是否退出当前包装关联操作?", "若退出,您所操作的数据将被放入待执行.",
                    "继续退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                            MainActivity.start(PackageStockInPDAActivity.this, 0);
                        }

                        @Override
                        public void onRightClick() {
                            mViewModel.tryUploadCode();
                        }
                    });
        } else {
            MainActivity.start(this, 0);
        }
    }

    /**
     * @param context  context
     * @param configId 配置ID
     */
    public static void start(Context context, Long configId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStockInPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
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
}
