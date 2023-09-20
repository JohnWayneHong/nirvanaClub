package com.jgw.delingha.module.scan_back.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.click_utils.listener.OnPackageLoadMoreListener;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CommonPackageScanBackCount;
import com.jgw.delingha.databinding.ActivityCommonPackageScanBackBinding;
import com.jgw.delingha.module.scan_back.adapter.CommonPackageScanBackAdapter;
import com.jgw.delingha.module.scan_back.viewmodel.CommonPackageScanBackViewModel;
import com.jgw.delingha.sql.entity.BasePackageCodeEntity;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : J-T
 * @date : 2022/2/21 11:48
 * description :
 */
public class CommonPackageScanBackActivity extends BaseActivity<CommonPackageScanBackViewModel, ActivityCommonPackageScanBackBinding> implements OnPackageLoadMoreListener {

    private CommonPackageScanBackAdapter mAdapter;
    public static final int TYPE_IN_WARE_HOUSE_PACKAGE = 300;
    public static final int TYPE_PACKAGE_STOCK_IN = 301;
    public static final int TYPE_PACKAGE_ASSOCIATION_CUSTOM = 302;
    public static final int TYPE_PACKAGE_ASSOCIATION = 303;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.scan_back_title));
        mBindingView.rvcPackageCommonScanBack.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        long configId = intent.getLongExtra(ConstantUtil.CONFIG_ID, -1);
        int type = intent.getIntExtra("type", -1);
        mViewModel.setTypeAndConfigId(type, configId);
        mAdapter = new CommonPackageScanBackAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvPackageCommonScanBack.setAdapter(mAdapter);
        mViewModel.loadNextPage();
        mBindingView.layoutScanBackFooter.setData(new CommonPackageScanBackCount());
        mViewModel.calculationTotal();
    }

    @Override
    public void initLiveData() {
        mViewModel.getCodeByConfigIdLiveData().observe(this, listResource -> {
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<BasePackageCodeEntity> list = listResource.getData();
                    mViewModel.updateData(list);
                    mAdapter.notifyAddListItem(list);
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(listResource.getErrorMsg());
                    break;
            }

        });

        mViewModel.getCalculateTotalLiveData().observe(this, countResource -> {
            switch (countResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    updateCounts(countResource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(countResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getClearDataLiveData().observe(this, stringResource -> {
            switch (stringResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    clearData();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(stringResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getCodeInfoLiveData().observe(this, beanResource -> {
            switch (beanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    BasePackageCodeEntity codeEntity = beanResource.getData();
                    if (codeEntity.getIsBoxCode()) {
                        //父码
                        showDeleteBoxCodeDialog(codeEntity.getCode());
                    } else {
                        //子码
                        mViewModel.deleteChildCode(codeEntity);
                    }
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(beanResource.getErrorMsg());
                    break;
            }
        });

        mViewModel.getDeleteBoxCodeLiveData().observe(this, beanResource -> {
            switch (beanResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    String boxCode = beanResource.getData();
                    deleteBox(boxCode);
                    mViewModel.calculationTotal();
                    mViewModel.checkNeedChangeBoxByDeleteCode(boxCode);
                    mViewModel.loadNextPage();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(beanResource.getErrorMsg());
                    break;

            }
        });

        mViewModel.getDeleteChildCodeLiveData().observe(this, mapResource -> {
            switch (mapResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    Map<String, BasePackageCodeEntity> data = mapResource.getData();
                    BasePackageCodeEntity deleteCode = data.get("deleteCode");
                    BasePackageCodeEntity addCode = data.get("addCode");
                    deleteAndAddCode(deleteCode, addCode);
                    mViewModel.calculationTotal();
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(mapResource.getErrorMsg());
                    break;

            }
        });
    }


    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutScanBackFooter.tvReturn
                        , mBindingView.layoutScanBackFooter.tvDeleteAll)
                .addOnClickListener()
                .submit();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLoadMoreListener(this);
    }

    @Override
    public void onLoadMore() {
        mViewModel.loadNextPage();
    }

    /**
     * counts 第一项为父码数量 第二项为子码数量
     */
    private void updateCounts(Map<String, Integer> map) {
        mBindingView.layoutScanBackFooter.getData().setData(map);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_return) {
            onBackPressed();
        } else if (id == R.id.tv_delete_all) {
            CommonDialogUtil.showSelectDialog(this,"是否删除所有码?", "若删除,您所录入的码将全部遗失.",
                    "退出", "删除", new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void onLeftClick() {
                        }

                        @Override
                        public void onRightClick() {
                            mViewModel.clearData();
                        }
                    });
        }
    }

    private void clearData() {
        mAdapter.notifyRemoveListItem();
        mViewModel.calculationTotal();
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        if (mAdapter.getDataList().isEmpty()) {
            return;
        }
        String code = event.mCode;
        if (!TextUtils.isEmpty(code)) {//判断码是否为空
            code = CheckCodeUtils.getMatcherDeliveryNumber(code);
        }
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            ScanCodeService.playSuccess();
            mViewModel.getCodeInfo(code);
        } else {
            ScanCodeService.playError();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        BasePackageCodeEntity basePackageCodeEntity = mAdapter.getDataList().get(position);
        Boolean isBoxCode = basePackageCodeEntity.getIsBoxCode();
        if (isBoxCode) {
            showDeleteBoxCodeDialog(basePackageCodeEntity.getCode());
        } else {
            mViewModel.deleteChildCode(basePackageCodeEntity);
        }
    }


    /**
     * 如果没有获取到新增的吗 则需要换箱
     */
    private void deleteAndAddCode(BasePackageCodeEntity deleteCode, BasePackageCodeEntity addCode) {
        mAdapter.notifyRemoveItem(deleteCode);
        if (addCode == null) {
            mViewModel.setNeedChangeBox(true);
        } else {
            mAdapter.notifyAddItem(addCode);
        }
    }

    public void showDeleteBoxCodeDialog(String code) {
        CommonDialogUtil.showSelectDialog(this,"您是否删除父码", "并且删除该码下的所有子码",
                "取消", "确定", new CommonDialog.OnButtonClickListener() {
                    

                    @Override
                    public void onRightClick() {
                        mViewModel.deleteBoxCode(code);
                    }
                });
    }

    private void deleteBox(String code) {
        ArrayList<BasePackageCodeEntity> tempList = mViewModel.getDeleteCodeListByBoxCode(code);
        for (BasePackageCodeEntity entity : tempList) {
            mAdapter.notifyRemoveItem(entity);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
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


    public static void start(Activity context, int requestCode, long configId, int type) {
        if (configId == -1 || type == -1) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.get_config_failed));
            return;
        }
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonPackageScanBackActivity.class);
            intent.putExtra(ConstantUtil.CONFIG_ID, configId);
            intent.putExtra("type", type);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
