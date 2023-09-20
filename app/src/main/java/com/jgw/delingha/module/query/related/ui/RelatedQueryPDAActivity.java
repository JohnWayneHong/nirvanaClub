package com.jgw.delingha.module.query.related.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.databinding.ActivityRelatedQueryPdaBinding;
import com.jgw.delingha.module.query.related.event.RelatedQueryEvent;
import com.jgw.delingha.module.query.related.viewmodel.RelatedQueryPDAViewModel;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RelatedQueryPDAActivity extends BaseActivity<RelatedQueryPDAViewModel, ActivityRelatedQueryPdaBinding> {

    private boolean isShowSingle;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.related_query_title));
        setRight(ResourcesUtils.getString(R.string.related_query_input));
    }

    @Override
    protected void initData() {
        isShowSingle = getIntent().getBooleanExtra("isShowSingle", false);
        mViewModel.setShowSingle(isShowSingle);
    }

    @Override
    protected void initListener() {
        super.initListener();

    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getTaskList().observe(this, new Observer<Resource<CodeRelationInfoResultBean>>() {
            @Override
            public void onChanged(Resource<CodeRelationInfoResultBean> codeRelationInfoResultBeanResource) {
                if (codeRelationInfoResultBeanResource.getLoadingStatus() == Resource.SUCCESS) {
                    if (codeRelationInfoResultBeanResource.getData() == null) {
                        ToastUtils.showToast("该码没有与其它码关联。");
                        return;
                    }
                    EventBus.getDefault().post(new RelatedQueryEvent.ScanCodeSuccessEvent(codeRelationInfoResultBeanResource.getData()));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.tv_toolbar_right) {
            showInputDialog(fm);
        }
    }

    public void showInputDialog(FragmentManager fm) {
        CommonDialogUtil.showInputDialog(this, "身份码", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {

            @Override
            public boolean onInput(String input) {
                EventBus.getDefault().post(new CommonEvent.ScanQRCodeEvent(input));
                return true;
            }
        });
    }


    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        if (CheckCodeUtils.checkCode(code)) {
            code = CheckCodeUtils.getMatcherDeliveryNumber(code);
            ScanCodeService.playSuccess();
            mViewModel.handleScanQRCode(code);
        } else {
            ScanCodeService.playError();
            CommonDialogUtil.showConfirmDialog(this, "身份码不存在!", code, "知道了", new CommonDialog.OnButtonClickListener() {});
        }
    }

    @Subscribe
    public void onScanCodeSuccess(RelatedQueryEvent.ScanCodeSuccessEvent event) {
        RelatedQueryListActivity.start(this, event.bean, isShowSingle);
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

    public static void start(Context context, boolean isShowSingle) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, RelatedQueryPDAActivity.class);
            intent.putExtra("isShowSingle", isShowSingle);
            context.startActivity(intent);
        }
    }
}
