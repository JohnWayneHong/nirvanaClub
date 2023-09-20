package com.jgw.delingha.custom_module.delingha.order_stock_in.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.delingha.bean.OrderStockInDetailsBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.custom_module.delingha.order_stock_in.model.OrderStockInModel;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockInDetailsViewModel extends BaseViewModel {

    private final OrderStockInModel model;
    private List<OrderStockInDetailsBean.ListBean> mList;

    private final MutableLiveData<String> mGetOrderStockInDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<OrderStockInDetailsBean.ListBean>> uploadLiveData = new MutableLiveData<>();
    private String mHouseList;
    private String inHouseList;
    private String wareHouseInId;

    public OrderStockInDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new OrderStockInModel();
    }

    public void getOrderStockInDetails(String houseList) {
        mHouseList = houseList;
        mGetOrderStockInDetailsLiveData.setValue(houseList);
    }

    public LiveData<Resource<List<OrderStockInDetailsBean.ListBean>>> getOrderDetails() {
        return Transformations.switchMap(mGetOrderStockInDetailsLiveData, model::getOrderStockInDetails);
    }

    public void setDataList(List<OrderStockInDetailsBean.ListBean> dataList) {
        mList = dataList;
    }

    public void upload(String inHouseList, String wareHouseInId) {
        this.inHouseList = inHouseList;
        this.wareHouseInId = wareHouseInId;
        uploadLiveData.setValue(null);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(uploadLiveData, input -> model.upload(inHouseList,wareHouseInId));
    }

    public boolean checkScanCodeListEmpty() {
        for (OrderStockInDetailsBean.ListBean bean : mList) {
            if (!bean.codeList.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
