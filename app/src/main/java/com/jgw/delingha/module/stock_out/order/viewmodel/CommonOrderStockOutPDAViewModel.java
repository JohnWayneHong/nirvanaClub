package com.jgw.delingha.module.stock_out.order.viewmodel;

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
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.module.stock_out.order.model.CommonOrderStockOutModel;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.BaseOrderScanCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;
import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/10
 */
public class CommonOrderStockOutPDAViewModel extends BaseViewModel {

    private final CommonOrderStockOutModel model;

    private List<BaseOrderScanCodeEntity> mList;
    private final MutableLiveData<Long> mGetProductInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mGetCodeListInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mCalculationTotalLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mSingleCheckCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mGroupCheckCodeLiveData = new MutableLiveData<>();
    private long mProductId;

    private OrderStockOutProductInfoEntity mHeaderData;
    private int mPage = 1;

    public CommonOrderStockOutPDAViewModel(@NonNull Application application) {
        super(application);
        model = new CommonOrderStockOutModel();
    }

    public void setDataList(List<BaseOrderScanCodeEntity> dataList) {
        mList = dataList;
    }

    public void getProductInfo(long productId) {
        mProductId = productId;
        mGetProductInfoLiveData.setValue(productId);
    }

    public LiveData<Resource<OrderStockOutProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mGetProductInfoLiveData, model::getProductInfo);
    }


    public void refreshListByProductId() {
        mPage = 1;
        mGetCodeListInfoLiveData.setValue(mProductId);
    }

    public LiveData<Resource<List<BaseOrderScanCodeEntity>>> getListDataLiveData() {
        return Transformations.switchMap(mGetCodeListInfoLiveData, input -> model.getOrderProductCodeList(input, mPage));
    }

    public void handleScanQRCode(String code, CustomRecyclerAdapter<BaseOrderScanCodeEntity> adapter, CustomBaseRecyclerView recyclerView) {
        OrderStockOutScanCodeEntity entity = model.queryEntityByCode(code);
        if (entity != null) {
            String msg;
            OrderStockOutProductInfoEntity productInfo = entity.getOrderStockOutProductInfoEntity().getTarget();
            OrderStockOutEntity orderInfo = productInfo.getOrderStockOutEntity().getTarget();
            if (orderInfo.getUserEntity().getTargetId() != LocalUserUtils.getCurrentUserId()) {
                msg = code + "该码被其他用户录入,请切换账号或清除离线数据";
            }else if (mHeaderData.getOrderStockOutEntity().getTarget().getId()!=orderInfo.getId()){
                msg = code + "该码已存在"+orderInfo.getOrderCode()+"订单中，不可重复扫码！";
            }else {
                msg = code + "该码已存在，不可重复扫码！";
            }
            ToastUtils.showToast(msg);
            return;
        }
        entity = new OrderStockOutScanCodeEntity();
        entity.setOuterCode(code);
        entity.getOrderStockOutProductInfoEntity().setTargetId(mProductId);
        entity.setCodeStatus(BaseOrderScanCodeEntity.STATUS_CODE_VERIFYING);
        model.insertCodeEntity(entity);
        adapter.notifyAddItem(entity);
        recyclerView.scrollToPosition(0);
        List<BaseOrderScanCodeEntity> tempList = new ArrayList<>();
        if (mList.size() > CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            for (int i = CustomRecyclerAdapter.ITEM_PAGE_SIZE; i < mList.size(); i++) {
                tempList.add(mList.get(i));
            }
            if (!tempList.isEmpty()) {
                mList.removeAll(tempList);
                adapter.notifyItemRangeRemoved(CustomRecyclerAdapter.ITEM_PAGE_SIZE, tempList.size());
            }
        }
        calculationTotal();
        mSingleCheckCodeLiveData.setValue(code);
        mPage = 1;
    }

    public LiveData<Resource<OrderStockOutScanCodeEntity>> getCheckOrderStockOutCodeLiveData() {
        return Transformations.switchMap(mSingleCheckCodeLiveData, input -> model.singleCheckCode(mHeaderData, input));
    }

    /**
     * 查询统计firstId之后本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(mProductId);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {
        return Transformations.switchMap(mCalculationTotalLiveData, model::getCalculationTotal);
    }


    public void tryAgainUpload() {
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            BaseOrderScanCodeEntity entity = mList.get(i);
            if (entity.getCodeStatus() != BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS) {
                tempList.add(entity.getCode());
            }
        }
        if (tempList.isEmpty()) {
            ToastUtils.showToast("没有可以重试的数据!");
            return;
        }
        mGroupCheckCodeLiveData.setValue(tempList);
    }

    public LiveData<Resource<List<OrderStockScanBean>>> getPostGroupCheckCodeLiveData() {
        return Transformations.switchMap(mGroupCheckCodeLiveData, input -> model.groupCheckCode(mHeaderData, input));
    }

    public boolean checkScanCodeListCount() {
        for (BaseOrderScanCodeEntity b : mList) {
            if (b.getCodeStatus() != CodeBean.STATUS_CODE_SUCCESS) {
                return false;
            }
        }
        return true;
    }

    public void setHeaderData(OrderStockOutProductInfoEntity entity) {
        mHeaderData = entity;
    }

    public void updateCodeStatus(OrderStockScanBean bean, int status) {
        model.updateCodeStatus(bean, status);
    }

    public void deleteCode(String code) {
        model.removeEntityByCode(code);
    }

    /**
     * 当前页数据完整时尝试请求下一页数据
     */
    public void loadMoreList() {
        if (mList.size() == mPage * CustomRecyclerAdapter.ITEM_PAGE_SIZE) {
            mPage++;
            mGetCodeListInfoLiveData.setValue(mProductId);
        }
    }

    public int getPage() {
        return mPage;
    }
}
