package com.jgw.delingha.module.disassemble.all.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.databinding.ActivityDisassembleAllWaitUploadBinding;
import com.jgw.delingha.module.disassemble.all.adapter.DisassembleAllWaitUploadRecyclerAdapter;
import com.jgw.delingha.module.disassemble.all.viewmode.DisassembleAllWaitUploadViewModel;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.sql.entity.DisassembleAllEntity;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * 整组拆解待执行列表
 */
public class DisassembleAllWaitUploadListActivity extends BaseActivity<DisassembleAllWaitUploadViewModel, ActivityDisassembleAllWaitUploadBinding> {

    private DisassembleAllWaitUploadRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcDisassembleWaitUpload.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("待执行打散套标");

        mAdapter = new DisassembleAllWaitUploadRecyclerAdapter();
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvDisassembleWaitUpload.setAdapter(mAdapter);
        mViewModel.setList(mAdapter.getDataList());
        mViewModel.loadList();
    }

    @Override
    public void initLiveData() {

        mViewModel.getListLiveData().observe(this, resource -> {
            if (resource.getLoadingStatus() == Resource.SUCCESS) {
                List<DisassembleAllEntity> data = resource.getData();
                mAdapter.notifyRefreshList(data);
            } else {
                ToastUtils.showToast(resource.getErrorMsg());
            }
            mViewModel.calculationTotal();
        });
        mViewModel.getCalculationTotalLiveData().observe(this, integerResource -> calculationTotal(integerResource.getData()));

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
    }

    private void showUploadFinishDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";

        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showConfirmDialog(this, "数据上传成功", details, "返回工作台", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                MainActivity.start(DisassembleAllWaitUploadListActivity.this, 0);
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
            CommonDialogUtil.showConfirmDialog(this, "身份码不存在!", scanCode, "知道了", new CommonDialog.OnButtonClickListener() {});
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

    private void onDeleteAllClick() {
        if (mAdapter.isEmpty()) {
            ToastUtils.showToast("没有码可以删除");
            return;
        }
        CommonDialogUtil.showSelectDialog(this, "是否确定全部删除?", "", "取消", "删除", new CommonDialog.OnButtonClickListener() {
            

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
        CommonDialogUtil.showSelectDialog(this, "确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {
            

            @Override
            public void onRightClick() {
                mViewModel.upload();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        DisassembleAllEntity entity = mAdapter.getDataList().get(position);
        mViewModel.removeData(entity);
        deleteCode(entity.getCode());
    }

    private void deleteCode(String outerCode) {
        DisassembleAllEntity entity = new DisassembleAllEntity();
        entity.setCode(outerCode);
        mAdapter.notifyRemoveItem(entity);
        mViewModel.calculationTotal();
    }

    public void calculationTotal(int size) {
        mBindingView.tvDisassembleWaitUploadAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
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


    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, DisassembleAllWaitUploadListActivity.class);
            context.startActivity(intent);
        }
    }
}
