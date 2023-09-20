package com.jgw.delingha.custom_module.wanwei.stock_out.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
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
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.custom_module.wanwei.stock_out.adapter.WanWeiStockOutPDAListAdapter;
import com.jgw.delingha.custom_module.wanwei.stock_out.viewmodel.WanWeiStockOutPDAViewModel;
import com.jgw.delingha.databinding.ActivityWanweiStockOutPdaBinding;
import com.jgw.delingha.module.main.MainActivity;
import com.jgw.delingha.module.scan_back.ui.CommonScanBackActivity;
import com.jgw.delingha.module.task_list.ui.TaskListActivity;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.WanWeiStockOutEntity;
import com.jgw.delingha.utils.AutoScanCodeUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

/**
 * 直接出库扫码界面
 * Created by XiongShaoWu
 * on 2020/2/27
 */
public class WanWeiStockOutPDAActivity extends BaseActivity<WanWeiStockOutPDAViewModel, ActivityWanweiStockOutPdaBinding> {

    private WanWeiStockOutPDAListAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.rvcWanweiStockOut.setEmptyLayout(R.layout.item_stock_action_no_data);
    }

    @Override
    protected void initData() {
        long configId = getIntent().getLongExtra(ConstantUtil.CONFIG_ID, -1);
        if (configId == -1) {
            ToastUtils.showToast("获取配置信息失败");
            return;
        }
        setTitle("出库");

        mViewModel.initHeaderData(configId);
        mAdapter = new WanWeiStockOutPDAListAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvWanweiStockOut.setAdapter(mAdapter);
        calculationTotal();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    //数据库版反扫
                    mViewModel.refreshListByConfigId();
                    break;
            }
        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getRequestHeaderDataLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    ConfigurationEntity data = resource.getData();
                    mBindingView.layoutWanweiStockOutHeader.setData(data);
                    mViewModel.setConfigInfo(data);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast("获取设置信息失败");
                    break;
            }
        });
        mViewModel.getCheckCodeLiveData().observe(this, resource -> {
            CodeBean bean = resource.getData();
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCodeStatusItem(bean, CodeBean.STATUS_CODE_SUCCESS);
                    mViewModel.calculationTotal();
                    break;
                case Resource.ERROR:
                    mViewModel.calculationTotal();
                    showCodeErrorDialog(bean.outerCodeId, resource.getErrorMsg());
                    break;
                case Resource.NETWORK_ERROR:
                    //网络错误时错误信息返回码
                    mViewModel.updateCodeStatus(bean, CodeBean.STATUS_CODE_FAIL);
                    updateCodeStatusItem(bean, CodeBean.STATUS_CODE_FAIL);
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
                    mViewModel.calculationTotal();
                    refreshList(resource.getData());
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
                    dismissLoadingDialog();
                    mViewModel.uploadCodes();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    dismissLoadingDialog();
                    break;
            }
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
                    ToastUtils.showToast(resource.getErrorMsg());
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
                .addView(mBindingView.layoutStockOutFooter.tvScanBack)
                .addView(mBindingView.layoutStockOutFooter.tvInputCode)
                .addView(mBindingView.layoutStockOutFooter.tvSure)
                .submit();
        mBindingView.rvWanweiStockOut.addOnScrollListener(new OnLoadMoreListener() {
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
            preUpload();
        }
    }

    private void preUpload() {
        ConfigurationEntity config = mBindingView.layoutWanweiStockOutHeader.getData();
        if (mViewModel.getSingleNumber() == config.getPlanNumber()) {
            mViewModel.getTaskId();
        } else {
            String details;
            if (mViewModel.getSingleNumber() < config.getPlanNumber()) {
                details = "当前扫码数量尚未满足计划数量，是否确定继续上传？";
            } else {
                details = "当前扫码数量已超出计划数量，是否确定继续上传？";
            }
            CommonDialogUtil.showSelectDialog(this,"提示", details,
                    "返回", "确认上传", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {

                        }

                        @Override
                        public void onRightClick() {
                            mViewModel.getTaskId();
                        }
                    });
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;

        scanCode = CheckCodeUtils.getMatcherDeliveryNumber(scanCode);
        if (CheckCodeUtils.checkCode(scanCode)) {
            mViewModel.requestCodeInfo(scanCode, mAdapter, mBindingView.rvWanweiStockOut);
            ScanCodeService.playSuccess();
        } else {
            CommonDialogUtil.showConfirmDialog(this,"身份码不存在!", scanCode, "知道了", new CommonDialog.OnButtonClickListener() {});
            ScanCodeService.playError();
        }
    }

    /**
     * 获取到码信息成功更新 或网络错误 码更新为错误状态
     */
    private void updateCodeStatusItem(CodeBean bean, int status) {
        WanWeiStockOutEntity temp = new WanWeiStockOutEntity();
        temp.setCode(bean.outerCodeId);
        int index = mAdapter.getDataList().indexOf(temp);
        if (index == -1) {
            return;
        }
        WanWeiStockOutEntity entity = mAdapter.getDataList().get(index);
        entity.setCodeStatus(status);
        entity.setSingleNumber(bean.singleNumber);
        mAdapter.notifyItemChanged(index);
    }

    private void loadMore(List<WanWeiStockOutEntity> data) {
        mAdapter.notifyAddListItem(data);
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

    private void refreshList(List<WanWeiStockOutEntity> list) {
        //此处不使用Adapter的原子操作 因为清空集合后 Adapter也会持有一个空界面的item 如果执行清空操作会报错
        //为了简化逻辑清空操作时重新绑定数据源来刷新
        mAdapter.notifyRefreshList(list);
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

    private void showUploadFinishDialog(UploadResultBean data) {
        String details;
        if (data.error == 0) {
            details = "您成功上传了" + data.success + "条数据.\n请稍后查看任务状态";

        } else {
            details = "您成功上传了" + data.success + "条数据.\n其中失败" + data.error + "条";
        }
        CommonDialogUtil.showSelectDialog(this,"数据上传成功", details, "返回", "查看状态", new CommonDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onRightClick() {
                MainActivity.start(WanWeiStockOutPDAActivity.this, 0);
                TaskListActivity.start(TaskListViewModel.TYPE_TASK_STOCK_OUT, WanWeiStockOutPDAActivity.this);
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

    private void jumpScanBack() {
        if (mViewModel.getList().isEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        CommonScanBackActivity.start(this, 1, CommonScanBackActivity.TYPE_WANWEI_STOCK_OUT, mViewModel.getConfigId());
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
                        MainActivity.start(WanWeiStockOutPDAActivity.this, 0);
                    }

                    @Override
                    public void onRightClick() {
                        preUpload();
                    }
                });
    }

    private void calculationTotal() {
        mViewModel.calculationTotal();
    }

    private void updateTotal(Map<String, Long> map) {
        Long codeNumber1 = map.get("codeNumber");
        long codeNumber = codeNumber1 == null ? 0 : codeNumber1;
        Long singleNumber1 = map.get("singleNumber");
        long singleNumber = singleNumber1 == null ? 0 : singleNumber1;
        mViewModel.setScanSingleNumber(singleNumber);
        Spanned text = Html.fromHtml("采集数:<font color='#03A9F4'>" + codeNumber + "</font>" +
                "<br/>" +
                "子码数量:<font color='#03A9F4'>" + singleNumber + "</font>");
        mBindingView.layoutStockOutFooter.tvCodeAmount.setText(text);
    }

    public static void start(Activity context, long configId, int requestCode) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, WanWeiStockOutPDAActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            context.startActivityForResult(intent, requestCode);
        }
    }

}
