package com.jgw.delingha.custom_module.delingha.breed.ear_reset;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.common.BaseScanActivity;
import com.jgw.delingha.databinding.ActivityBreedEarResetDetailsBinding;
import com.jgw.scan_code_library.CheckCodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 2023-8-4 09:42:48
 * 养殖管理 耳号重置 activity
 */
public class EarResetListActivity extends BaseScanActivity<EarResetListViewModel, ActivityBreedEarResetDetailsBinding>{

    public EarResetAssociateAdapter mAdapter;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("耳号重置");
        mAdapter = new EarResetAssociateAdapter();

        mBindingView.rvBreedEarInfo.setAdapter(mAdapter);
        updateCountView(mAdapter.getDataList().size());
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getRemoveEarCodeAssociationLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    ToastUtils.showToast("耳号重置成功");
                    finish();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

    }

    @Override
    protected void initListener() {
        super.initListener();

        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutEarActionFooter.tvInputCode)
                .addView(mBindingView.layoutEarActionFooter.tvScanBack)
                .submit();

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == mBindingView.layoutEarActionFooter.tvScanBack.getId()) {
            if (mAdapter.getDataList().size() <= 0) {
                ToastUtils.showToast("暂无耳号可重置");
                return;
            }
            CommonDialogUtil.showSelectDialog(this, "提示", "确定要重置所有耳号的状态吗？?", "取消", "确认", new CommonDialog.OnButtonClickListener() {
                @Override
                public void onRightClick() {

                    mViewModel.removeEarCodeAssociation(mAdapter.getDataList());
                }
            });

        }else if (id == mBindingView.layoutEarActionFooter.tvInputCode.getId()) {
            CommonDialogUtil.showInputDialog(this, "需要重置的耳号", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                @Override
                public boolean onInput(String input) {
                    scanable = true;
                    EventBus.getDefault().post(new CommonEvent.ScanRFIDEvent(input));
                    return true;
                }
            });
        }
    }

    public void updateCountView(long count) {
        mBindingView.layoutEarActionFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + count + "</font>条"));
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanRFIDEvent event) {
        if (!scanable) {
            return;
        }
        scanable = false;
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            mAdapter.addAndNotifyLastItem(code);
            updateCountView(mAdapter.getDataList().size());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, EarResetListActivity.class);
            context.startActivity(intent);
        }
    }

}
