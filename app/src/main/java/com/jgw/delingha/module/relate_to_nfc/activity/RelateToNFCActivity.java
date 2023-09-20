package com.jgw.delingha.module.relate_to_nfc.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcV;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

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
import com.jgw.delingha.databinding.ActivityRelateToNfcBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.relate_to_nfc.adapter.RelateToNFCAdapter;
import com.jgw.delingha.module.relate_to_nfc.viewmodel.RelateToNFCViewModel;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;
import com.jgw.scan_nfc_library.utils.NfcUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * @author : J-T
 * @date : 2022/6/8 15:57
 * description : 关联NFC Activity
 */
public class RelateToNFCActivity extends BaseActivity<RelateToNFCViewModel, ActivityRelateToNfcBinding> {
    private RelateToNFCAdapter mAdapter;
    private NfcUtils nfcUtils;

    @Override
    protected void initView() {
        setTitle("关联NFC");
        mBindingView.rvcRelateToNfcRecord.setEmptyLayout(R.layout.item_scan_back_no_data);
    }

    @Override
    protected void initData() {
        mAdapter = new RelateToNFCAdapter();
        mBindingView.setData(mViewModel.getNfcBean());
        mBindingView.rvRelateToNfcRecord.setAdapter(mAdapter);
        mViewModel.getCount();
        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.refreshList();
        nfcUtils = NfcUtils.getInstance(this,NfcAdapter.ACTION_TECH_DISCOVERED);
        nfcUtils.setTechList(new String[][]{new String[]{
                NfcV.class.getName(),
        }});
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getCountLiveData().observe(this, integerResource -> {
            switch (integerResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    long size = integerResource.getData();
                    mBindingView.layoutRelateToNfcFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条"));
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(integerResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getRefreshListLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(listResource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getLoadMoreListLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyAddListItem(listResource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getTaskIdLiveData().observe(this, stringResource -> {
            switch (stringResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    String data = stringResource.getData();
                    mViewModel.uploadData(data);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(stringResource.getErrorMsg());
                    break;
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
            }
        });


    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvRelateToNfcQrcode)
                .addView(mBindingView.tvRelateToNfcNfccode)
                .addView(mBindingView.layoutRelateToNfcFooter.tvScanBack)
                .addView(mBindingView.layoutRelateToNfcFooter.tvUpload)
                .addOnClickListener()
                .submit();
        mBindingView.rvRelateToNfcRecord.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.iv_clear) {
            mBindingView.tvRelateToNfcQrcode.setText("");
        } else if (id == R.id.tv_relate_to_nfc_qrcode) {
            showInputDialog();
        } else if (id == R.id.tv_scan_back) {
            RelateToNFCScanBackActivity.start(this, 1);
        } else if (id == R.id.tv_upload) {
            uploadClick();
        }
    }

    public void uploadClick() {
        if (mViewModel.checkCode()) {
            return;
        }
        CommonDialogUtil.showSelectDialog(this,"确定上传?", "", "取消", "确定", new CommonDialog.OnButtonClickListener() {

            @Override
            public void onLeftClick() {

            }

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
                MainActivity.start(RelateToNFCActivity.this, 0);
            }

            @Override
            public void onRightClick() {
                MainActivity.start(RelateToNFCActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_RELATE_TO_NFC, RelateToNFCActivity.this);
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
        } else if (mViewModel.checkCodeExisted(code)) {
            ToastUtils.showToast(code + "该码已录入或被其他账号录入,请切换账号或清除缓存");
            onCodeError();
        } else {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvRelateToNfcRecord);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        List<String> strings = nfcUtils.parseNfcData(intent);
        if (strings == null) {
            return;
        }
        final int FDW_UID_LENGTH = 16;
        for (String s : strings) {
            if (TextUtils.isEmpty(s)){
                ToastUtils.showToast("未能获取到UID!");
                return;
            }
            if (s.length()!= FDW_UID_LENGTH ||!TextUtils.equals("1de0",s.substring(FDW_UID_LENGTH-4,FDW_UID_LENGTH).toLowerCase())){
                ToastUtils.showToast("UID不合法!");
                return;
            }
            if (mViewModel.checkNFCExisted(s)) {
                ToastUtils.showToast(s + "该码已录入或被其他账号录入,请切换账号或清除缓存");
                return;
            }
            mViewModel.handleScanNFCCode(s, mAdapter, mBindingView.rvRelateToNfcRecord);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case 1:
                // 反扫
                mViewModel.refreshList();
                mViewModel.getCount();
                break;
            default:
        }
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
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, RelateToNFCActivity.class);
            context.startActivity(intent);
        }
    }
}
