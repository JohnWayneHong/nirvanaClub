package com.jgw.delingha.module.supplement_to_box.base.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.PackageCodeInfoBean;
import com.jgw.delingha.module.supplement_to_box.base.model.SupplementToBoxModel;
import com.jgw.delingha.sql.adapter.CodeEntityRecyclerAdapter;
import com.jgw.delingha.sql.operator.ProductPackageOperator;

import java.util.ArrayList;
import java.util.List;

public class SupplementToBoxViewModel extends BaseViewModel {


    private final SupplementToBoxModel model;
    private List<CodeBean> mList = new ArrayList<>();
    private String mParentCode;

    private PackageCodeInfoBean mParentCodeInfo;

    private final MutableLiveData<String> mCheckParentCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mCheckSonCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<String>> mUploadLiveData = new ValueKeeperLiveData<>();

    public SupplementToBoxViewModel(@NonNull Application application) {
        super(application);
        model = new SupplementToBoxModel();
    }

    public void setListData(List<CodeBean> codeBeans) {
        mList = codeBeans;
    }

    public boolean hasParentCode() {
        return !TextUtils.isEmpty(mParentCode);
    }

    public void onScanParentCode(String code) {
        mParentCode = code;
        mCheckParentCodeLiveData.setValue(code);
    }

    public LiveData<Resource<PackageCodeInfoBean>> getCheckParentCodeLiveData() {
        return Transformations.switchMap(mCheckParentCodeLiveData, model::getPackageCodeInfo);
    }

    public void setParentCodeInfo(PackageCodeInfoBean data) {
        mParentCodeInfo = data;
    }

    public void onScanCode(String code, CodeEntityRecyclerAdapter<CodeBean> adapter, CustomBaseRecyclerView recyclerView) {
        //是否取消限制
        boolean b = ProductPackageOperator.checkPackageRestrictProductId(mParentCodeInfo.productId);
        //判断是否超出包装规格上限
        if (b && (mParentCodeInfo.lastNumber - mParentCodeInfo.sonCodeNumber - mList.size()) <= 0) {
            ToastUtils.showToast("补码已满所设定的包装规格!");
            return;
        }
        CodeBean codeBean = new CodeBean();
        codeBean.outerCodeId = code;
        codeBean.codeStatus = CodeBean.STATUS_CODE_VERIFYING;
        adapter.notifyAddItem(codeBean);
        recyclerView.scrollToPosition(0);
        mCheckSonCodeLiveData.setValue(code);
    }

    public LiveData<Resource<String>> getCheckSonCodeLiveData() {
        return Transformations.switchMap(mCheckSonCodeLiveData, input -> model.checkSubCodeStatus(input, mParentCodeInfo));
    }

    public boolean checkCodeExisted(String code) {
        CodeBean codeBean = new CodeBean(code);
        if (mList.contains(codeBean)) {
            ToastUtils.showToast("该码已存在");
            return true;
        }
        return false;
    }

    public boolean checkBoxFull() {
        boolean b = ProductPackageOperator.checkPackageRestrictProductId(mParentCodeInfo.productId);
        //判断是否超出包装规格上限
        if (b && (mParentCodeInfo.lastNumber - mParentCodeInfo.sonCodeNumber - mList.size()) <= 0) {
            ToastUtils.showToast("补码已满所设定的包装规格!");
            return true;
        }
        return false;
    }

    public void upload() {
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        if (mParentCodeInfo == null || TextUtils.isEmpty(mParentCodeInfo.outerCodeId)) {
            ToastUtils.showToast("获取父码信息失败");
            return;
        }
        ArrayList<String> codes = new ArrayList<>();
        for (CodeBean b : mList) {
            if (b.codeStatus == CodeBean.STATUS_CODE_VERIFYING) {
                ToastUtils.showToast("还有码在校验中,请稍后再试");
                return;
            }
            codes.add(b.outerCodeId);
        }
        mUploadLiveData.setValue(codes);
    }


    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, input -> model.upload(input, mParentCodeInfo));
    }

    public void resetParentCodeInfo() {
        mParentCode = "";
        mParentCodeInfo = null;
    }
}
