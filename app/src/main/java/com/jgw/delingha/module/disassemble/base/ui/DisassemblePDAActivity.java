package com.jgw.delingha.module.disassemble.base.ui;

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
import com.jgw.delingha.bean.SplitCheckResultBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityDisassembleBinding;
import com.jgw.delingha.module.disassemble.base.viewmodel.DisassembleViewModel;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class DisassemblePDAActivity extends BaseActivity<DisassembleViewModel, ActivityDisassembleBinding> {

    private static final int TYPE_SCAN_BACK = 121;
    private CodeEntityRecyclerAdapter<BaseCodeEntity> mAdapter;

    @Override
    protected void initView() {
        setRight("手输");
        mBindingView.rvcWholeGroupDisassemble.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        boolean isSingleDisassemble = getIntent().getBooleanExtra("isSingleDisassemble", true);
        mViewModel.setIsSingleDisassemble(isSingleDisassemble);
        String string;
        String tips;
        if (isSingleDisassemble) {
            string = ResourcesUtils.getString(R.string.single_disassemble_title);
            tips = ResourcesUtils.getString(R.string.single_disassemble_tip);
        } else {
            string = ResourcesUtils.getString(R.string.whole_group_disassemble_title);
            tips = ResourcesUtils.getString(R.string.whole_group_disassemble_tip);
        }
        setTitle(string);
        mBindingView.tvDisassembleTip.setText(tips);

        mAdapter = new CodeEntityRecyclerAdapter<>();
        mBindingView.rvWholeGroupDisassemble.setAdapter(mAdapter);
        mViewModel.setDataList(mAdapter.getDataList());

        mViewModel.hasWaitUpload();
        mViewModel.calculationTotal();
        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        mViewModel.getWaitUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    if (!resource.getData()) {
                        return;
                    }
                    showWaitUploadDialog();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            Long size = resource.getData();
            mBindingView.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
        });
        mViewModel.getCheckCodeLiveData().observe(this, resource -> {
            SplitCheckResultBean data = resource.getData();
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.updateCodeStatus(data);
                    break;
                case Resource.ERROR:
                    onCodeError();
                    showCodeErrorDialog(data.code, resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    //noinspection DuplicateBranchesInSwitch
                    mViewModel.updateCodeStatus(data);
                    break;
            }
        });
        mViewModel.getUpdateCodeLiveData().observe(this, resource -> updateItemStatus(resource.getData()));
        mViewModel.getLoadListLiveData().observe(this, resource -> {
            DisassemblePDAActivity.this.loadList(resource.getData());
            mViewModel.calculationTotal();
        });
        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
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
                MainActivity.start(DisassemblePDAActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(DisassemblePDAActivity.this, 0);
                int type = mViewModel.isSingleDisassemble ? TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT : TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT;
                TaskListActivity.start(type, DisassemblePDAActivity.this);
            }
        });
    }

    private void loadList(List<BaseCodeEntity> data) {
        if (mViewModel.getPage() == 1) {
            mAdapter.notifyRefreshList(data);
        } else {
            mAdapter.notifyAddListItem(data);
        }
    }

    private void showWaitUploadDialog() {
        CommonDialogUtil.showSelectDialog(this,"您有待执行任务未提交", "是否前往待执行页面",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        DisassembleWaitUploadListActivity.start(DisassemblePDAActivity.this, mViewModel.isSingleDisassemble);
                        finish();
                    }
                });
    }

    private void updateItemStatus(BaseCodeEntity entity) {
        mAdapter.notifyRefreshItem(entity);
    }

    /**
     * 扫码校验接口 后台返回码错误
     */
    private void showCodeErrorDialog(String code, String errorMsg) {
        mViewModel.deleteCode(code);
        mViewModel.refreshList();
        CommonDialogUtil.showConfirmDialog(this,"", errorMsg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                CommonDialog.OnButtonClickListener.super.onRightClick();
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvScanBack, mBindingView.tvSure)
                .addOnClickListener()
                .submit();
        mBindingView.rvWholeGroupDisassemble.addOnScrollListener(new OnLoadMoreListener() {
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
        if (id == R.id.tv_toolbar_right) {
            showInputDialog();
        } else if (id == R.id.tv_scan_back) {
            if (mViewModel.mList.isEmpty()) {
                ToastUtils.showToast("请先扫码!");
                return;
            }
            int type;
            if (mViewModel.isSingleDisassemble) {
                type = CommonScanBackActivity.TYPE_DISASSEMBLE_SINGLE;
            } else {
                type = CommonScanBackActivity.TYPE_DISASSEMBLE_GROUP;
            }
            CommonScanBackActivity.start(DisassemblePDAActivity.this, TYPE_SCAN_BACK, type);

        } else if (id == R.id.tv_sure) {
            onUploadClick();
        }
    }

    private void onUploadClick() {
        if (mAdapter.isEmpty()) {
            ToastUtils.showToast("请先扫码!");
            return;
        }
        if (mViewModel.existVerifyingOrFailData()) {
            ToastUtils.showToast("还有码在校验中,请稍后再试");
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.upload();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == TYPE_SCAN_BACK) {// 反扫
            mViewModel.refreshList();
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.mList.isEmpty()) {
            super.onBackPressed();
            return;
        }
        CommonDialogUtil.showSelectDialog(this,mViewModel.isSingleDisassemble ? "是否退出当前单个拆解操作?" : "是否退出当前整组拆解操作?",
                "若退出,您所操作的数据将被放入待执行中.",
                "确认退出", "立即上传", new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick() {
                        finish();
                    }

                    @Override
                    public void onRightClick() {
                        if (mViewModel.existVerifyingOrFailData()) {
                            ToastUtils.showToast("还有码在校验中,请稍后再试");
                            return;
                        }
                        mViewModel.upload();
                    }
                });
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (!CheckCodeUtils.checkCode(code)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        } else if (mViewModel.isRepeatCode(code)) {
            onCodeError();
        } else {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvWholeGroupDisassemble);
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
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

    public static void start(Context context, boolean isSingleDisassemble) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, DisassemblePDAActivity.class);
            intent.putExtra("isSingleDisassemble", isSingleDisassemble);
            context.startActivity(intent);
        }
    }
}
