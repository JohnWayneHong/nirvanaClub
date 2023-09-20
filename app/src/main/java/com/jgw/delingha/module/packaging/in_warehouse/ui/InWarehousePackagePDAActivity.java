package com.jgw.delingha.module.packaging.in_warehouse.ui;

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
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityInWarehousePackageBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.packaging.in_warehouse.adpter.InWarehousePackageAdapter;
import com.jgw.delingha.module.packaging.in_warehouse.viewmodel.InWarehousePackageViewModel;
import com.jgw.delingha.module.scan_back.ui.CommonPackageScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * author : xsw
 * data : 2020/09/21
 * description : 在库关联扫码界面
 */
public class InWarehousePackagePDAActivity extends BaseActivity<InWarehousePackageViewModel, ActivityInWarehousePackageBinding> {

    private static final int TYPE_SCAN_BACK = 101;

    private InWarehousePackageAdapter mAdapter;
    private boolean onFullBox;

    @Override
    protected void initView() {
        setTitle("在库关联");
        setRight("输入");
        mBindingView.rvcPackaging.setEmptyLayout(R.layout.item_scan_back_no_data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == TYPE_SCAN_BACK) {
                String boxCode = mViewModel.getCurrentBoxCode();
                if (mViewModel.isRepeatBoxCode(boxCode)) {
                    //当前箱码还存在 刷新子码列表
                    mViewModel.refreshCurrentBoxList();
                } else {
                    //执行父码被删除的逻辑
                    updateBoxCodeStatus(boxCode, Resource.ERROR);
                    mAdapter.notifyRemoveListItem();

                    if (!TextUtils.isEmpty(boxCode)) {
                        mViewModel.resetBoxCode();
                        mViewModel.removeBoxCode(boxCode);
                    }
                    mViewModel.updateStatisticsCount();
                }
            }
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        if (onFullBox) {
            onCodeError();
            return;
        }
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (!CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this, "身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        } else if (mViewModel.isRepeatCode(code)) {
            onCodeError();
        } else {
            ScanCodeService.playSuccess();
            if (mViewModel.isInputBoxCode()) {
                mViewModel.checkBoxCode(code);
            } else {
                mViewModel.checkSonCode(code, mAdapter, mBindingView.rvPackaging);
            }
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        long configId = intent.getLongExtra(ConstantUtil.CONFIG_ID, -1);
        if (configId == -1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        mViewModel.getConfigInfo(configId);

        mAdapter = new InWarehousePackageAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvPackaging.setAdapter(mAdapter);


    }

    @Override
    public void initLiveData() {
        //设置配置信息
        mViewModel.getConfigInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.setConfigInfo(resource.getData());
                    setConfigInfo(mViewModel.getConfigEntity());
                    //获取到配置信息时才能去刷新统计信息
                    mViewModel.updateStatisticsCount();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        //刷新左下全部统计数量
        mViewModel.getUpdateStatisticsCountLiveData().observe(this, resource -> {
            LogUtils.showLog("UpdateStatisticsCount:", "+++");

            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateStatisticsCount(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        //仅更新当前子码数量统计
        mViewModel.getCurrentCountLiveData().observe(this, aLong -> updateCodeCountView());
        //校验父码
        mViewModel.getCheckBoxCodeLiveData().observe(this, resource -> {
            String code = resource.getData();
            //仅当校验为当前箱码时触发父码相关UI修改
            if (mViewModel.isCurrentBoxCode(code)) {
                //码状态变更涉及UI修改
                updateBoxCodeStatus(code, resource.getLoadingStatus());
            }
            LogUtils.showLog("checkBoxCode:" + code, ",status=" + resource.getLoadingStatus());
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mViewModel.updateStatisticsCount();
                    break;
                case Resource.SUCCESS:
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR://服务器校验错误删除码
                    onCodeError();
                    showBoxCodeErrorDialog(code, resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR://网络错误不删除
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });
        //校验子码
        mViewModel.getCheckCodeLiveData().observe(this, resource -> {
            InWarehousePackageEntity entity = resource.getData();
            String code = entity.getOuterCode();
            //此处仅处理成功和网络失败
            if (mViewModel.isCurrentBoxCode(entity.getParentOuterCodeId())) {
                //码状态变更涉及UI修改
                updateCodeStatus(entity.getOuterCode(), resource.getLoadingStatus());
            }
            LogUtils.showLog("checkCode:" + code, ",status=" + resource.getLoadingStatus());
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_SUCCESS);
                    break;
                case Resource.ERROR://服务器校验错误删除码
                    onCodeError();
                    showCodeErrorDialog(code, resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR://网络错误不删除
                    mViewModel.updateCodeStatus(code, CodeBean.STATUS_CODE_FAIL);
                    break;
            }
        });
        //当前箱满刷新界面
        mViewModel.getOnFullBoxLiveData().observe(this, aLong -> {
            mAdapter.notifyRemoveListItem();
            updateBoxCodeStatus(mViewModel.getCurrentBoxCode(), Resource.ERROR);
            mViewModel.resetBoxCode();
            mViewModel.updateStatisticsCount();
            onFullBox = true;
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this, "亲,该箱已装满!", "该箱码数据存在'查看/删除里'", "确定", new CommonDialog.OnButtonClickListener() {
                @Override
                public void onRightClick() {
                    onFullBox = false;
                }
            });
        });
        //展示扫码的箱码数据
        mViewModel.getShowBoxListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    //箱码对象储存了子码列表
                    updateShowBoxList(resource);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        //刷新当前箱码数据
        mViewModel.getRefreshBoxListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    //箱码对象储存了子码列表
                    mAdapter.notifyRefreshList(resource.getData());
                    updateCodeCountView();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getTryUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    //箱码对象储存了子码列表
                    String notFullBoxCode = resource.getData();
                    if (TextUtils.isEmpty(notFullBoxCode)) {
                        mViewModel.getTaskId();
                    } else {
                        showNotFullBoxDialog(notFullBoxCode);
                    }
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
                    mViewModel.realUploadCode();
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
                    UploadResultBean data = resource.getData();
                    showUploadFinishDialog(data);
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void showNotFullBoxDialog(String boxCode) {
        String firstNumberName = mViewModel.getFirstNumberName();
        String lastNumberName = mViewModel.getLastNumberName();
        String title = "该" + firstNumberName + "码下的" + lastNumberName + "码还未满\n请先填满";
        CommonDialogUtil.showConfirmDialog(this, title, boxCode, "知道了", new CommonDialog.OnButtonClickListener() {});
    }

    private void updateShowBoxList(Resource<InWarehousePackageEntity> resource) {
        InWarehousePackageEntity entity = resource.getData();
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
        List<InWarehousePackageEntity> sonList = entity.getSonList();
        mAdapter.notifyRefreshList(sonList);
        mViewModel.updateStatisticsCount();
    }

    private void updateBoxCodeStatus(String code, int status) {
        int boxCodeItemVisible = View.GONE;
        boolean isSuccess = false;
        InWarehousePackageEntity entity = new InWarehousePackageEntity();
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
        }
        if (boxCodeItemVisible == View.GONE) {
            mBindingView.layoutInWarehousePackageHeader.flItemPda.setVisibility(View.GONE);
        } else {
            mBindingView.layoutInWarehousePackageHeader.flItemPda.setVisibility(View.VISIBLE);
            mBindingView.layoutInWarehousePackageHeader.layoutBoxCode.setData(entity);

            mBindingView.layoutInWarehousePackageHeader.layoutBoxCode.tvChecking.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
            mBindingView.layoutInWarehousePackageHeader.layoutBoxCode.ivCheckOk.setVisibility(isSuccess ? View.VISIBLE : View.GONE);
        }

    }

    /**
     * 更新子码状态
     *
     * @param code   子码
     * @param status 仅处理成功和网络失败状态变化
     */
    private void updateCodeStatus(String code, int status) {
        InWarehousePackageEntity tempEntity = new InWarehousePackageEntity();
        tempEntity.setOuterCode(code);
        int index = mAdapter.getDataList().indexOf(tempEntity);
        if (index == -1) {
            return;
        }
        InWarehousePackageEntity entity = mAdapter.getDataList().get(index);
        switch (status) {
            case Resource.SUCCESS:
                entity.setCodeStatus(CodeBean.STATUS_CODE_SUCCESS);
                mAdapter.notifyRefreshItem(entity);
                break;
            case Resource.ERROR:
                mAdapter.notifyRemoveItem(index);
                break;
            case Resource.NETWORK_ERROR:
                entity.setCodeStatus(CodeBean.STATUS_CODE_FAIL);
                mAdapter.notifyItemChanged(index);
                break;
        }
    }

    private void showBoxCodeErrorDialog(String outerCodeId, String msg) {
        mViewModel.removeBoxCode(outerCodeId);
        mViewModel.updateStatisticsCount();
        CommonDialogUtil.showConfirmDialog(this, "", msg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {

            }
        });
    }

    private void showCodeErrorDialog(String outerCodeId, String msg) {
        mViewModel.removeCode(outerCodeId);
        CommonDialogUtil.showConfirmDialog(this, "", msg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                CommonDialog.OnButtonClickListener.super.onRightClick();
            }
        });
    }

    private void setConfigInfo(PackageConfigEntity entity) {
        mBindingView.layoutInWarehousePackageHeader.setData(entity);
        boolean empty = TextUtils.isEmpty(entity.getProductBatchName());
        mBindingView.layoutInWarehousePackageHeader.llProductBatch.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void clickRight() {
        super.clickRight();
        CommonDialogUtil.showInputDialog(this, "身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {


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
                .addView(mBindingView.layoutStockReturnFooter.tvScanBack,
                        mBindingView.layoutStockReturnFooter.tvSure)
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
                    CommonPackageScanBackActivity.TYPE_IN_WARE_HOUSE_PACKAGE);
        } else if (id == R.id.tv_sure) {
            if (!mViewModel.hasWaitUpload()) {
                ToastUtils.showToast("请先扫码");
                return;
            }
            CommonDialogUtil.showSelectDialog(this, "确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {


                @Override
                public void onRightClick() {
                    mViewModel.tryUploadCode();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.hasWaitUpload()) {

            CommonDialogUtil.showSelectDialog(this, "是否退出当前包装关联操作?", "若退出,您所操作的数据将被放入待执行.",
                    "继续退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                            MainActivity.start(InWarehousePackagePDAActivity.this, 0);
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

    private void showUploadFinishDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";

        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showSelectDialog(this, "数据上传成功", details, "返回工作台", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                MainActivity.start(InWarehousePackagePDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(InWarehousePackagePDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_IN_WAREHOUSE_PACKAGE, InWarehousePackagePDAActivity.this);
            }
        });
    }

    private void updateStatisticsCount(long boxCount) {
        updateCodeCountView();
        updateBoxCountView(boxCount);
    }

    /**
     * 更新页面左下角"当前"开头字样 当前箱子码数量统计
     */
    private void updateCodeCountView() {
        Spanned codeCountStatistics = mViewModel.getCodeCountStatistics();
        mBindingView.layoutStockReturnFooter.tvCodeAmount.setText(codeCountStatistics);
    }

    /**
     * 更新页面左下角"累计"字样 共计箱(父)码数量
     *
     * @param boxCount
     */
    private void updateBoxCountView(long boxCount) {
        Spanned boxCodeCountStatistics = mViewModel.getBoxCodeCountStatistics(boxCount);
        mBindingView.layoutStockReturnFooter.tvBoxAmount.setText(boxCodeCountStatistics);
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

    /**
     * 跳转函数
     */
    public static void start(Context context, long configId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, InWarehousePackagePDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivity(intent);
        }
    }
}
