package com.jgw.delingha.custom_module.delingha.breed.out.details;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedEarAssociateBean;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.common.BaseScanActivity;
import com.jgw.delingha.custom_module.delingha.breed.in.details.BreedInDetailsViewModel;
import com.jgw.delingha.databinding.ActivityBreedScanBackBinding;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023-8-4 10:55:53
 * 养殖离场 耳号反扫
 */
public class BreedOutScanBackActivity extends BaseScanActivity<BreedOutDetailsViewModel, ActivityBreedScanBackBinding> {

    private BreedOutScanBackRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcCommonScanBack.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("删除");

        mAdapter = new BreedOutScanBackRecyclerAdapter();
        mBindingView.rvCommonScanBack.setAdapter(mAdapter);
        mViewModel.setBatchOut(getIntent().getStringExtra("batchIn"));
        calculationTotal();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.layoutBreedScanBackFooter.tvInputCode)
                .addView(mBindingView.layoutBreedScanBackFooter.tvDeleteAll)
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_delete_all) {
            onBackPressed();
        } else if (id == R.id.tv_input_code) {
            CommonDialogUtil.showInputDialog(this, "耳号", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                @Override
                public boolean onInput(String input) {
                    EventBus.getDefault().post(new CommonEvent.ScanRFIDEvent(input));
                    return true;
                }

            });

        }
    }

    @Override
    public void initLiveData() {
        mViewModel.getRemoveEarCodeAssociationLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mAdapter.addAndNotifyLastItem(mViewModel.getScanCode());
                    calculationTotal();
                    LogUtils.xswShowLog("耳号取消关联成功");
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void calculationTotal() {
        int size = mAdapter.getDataList().size();
        Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
        mBindingView.layoutBreedScanBackFooter.tvCodeAmount.setText(spanned);
    }

    @Override
    public void onBackPressed() {
        MMKVUtils.saveTempData(mAdapter.getDataList());
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanRFIDEvent event) {
        if (!scanable) {
            return;
        }
        scanable = false;
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            ScanCodeService.playSuccess();
            deleteItem(code);
        } else {
            ScanCodeService.playError();
        }

    }

    private void deleteItem(String code) {
        mViewModel.setScanCode(code);
        mViewModel.removeEarCodeAssociation();
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

    public static void start(Fragment fragment, int requestCode,String batchOut) {
        if (fragment == null || fragment.getActivity() == null) {
            return;
        }
        FragmentActivity context = fragment.getActivity();
        Intent intent = new Intent(context, BreedOutScanBackActivity.class);
        intent.putExtra("batchOut",batchOut);

        context.startActivityForResult(intent, requestCode);
    }

}
