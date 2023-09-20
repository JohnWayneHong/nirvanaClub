package com.jgw.delingha.custom_module.delingha.order_stock_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.model.OrderStockOutModel;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockOutDetailsViewModel extends BaseViewModel {

    private final OrderStockOutModel model;
    private List<OrderStockOutDetailsBean.ListBean> mList;

    private final MutableLiveData<OrderStockOutListBean.ListBean> mGetOrderStockInDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<OrderStockOutDetailsBean.ListBean>> mSaveCheckLiveData = new MutableLiveData<>();
    private final MutableLiveData<OrderStockOutDetailsBean.ListBean> mUploadLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<OrderStockOutDetailsBean.ListBean>> mUploadAllLiveData = new MutableLiveData<>();
    private OrderStockOutListBean.ListBean mBean;

    public OrderStockOutDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new OrderStockOutModel();
    }

    public void getOrderStockOutDetails(OrderStockOutListBean.ListBean bean) {
        mBean = bean;
        mGetOrderStockInDetailsLiveData.setValue(bean);
    }

    public LiveData<Resource<List<OrderStockOutDetailsBean.ListBean>>> getOrderDetails() {
        return Transformations.switchMap(mGetOrderStockInDetailsLiveData, model::getOrderStockOutDetails);
    }

    public void setDataList(List<OrderStockOutDetailsBean.ListBean> dataList) {
        mList = dataList;
    }

    public void uploadAll() {
//        if (checkCodeLimit()){
//            return;
//        }
        mUploadAllLiveData.setValue(mList);
    }

    //    private boolean checkCodeLimit() {
//        for (int i = 0; i < mList.size(); i++) {
//            OrderStockOutDetailsBean.ListBean listBean = mList.get(i);
//            if (checkScanNumber(listBean)) {
//                ToastUtils.showToast(listBean.productName + "扫码数量超过计划数量,请处理后上传");
//                return true;
//            }
//        }
//        return false;
//    }
    public String checkCodeFull() {
        for (int i = 0; i < mList.size(); i++) {
            OrderStockOutDetailsBean.ListBean listBean = mList.get(i);
            if (checkScanNumberIsFull(listBean)) {
                return listBean.productName + "扫码数量不满足计划数量,请确认是否上传?";
            }
        }
        return null;
    }

    private boolean checkScanNumber(OrderStockOutDetailsBean.ListBean bean) {
        int scanFirst = 0;
        int scanSecond = 0;
        int scanThird = 0;
        for (OrderStockScanBean b : bean.codeList) {
            scanFirst += b.firstLevel;
            scanSecond += b.secondLevel;
            scanThird += b.thirdLevel;
        }
        int firstCount = bean.planFirstOutNumber - bean.firstOutNumber - scanFirst;
        if (firstCount < 0) {
            return true;
        }
        int secondCount = bean.planSecondOutNumber - bean.secondOutNumber - scanSecond;
        if (secondCount < 0) {
            return true;
        }
        int thirdCount = bean.planThirdOutNumber - bean.thirdOutNumber - scanThird;
        if (thirdCount < 0) {
            return true;
        }
        return false;
    }

    public boolean checkScanNumberIsFull(OrderStockOutDetailsBean.ListBean bean) {
        int scanFirst = 0;
        int scanSecond = 0;
        int scanThird = 0;
        for (OrderStockScanBean b : bean.codeList) {
            scanFirst += b.firstLevel;
            scanSecond += b.secondLevel;
            scanThird += b.thirdLevel;
        }
        int firstCount = bean.planFirstOutNumber - bean.firstOutNumber - scanFirst;
        if (firstCount > 0) {
            return true;
        }
        int secondCount = bean.planSecondOutNumber - bean.secondOutNumber - scanSecond;
        if (secondCount > 0) {
            return true;
        }
        int thirdCount = bean.planThirdOutNumber - bean.thirdOutNumber - scanThird;
        if (thirdCount > 0) {
            return true;
        }
        return false;
    }

    public LiveData<Resource<String>> getUploadAllLiveData() {
        return Transformations.switchMap(mUploadAllLiveData, input -> model.uploadAll( mBean.outHouseList));
    }

    public void saveCheck() {
//        if (checkCodeLimit()){
//            return;
//        }
        mSaveCheckLiveData.setValue(mList);
    }
    public LiveData<Resource<String>> getSaveCheckLiveData() {
        return Transformations.switchMap(mSaveCheckLiveData, input -> model.saveCheck(input, mBean.outHouseList));
    }

    public void upload(OrderStockOutDetailsBean.ListBean bean) {
        mUploadLiveData.setValue(bean);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, input -> model.upload(input, mBean.outHouseList));
    }
}
