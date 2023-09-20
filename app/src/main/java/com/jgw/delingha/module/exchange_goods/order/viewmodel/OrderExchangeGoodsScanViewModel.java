package com.jgw.delingha.module.exchange_goods.order.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.module.exchange_goods.order.adapter.OrderExchangeGoodsScanRecyclerAdapter;
import com.jgw.delingha.module.exchange_goods.order.model.OrderExchangeGoodsScanModel;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/10
 */
public class OrderExchangeGoodsScanViewModel extends BaseViewModel {

    private final OrderExchangeGoodsScanModel model;
    private OrderExchangeGoodsScanRecyclerAdapter mAdapter;

    private final MutableLiveData<Integer> codeCountLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> codeLiveData = new MutableLiveData<>();
    private List<OrderExchangeGoodsCodeBean> mList;
    private OrderExchangeGoodsDetailsBean.ProductsBean mBean;

    public OrderExchangeGoodsScanViewModel(@NonNull Application application) {
        super(application);
        model = new OrderExchangeGoodsScanModel();
    }

    public LiveData<Integer> getCodeCountLiveData() {
        return codeCountLiveData;
    }


    public LiveData<Resource<OrderExchangeGoodsCodeBean>> getCodeInfoLiveData() {
        return Transformations.switchMap(codeLiveData, new Function<String, LiveData<Resource<OrderExchangeGoodsCodeBean>>>() {
            @Override
            public LiveData<Resource<OrderExchangeGoodsCodeBean>> apply(String input) {
                return model.getCodeInfo(input, mBean.productId);
            }
        });
    }


    public OrderExchangeGoodsScanRecyclerAdapter initAdapter(OrderExchangeGoodsDetailsBean.ProductsBean bean) {
        if (mAdapter == null) {
            mBean = bean;
            mAdapter = new OrderExchangeGoodsScanRecyclerAdapter();
            mList = mAdapter.getDataList();
            mList.addAll(bean.codeList);
        }
        return mAdapter;
    }

    public List<OrderExchangeGoodsCodeBean> getList() {
        return mList;
    }

    public void onScanBackResult(List<OrderExchangeGoodsCodeBean> list) {
        if (list == null) {
            mAdapter.notifyRemoveListItem();
        } else {
            mAdapter.notifyRefreshList(list);
        }
        updateCountView();
    }

    public void updateCountView() {
        codeCountLiveData.setValue(mList.size());
        mBean.scanSingleCodeNumber = getSingleCodeCount();
    }

    public void handleScanQRCode(String code) {
        OrderExchangeGoodsCodeBean bean = new OrderExchangeGoodsCodeBean(code);
        bean.codeStatus = CodeBean.STATUS_CODE_VERIFYING;
        bean.productName = mBean.productName;
        bean.productCode = mBean.productCode;
        mAdapter.notifyAddItem(bean, 0);
        updateCountView();
        codeLiveData.setValue(code);
    }

    public boolean checkCodeExisted(String code) {
        OrderExchangeGoodsCodeBean bean = new OrderExchangeGoodsCodeBean(code);
        if (mList.contains(bean)) {
            ToastUtils.showToast(code + "该码已存在");
            return true;
        }
        return false;
    }

    private int getSingleCodeCount() {
        int size = 0;
        for (int j = 0; j < mList.size(); j++) {
            OrderExchangeGoodsCodeBean temp = mList.get(j);
            size += temp.singleCodeNumber;
        }
        return size;
    }

    public void updateCodeStatus(Resource<OrderExchangeGoodsCodeBean> bean) {
        int index = mList.indexOf(bean.getData());
        switch (bean.getLoadingStatus()) {
            case Resource.SUCCESS:
                if (index != -1) {
                    OrderExchangeGoodsCodeBean orderExchangeGoodsCodeBean = mList.get(index);
                    orderExchangeGoodsCodeBean.codeStatus = CodeBean.STATUS_CODE_SUCCESS;
                    orderExchangeGoodsCodeBean.singleCodeNumber = bean.getData().singleCodeNumber;
                    orderExchangeGoodsCodeBean.sysVersion = bean.getData().sysVersion;
                    orderExchangeGoodsCodeBean.currentLevel = bean.getData().currentLevel;
                    mAdapter.notifyRefreshItem(orderExchangeGoodsCodeBean);
                }
                break;
            case Resource.ERROR:
                if (index != -1) {
                    mAdapter.notifyRemoveItem(index);
                }
                break;
        }
        updateCountView();
    }

    /**
     * 是否全部校验完成
     *
     * @return
     */
    public boolean checkScanCodeListStatus() {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).codeStatus != CodeBean.STATUS_CODE_SUCCESS) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否达到订单数量
     *
     * @return
     */
    public boolean checkScanCodeListCount() {
        int size = getSingleCodeCount();
        return size >= (mBean.singleCodeNumber - mBean.actualSingleCodeNumber);
    }
}
