package com.jgw.delingha.module.inventory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.InventoryDetailsBean;
import com.jgw.delingha.bean.InventoryScanBean;
import com.jgw.delingha.module.inventory.adapter.InventoryPDAListRecyclerAdapter;
import com.jgw.delingha.module.inventory.model.InventoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/10
 */
public class InventoryPDAViewModel extends BaseViewModel {

    private final InventoryModel model;

    private InventoryDetailsBean.ListBean mHeaderBean;
    private List<InventoryScanBean> mList;
    private final MutableLiveData<Integer> mCalculationTotalLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mPostSingleInventoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mPostGroupInventoryLiveData = new MutableLiveData<>();

    //
    public InventoryPDAViewModel(@NonNull Application application) {
        super(application);
        model = new InventoryModel();
    }

    public void setDataList(List<InventoryScanBean> dataList) {
        mList = dataList;
    }

    public void setHeaderData(InventoryDetailsBean.ListBean bean) {
        mHeaderBean = bean;
    }

    public InventoryDetailsBean.ListBean getHeaderBean() {
        return mHeaderBean;
    }

    public void handleScanQRCode(String code, InventoryPDAListRecyclerAdapter adapter, CustomBaseRecyclerView recyclerView) {
        InventoryScanBean bean = new InventoryScanBean(code);
        bean.codeStatus = CodeBean.STATUS_CODE_VERIFYING;
        if (mList.size() == 0) {
            mList.add(0, bean);
            adapter.notifyItemInserted(0);
        } else {
            mList.add(0, bean);
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(1, mList.size());
            recyclerView.scrollToPosition(0);
        }
        calculationTotal();
        mPostSingleInventoryLiveData.setValue(code);
    }

    public boolean checkCodeExisted(String code){
        InventoryScanBean bean = new InventoryScanBean(code);
        if (mList.contains(bean)) {
            ToastUtils.showToast(code + "该码已存在");
            return true;
        }
        return false;
    }

    public LiveData<Resource<InventoryScanBean>> getPostSingleInventoryLiveData() {
        return Transformations.switchMap(mPostSingleInventoryLiveData, input -> model.postSingleInventory(mHeaderBean, input));
    }

    /**
     * 查询统计firstId之后本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, model::getCalculationTotal);
    }


    public void tryAgainUpload() {
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            InventoryScanBean inventoryScanBean = mList.get(i);
            if (inventoryScanBean.codeStatus == CodeBean.STATUS_CODE_FAIL) {
                tempList.add(inventoryScanBean.outerCodeId);
            }
        }
        if (tempList.isEmpty()) {
            ToastUtils.showToast("没有可以重试的数据");
            return;
        }
        mPostGroupInventoryLiveData.setValue(tempList);
    }

    public LiveData<Resource<List<InventoryScanBean>>> getPostGroupInventoryLiveData() {
        return Transformations.switchMap(mPostGroupInventoryLiveData, input -> model.postGroupInventory(mHeaderBean, input));
    }

    public boolean checkScanCodeListCount() {
        for (InventoryScanBean b : mList) {
            if (b.codeStatus != CodeBean.STATUS_CODE_SUCCESS) {
                return false;
            }
        }
        return true;
    }
}
