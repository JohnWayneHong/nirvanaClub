package com.jgw.delingha.module.login.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrganizationBean;
import com.jgw.delingha.bean.SystemBean;
import com.jgw.delingha.databinding.ActivitySelectOrgSystemBinding;
import com.jgw.delingha.module.login.event.SelectOrgSystemEvent;
import com.jgw.delingha.module.login.viewmodel.SelectOrgSystemViewModel;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.utils.PickerUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SelectOrgSystemFragment extends BaseFragment<SelectOrgSystemViewModel, ActivitySelectOrgSystemBinding> {

    private static final int TYPE_LIST_ORGANIZATION = 1;
    private static final int TYPE_LIST_SYSTEM = 2;

    @Override
    protected void initView() {
    }

    @Override
    protected void initFragmentData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String tempToken = arguments.getString("tempToken");
            mViewModel.setTempToken(tempToken);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvConfirm, mBindingView.llOrg, mBindingView.llSystem)
                .submit();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getOrganizationListLiveData().observe(this, listResource -> {
            //此处只处理成功状态，ERROR沿用model层的super.onError()。
            //noinspection SwitchStatementWithTooFewBranches
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<String> result = new ArrayList<>();
                    for (OrganizationBean.ListBean bean : listResource.getData()) {
                        result.add(bean.organizationFullName);
                    }
                    showDataPicker(getActivity(), listResource.getData(), result, TYPE_LIST_ORGANIZATION);
                    break;
            }
        });

        mViewModel.getSystemListLiveData().observe(this, listResource -> {
            //此处只处理成功状态，ERROR沿用model层的super.onError()。
            //noinspection SwitchStatementWithTooFewBranches
            switch (listResource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    List<String> result = new ArrayList<>();
                    for (SystemBean.ListBean bean : listResource.getData()) {
                        result.add(bean.sysName);
                    }
                    showDataPicker(getActivity(), listResource.getData(), result, TYPE_LIST_SYSTEM);
                    break;
            }
        });

        mViewModel.getSubmitOrgSystemLiveData().observe(this, orgAndSysBeanResource -> {
            switch (orgAndSysBeanResource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    MMKVUtils.save(ConstantUtil.ORGANIZATION_NAME, mViewModel.getOrganizationFullName());
                    MMKVUtils.save(ConstantUtil.ORGANIZATION_ID, mViewModel.getOrganizationId());
                    MMKVUtils.save(ConstantUtil.SYSTEM_NAME, mViewModel.getSystemName());
                    MMKVUtils.save(ConstantUtil.SYSTEM_ID, mViewModel.getSystemId());
                    MMKVUtils.save(ConstantUtil.USER_TOKEN, orgAndSysBeanResource.getData().token);
                    MMKVUtils.save(ConstantUtil.SYSTEM_EXPIRE_TIME, -1);
                    mViewModel.saveUserEntity();
                    mViewModel.requestCurrentCustomerInfo();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    MMKVUtils.save(ConstantUtil.ORGANIZATION_NAME, "");
                    MMKVUtils.save(ConstantUtil.ORGANIZATION_ID, "");
                    MMKVUtils.save(ConstantUtil.SYSTEM_NAME, "");
                    MMKVUtils.save(ConstantUtil.SYSTEM_ID, "");

                    MMKVUtils.save(ConstantUtil.USER_ENTITY_ID, "");
                    break;
            }
        });

        mViewModel.getRequestCurrentCustomerInfoLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                case Resource.ERROR:
                    dismissLoadingDialog();
                    EventBus.getDefault().post(new SelectOrgSystemEvent.submitOrgSysSuccessEvent());
                    break;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ll_org) {
            mViewModel.getOrganizationList();
        } else if (id == R.id.ll_system) {
            trySelectSystem();
        } else if (id == R.id.tv_confirm) {
            mViewModel.submitOrgSystem();
        } else if (id == R.id.iv_toolbar_back) {
            onBack();
        }
    }

    private void trySelectSystem() {
        if (TextUtils.isEmpty(mBindingView.tvOrgName.getText().toString())) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.select_org_system_tip));
            return;
        }
        mViewModel.getSystemList();
    }

    private void showDataPicker(Context context, List originList, List<String> list, int type) {
        PickerUtils.showDataPicker(context, list, position -> {
            if (type == TYPE_LIST_ORGANIZATION) {
                mViewModel.setOrganizationFullName(list.get(position));
                OrganizationBean.ListBean organizationBean = (OrganizationBean.ListBean) originList.get(position);
                mViewModel.setOrganizationId(organizationBean.organizationId);
                MMKVUtils.save(ConstantUtil.ORGANIZATION_ICON, organizationBean.logo);
                mViewModel.setSystemId("");
                mViewModel.setSystemName("");

                mBindingView.tvOrgName.setText(mViewModel.getOrganizationFullName());
                mBindingView.tvSystemName.setText(mViewModel.getSystemName());
            } else if (type == TYPE_LIST_SYSTEM) {
                mViewModel.setSystemName(list.get(position));
                mViewModel.setSystemId(((SystemBean.ListBean) originList.get(position)).sysId);
                mBindingView.tvSystemName.setText(mViewModel.getSystemName());
            }
            mBindingView.tvConfirm.setEnabled(checkInputInfo());
        });
    }

    private boolean checkInputInfo() {
        return !TextUtils.isEmpty(mBindingView.tvOrgName.getText().toString())
                && !TextUtils.isEmpty(mBindingView.tvSystemName.getText().toString());
    }

    private void onBack() {
        MMKVUtils.save(ConstantUtil.USER_TOKEN, "");
        Activity activity = getActivity();
        if (activity != null && isActivityNotFinished()) {
            activity.onBackPressed();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mBindingView.tvOrgName.setText("");
        mBindingView.tvSystemName.setText("");
    }
}
