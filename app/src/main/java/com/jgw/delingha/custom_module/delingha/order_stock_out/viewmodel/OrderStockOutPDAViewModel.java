package com.jgw.delingha.custom_module.delingha.order_stock_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.view.CustomBaseRecyclerView;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.model.OrderStockOutModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/10
 */
public class OrderStockOutPDAViewModel extends BaseViewModel {

    private final OrderStockOutModel model;

    private OrderStockOutDetailsBean.ListBean mHeaderBean;
    private List<OrderStockScanBean> mList;
    private final MutableLiveData<Integer> mCalculationTotalLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mSingleCheckCodeLiveData = new MutableLiveData<>();

    public OrderStockOutPDAViewModel(@NonNull Application application) {
        super(application);
        model = new OrderStockOutModel();
    }

    public void setDataList(List<OrderStockScanBean> dataList) {
        mList = dataList;
    }

    public void setHeaderData(OrderStockOutDetailsBean.ListBean bean) {
        mHeaderBean = bean;
    }

    public void handleScanQRCode(String code, CustomRecyclerAdapter<?> adapter, CustomBaseRecyclerView recyclerView) {
        OrderStockScanBean bean = new OrderStockScanBean(code);
        if (mList.contains(bean)) {
            ToastUtils.showToast(code + "该码已存在");
            return;
        }
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
        mSingleCheckCodeLiveData.setValue(code);
    }

    public LiveData<Resource<OrderStockScanBean>> getCheckOrderStockInCodeLiveData() {
        return Transformations.switchMap(mSingleCheckCodeLiveData, input -> model.singleCheckCode(mHeaderBean, input));
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

    public boolean checkScanCodeListCount() {
        for (OrderStockScanBean b : mList) {
            if (b.codeStatus != CodeBean.STATUS_CODE_SUCCESS) {
                return false;
            }
        }
        return true;
    }
}
