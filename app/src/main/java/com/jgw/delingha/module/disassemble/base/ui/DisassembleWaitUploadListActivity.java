package com.jgw.delingha.module.disassemble.base.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
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
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityDisassemblePendingBinding;
import com.jgw.delingha.module.disassemble.base.adapter.DisassembleWaitUploadRecyclerAdapter;
import com.jgw.delingha.module.disassemble.base.viewmodel.DisassembleWaitUploadViewModel;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 整组拆解待执行列表
 */
public class DisassembleWaitUploadListActivity extends BaseActivity<DisassembleWaitUploadViewModel, ActivityDisassemblePendingBinding> {

    private DisassembleWaitUploadRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcDisassembleWaitUpload.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        boolean isSingleDisassemble = getIntent().getBooleanExtra("isSingleDisassemble", false);
        String string;
        if (isSingleDisassemble) {
            string = ResourcesUtils.getString(R.string.single_disassemble_pending_title);
        } else {
            string = ResourcesUtils.getString(R.string.whole_group_disassemble_pending_title);
        }
        setTitle(string);
        mViewModel.setIsSingleDisassemble(isSingleDisassemble);

        mAdapter = new DisassembleWaitUploadRecyclerAdapter();
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvDisassembleWaitUpload.setAdapter(mAdapter);

        mViewModel.loadList();
        calculationTotal();
    }

    @Override
    public void initLiveData() {

        mViewModel.getLoadListLiveData().observe(this, resource -> {
            loadList(resource.getData());
            calculationTotal();
        });
        mViewModel.getClearDataLiveData().observe(this, resource -> mViewModel.loadList());

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

        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            Long size = resource.getData();
            mBindingView.tvDisassembleWaitUploadAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
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
                MainActivity.start(DisassembleWaitUploadListActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(DisassembleWaitUploadListActivity.this, 0);
                int type = mViewModel.getIsSingleDisassemble() ? TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT : TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT;
                TaskListActivity.start(type, DisassembleWaitUploadListActivity.this);
            }
        });
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(scanCode)) {
            mViewModel.deleteCode(scanCode);
            deleteCode(scanCode);
            ScanCodeService.playSuccess();
        } else {
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", scanCode, "知道了", new CommonDialog.OnButtonClickListener() {});
            ScanCodeService.playError();
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvDisassembleWaitUploadDeleteAll)
                .addView(mBindingView.tvDisassembleWaitUploadSubmit)
                .addOnClickListener()
                .submit();
        mBindingView.rvDisassembleWaitUpload.addOnScrollListener(new OnLoadMoreListener() {
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
        if (id == R.id.tv_disassemble_wait_upload_delete_all) {
            onDeleteAllClick();
        } else if (id == R.id.tv_disassemble_wait_upload_submit) {
            onUploadClick();
        }
    }

    private void loadList(List<BaseCodeEntity> data) {
        if (mViewModel.getPage() == 1) {
            mAdapter.notifyRefreshList(data);
        } else {
            mAdapter.notifyAddListItem(data);
        }
    }

    private void onDeleteAllClick() {
        if (mAdapter.isEmpty()) {
            ToastUtils.showToast("没有码可以删除");
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"是否确定全部删除?", "", "取消", "删除", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.clearData();
            }
        });
    }

    private void onUploadClick() {
        if (mAdapter.isEmpty()) {
            ToastUtils.showToast("请先扫码!");
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
    public void onItemClick(View view, int position) {
        BaseCodeEntity baseCodeEntity = mAdapter.getDataList().get(position);
        String code = baseCodeEntity.getCode();
        mViewModel.deleteCode(code);
        deleteCode(code);
    }

    private void deleteCode(String outerCode) {
        int index = -1;
        for (int i = 0; i < mAdapter.getDataList().size(); i++) {
            BaseCodeEntity entity = mAdapter.getDataList().get(i);
            if (TextUtils.equals(entity.getCode(), outerCode)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return;
        }
        mAdapter.notifyRemoveItem(index);
        calculationTotal();
    }

    public void calculationTotal() {
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


    public static void start(Context context, boolean isSingleDisassemble) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, DisassembleWaitUploadListActivity.class);
            intent.putExtra("isSingleDisassemble", isSingleDisassemble);
            context.startActivity(intent);
        }
    }
}
