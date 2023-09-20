package com.jgw.delingha.module.stock_out.order.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CommonOrderStockOutListBean;
import com.jgw.delingha.module.scan_back.ui.CommonOrderProductScanBackActivity;
import com.jgw.delingha.module.select_list.scan_rule.ScanRuleSelectListActivity;
import com.jgw.delingha.module.stock_out.order.model.CommonOrderStockOutModel;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.module.wait_upload_task.model.OrderTaskWaitUploadModel;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class CommonOrderStockOutDetailsViewModel extends BaseViewModel {

    private final CommonOrderStockOutModel model;
    private final OrderTaskWaitUploadModel mOrderTaskWaitUploadModel;
    private List<OrderStockOutProductInfoEntity> mList;

    private final MutableLiveData<CommonOrderStockOutListBean> mGetOrderStockOutLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mGetLocalOrderStockOutLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutEntity> mGetOrderStockOutDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutEntity> mGetLocalProductListLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutEntity> mUploadLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutEntity> mInsertOrderDataLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutEntity> mOrderInfoChangeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mProductInfoLiveData = new MutableLiveData<>();

    private final MutableLiveData<OrderStockOutProductInfoEntity> mInputNumberLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutEntity> mRefreshProductCodeNumberLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutProductInfoEntity> mCheckScanCodeNumberLiveData = new MutableLiveData<>();

    private OrderStockOutEntity orderStockOutEntity;

    public CommonOrderStockOutDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new CommonOrderStockOutModel();
        mOrderTaskWaitUploadModel = new OrderTaskWaitUploadModel();
    }

    public void getOrderStockOutInfo(CommonOrderStockOutListBean bean) {
        mGetOrderStockOutLiveData.setValue(bean);
    }

    public LiveData<Resource<OrderStockOutEntity>> getOrderStockOutLiveData() {
        return Transformations.switchMap(mGetOrderStockOutLiveData, model::getOrderStockOut);
    }

    public void getLocalOrderStockOutInfo(long id) {
        mGetLocalOrderStockOutLiveData.setValue(id);
    }

    public LiveData<Resource<OrderStockOutEntity>> getLocalOrderStockOutLiveData() {
        return Transformations.switchMap(mGetLocalOrderStockOutLiveData, model::getOrderStockOut);
    }


    public void getProductList(OrderStockOutEntity data) {
        mGetOrderStockOutDetailsLiveData.setValue(data);
    }

    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> getProductListLiveData() {
        return Transformations.switchMap(mGetOrderStockOutDetailsLiveData,
                model::getOrderStockOutDetails);
    }

    public void getLocalProductList() {
        mGetLocalProductListLiveData.setValue(orderStockOutEntity);
    }

    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> getLocalProductListLiveData() {
        return Transformations.switchMap(mGetLocalProductListLiveData, model::getLocalProductList);
    }

    public void setDataList(List<OrderStockOutProductInfoEntity> dataList) {
        mList = dataList;
    }

    public void upload() {
        if (checkScanCodeListEmpty()) {
            ToastUtils.showToast("请先扫码");
            return;
        }
        if (checkCodeLimit()) {
            return;
        }
        if (checkCodeStatus()){
            return;
        }
        mUploadLiveData.setValue(orderStockOutEntity);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData,s-> mOrderTaskWaitUploadModel.upload(s, TaskListViewModel.TYPE_TASK_STOCK_OUT, CommonOrderProductScanBackActivity.TYPE_STOCK_OUT));
    }

    private boolean checkCodeLimit() {
        for (int i = 0; i < mList.size(); i++) {
            OrderStockOutProductInfoEntity listBean = mList.get(i);
            if (listBean.getTotalCurrentSingleNumber() != 0 && checkCodeScanRule(listBean.getPlanNumber() - listBean.getTotalSingleNumber())) {
                ToastUtils.showToast(listBean.getProductName() + "不符合当前扫码规则!");
                return true;
            }
        }
        return false;
    }
    private boolean checkCodeStatus() {
        for (int i = 0; i < mList.size(); i++) {
            OrderStockOutProductInfoEntity listBean = mList.get(i);
            for (BaseCodeEntity b:listBean.getCodeList()){
                if (b.getCodeStatus()!=BaseCodeEntity.STATUS_CODE_SUCCESS){
                    ToastUtils.showToast(listBean.getProductName() + "有码未校验成功!请删除或前往扫码页重新校验!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据计划数量差额判断是否符合扫码规则
     *
     * @param planSurplus 计划数量-扫码数量
     * @return true 表示校验不通过 false 符合当前扫码规则
     */
    public boolean checkCodeScanRule(int planSurplus) {
        int type = MMKVUtils.getInt(ConstantUtil.SCAN_RULE);
        switch (type) {
            case ScanRuleSelectListActivity.LESS_OR_EQUAL:
                return planSurplus < 0;
            case ScanRuleSelectListActivity.EQUAL:
                return planSurplus != 0;
            case ScanRuleSelectListActivity.GREATER_OR_EQUAL:
                return planSurplus > 0;
        }
        return false;
    }



    public boolean checkScanCodeListEmpty() {
        for (OrderStockOutProductInfoEntity bean : mList) {
            if (bean.getTotalCurrentSingleNumber() != 0) {
                return false;
            }
        }
        return true;
    }

    public void insertOrderData(OrderStockOutEntity headerBean) {
        mInsertOrderDataLiveData.setValue(headerBean);
    }

    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> getInsertOrderDataLiveData() {
        return Transformations.switchMap(mInsertOrderDataLiveData, input -> model.insertOrderData(input, mList));
    }

    public void onOrderInfoChange() {
        mOrderInfoChangeLiveData.setValue(orderStockOutEntity);
    }

    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> getOrderInfoChangeLiveData() {
        return Transformations.switchMap(mOrderInfoChangeLiveData, input -> model.onOrderInfoChange(input, mList));
    }

    public void getProductInfo(long productId) {
        mProductInfoLiveData.setValue(productId);
    }

    public LiveData<Resource<OrderStockOutProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mProductInfoLiveData, model::getProductInfo);
    }

    public void setOrderData(OrderStockOutEntity orderBean) {
        orderStockOutEntity =orderBean;
    }


    public void inputNumber(OrderStockOutProductInfoEntity input) {
        mInputNumberLiveData.setValue(input);
    }

    public LiveData<Resource<OrderStockOutProductInfoEntity>> getInputNumberLiveData() {
        return Transformations.switchMap(mInputNumberLiveData, model::inputNumber);
    }
    public void checkScanCodeNumber(OrderStockOutProductInfoEntity input) {
        mCheckScanCodeNumberLiveData.setValue(input);
    }

    public LiveData<Resource<OrderStockOutProductInfoEntity>> getCheckScanCodeNumberLiveData() {
        return Transformations.switchMap(mCheckScanCodeNumberLiveData, model::checkScanCodeNumber);
    }

    public void refreshProductCodeNumber(OrderStockOutEntity entity) {
        mRefreshProductCodeNumberLiveData.setValue(entity);
    }

    public LiveData<Resource<String>> getRefreshProductCodeNumberLiveData() {
        return Transformations.switchMap(mRefreshProductCodeNumberLiveData, model::refreshProductCodeNumber);
    }
}
