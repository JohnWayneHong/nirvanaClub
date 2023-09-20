package com.jgw.delingha.module.supplement_to_box.produce.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
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
import com.jgw.delingha.bean.PackageCheckCodeRequestParamBean;
import com.jgw.delingha.bean.ProducePackageCodeInfoBean;
import com.jgw.delingha.module.supplement_to_box.produce.adapter.ProduceSupplementToBoxRecyclerAdapter;
import com.jgw.delingha.module.supplement_to_box.produce.model.ProduceSupplementToBoxModel;
import com.jgw.delingha.sql.operator.ProductPackageOperator;

import java.util.ArrayList;
import java.util.List;

public class ProduceSupplementToBoxViewModel extends BaseViewModel {


    private final ProduceSupplementToBoxModel model;
    private List<CodeBean> mList = new ArrayList<>();
    private String mParentCode;

    private ProducePackageCodeInfoBean mParentCodeInfo;

    private final MutableLiveData<String> mCheckParentCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageCheckCodeRequestParamBean> mCheckSonCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<String>> mUploadLiveData = new ValueKeeperLiveData<>();

    public ProduceSupplementToBoxViewModel(@NonNull Application application) {
        super(application);
        model = new ProduceSupplementToBoxModel();
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

    public LiveData<Resource<ProducePackageCodeInfoBean>> getCheckParentCodeLiveData() {
        return Transformations.switchMap(mCheckParentCodeLiveData, model::checkBoxCode);
    }

    public void setParentCodeInfo(ProducePackageCodeInfoBean data) {
        mParentCodeInfo = data;
    }

    public void onScanCode(String code, ProduceSupplementToBoxRecyclerAdapter adapter, CustomBaseRecyclerView recyclerView) {
        CodeBean codeBean = new CodeBean();
        codeBean.outerCodeId = code;
        codeBean.codeStatus = CodeBean.STATUS_CODE_VERIFYING;
        if (mList.size() == 0) {
            mList.add(0, codeBean);
            adapter.notifyItemChanged(0);
        } else {
            adapter.notifyAddItem(codeBean,0);
            recyclerView.scrollToPosition(0);
        }

        PackageCheckCodeRequestParamBean param = new PackageCheckCodeRequestParamBean();
        param.code = code;
        param.boxCode = mParentCode;
        mCheckSonCodeLiveData.setValue(param);
    }

    public boolean checkCodeExisted(String code) {
        CodeBean codeBean = new CodeBean();
        codeBean.outerCodeId = code;
        if (mList.contains(codeBean)) {
            ToastUtils.showToast("该码已存在");
            return true;
        }
        return false;
    }

    public boolean checkBoxFull() {
        boolean b = ProductPackageOperator.checkPackageRestrictProductId(mParentCodeInfo.productId);
        //判断是否超出包装规格上限
        if (b && (mParentCodeInfo.getPackageSpecificationNumber() - mParentCodeInfo.hasSonQuantity - mList.size()) <= 0) {
            ToastUtils.showToast("补码已满所设定的包装规格!");
            return true;
        }
        return false;
    }

    public LiveData<Resource<String>> getCheckSonCodeLiveData() {
        return Transformations.switchMap(mCheckSonCodeLiveData, model::checkCode);
    }

    public void upload() {
        ArrayList<String> codes = new ArrayList<>();
        for (CodeBean b : mList) {
            if (b.codeStatus == CodeBean.STATUS_CODE_VERIFYING) {
                ToastUtils.showToast("还有码在校验中,请稍后再试");
                return;
            }
            codes.add(b.outerCodeId);
        }
        if (!NetUtils.iConnected()) {
            ToastUtils.showToast("网络繁忙,请重试");
            return;
        }
        if (mParentCodeInfo == null || TextUtils.isEmpty(mParentCodeInfo.outerCode)) {
            ToastUtils.showToast("获取父码信息失败");
            return;
        }
        mUploadLiveData.setValue(codes);
    }


    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, new Function<List<String>, LiveData<Resource<String>>>() {
            @Override
            public LiveData<Resource<String>> apply(List<String> input) {
                return model.upload(input, mParentCodeInfo);
            }
        });
    }

    public void resetParentCodeInfo() {
        mParentCode = "";
        mParentCodeInfo = null;
    }
}
