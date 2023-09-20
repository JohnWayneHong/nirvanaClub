package com.jgw.delingha.module.inventory.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.BuildConfig;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.InventoryDetailsBean;
import com.jgw.delingha.bean.InventoryScanBean;
import com.jgw.delingha.databinding.ActivityInventoryPdaBinding;
import com.jgw.delingha.module.inventory.adapter.InventoryPDAListRecyclerAdapter;
import com.jgw.delingha.module.inventory.viewmodel.InventoryPDAViewModel;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/10
 */
public class InventoryPDAActivity extends BaseActivity<InventoryPDAViewModel, ActivityInventoryPdaBinding> {


    private InventoryPDAListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        if (!TextUtils.equals(BuildConfig.BUILD_TYPE, "release")) {
            mBindingView.rvInventoryPda.setItemAnimator(null);
        }
        mBindingView.rvcInventoryPda.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("盘点");
        setRight("重试");
        String data = getIntent().getStringExtra("data");
        InventoryDetailsBean.ListBean bean = JsonUtils.parseObject(data, InventoryDetailsBean.ListBean.class);
        mViewModel.setHeaderData(bean);
        mBindingView.layoutInventoryHeader.setData(bean);

        mAdapter = new InventoryPDAListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvInventoryPda.setAdapter(mAdapter);
        mViewModel.calculationTotal();
    }

    @Override
    protected void clickRight() {
        super.clickRight();
        mViewModel.tryAgainUpload();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutInventoryFooter.tvInventoryPdaInputCode)
                .addView(mBindingView.layoutInventoryFooter.tvInventoryPdaView)
                .addView(mBindingView.layoutInventoryFooter.tvInventoryPdaSure)
                .addOnClickListener()
                .submit();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void initLiveData() {
        mViewModel.getPostSingleInventoryLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateItemStatus(resource.getData());
                    break;
                case Resource.ERROR:
                    onCodeError();
                    showCodeErrorDialog(resource.getData(), resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    updateItemStatus(resource.getData());
                    break;
            }
        });
        mViewModel.getPostGroupInventoryLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    showUploadFinishDialog(resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getCalculationTotalLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateTotal();
                    break;
                case Resource.ERROR:
                    break;
            }
        });
    }

    private void updateTotal() {
        int size = mAdapter.getDataList().size();
        Spanned spanned = Html.fromHtml("共<font color='#03A9F4'>" + size + "</font>条");
        mBindingView.layoutInventoryFooter.tvInventoryPdaCodeAmount.setText(spanned);
    }

    /**
     * 扫码校验接口 后台返回码错误
     */
    private void showCodeErrorDialog(InventoryScanBean data, String errorMsg) {
        CommonDialogUtil.showConfirmDialog(this, "", errorMsg, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                removeItem(data);
            }
        });
    }

    private void showUploadFinishDialog(List<InventoryScanBean> data) {
        int success = 0;
        int error = 0;
        for (int i = 0; i < data.size(); i++) {
            InventoryScanBean bean = data.get(i);
            if (bean.codeStatus == CodeBean.STATUS_CODE_SUCCESS) {
                success++;
            } else {
                error++;
            }
        }
        String details;
        if (error == 0) {
            details = "您成功上传了" + success + "条数据.";

        } else {
            details = "您成功上传了" + success + "条数据.\n其中失败" + error + "条";
        }
        CommonDialogUtil.showConfirmDialog(this, "数据上传成功", details, "知道了", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onRightClick() {
                refreshList(data);
            }
        });
    }

    private void refreshList(List<InventoryScanBean> data) {
        ArrayList<InventoryScanBean> temp = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            InventoryScanBean inventoryScanBean = data.get(i);
            if (inventoryScanBean.codeStatus == CodeBean.STATUS_CODE_SUCCESS) {
                List<InventoryScanBean> currentList = mAdapter.getDataList();
                int index = currentList.indexOf(inventoryScanBean);
                if (index == -1) {
                    break;
                }
                updateItemStatus(inventoryScanBean);

            } else //noinspection StatementWithEmptyBody
                if (inventoryScanBean.codeStatus == CodeBean.STATUS_CODE_FAIL && inventoryScanBean.isRealError) {
                    temp.add(inventoryScanBean);
                } else {
                    //非成功失败说明未成功调用校验接口
                }
        }
        if (!temp.isEmpty()) {
            for (InventoryScanBean bean : temp) {
                mAdapter.notifyRemoveItem(bean);
            }
            mViewModel.calculationTotal();
        }
    }

    private void removeItem(InventoryScanBean data) {
        List<InventoryScanBean> dataList = mAdapter.getDataList();
        int index = dataList.indexOf(data);
        if (index == -1) {
            return;
        }
        mAdapter.notifyRemoveItem(index);
        mAdapter.notifyItemRangeChanged(index, mAdapter.getDataList().size());
        mViewModel.calculationTotal();
    }

    private void updateItemStatus(InventoryScanBean data) {
        List<InventoryScanBean> dataList = mAdapter.getDataList();
        int index = dataList.indexOf(data);
        if (index == -1) {
            return;
        }
        InventoryScanBean realData = dataList.get(index);
        realData.codeStatus = data.codeStatus;
        mAdapter.notifyItemChanged(index);

        InventoryDetailsBean.ListBean oldData = mBindingView.layoutInventoryHeader.getData();
        oldData.inventoryFirstNumber += data.inventoryFirstNumber;
        oldData.inventorySecondNumber += data.inventorySecondNumber;
        oldData.inventoryThirdNumber += data.inventoryThirdNumber;
        mBindingView.layoutInventoryHeader.setData(oldData);
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

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        if (!CheckCodeUtils.checkCode(code)) {
            onCodeError();
            CommonDialogUtil.showConfirmDialog(this, "身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        } else if (mViewModel.checkCodeExisted(code)) {
            onCodeError();
        } else {
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code, mAdapter, mBindingView.rvInventoryPda);
        }
    }

    public void onCodeError() {
        ScanCodeService.playError();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_inventory_pda_input_code) {
            CommonDialogUtil.showInputDialog(this, "身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {


                @Override
                public boolean onInput(String input) {
                    AutoScanCodeUtils.autoScan(input);
                    return CommonDialog.OnButtonClickListener.super.onInput(input);
                }
            });
        } else if (id == R.id.tv_inventory_pda_view) {
            InventoryFinishListActivity.start(this, mViewModel.getHeaderBean().inventoryProductId);
        } else if (id == R.id.tv_inventory_pda_sure) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mViewModel.checkScanCodeListCount()) {
            CommonDialogUtil.showSelectDialog(this, "有码未校验成功!", "继续退出未上传成功的码将会丢失!",
                    "取消", "继续退出", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                        }

                        @Override
                        public void onRightClick() {
                            finish();
                        }
                    });
        } else {
            finish();
        }
    }

    public static void start(Activity context, InventoryDetailsBean.ListBean bean) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, InventoryPDAActivity.class);
            intent.putExtra("data", JsonUtils.toJsonString(bean));
            context.startActivity(intent);
        }
    }
}
