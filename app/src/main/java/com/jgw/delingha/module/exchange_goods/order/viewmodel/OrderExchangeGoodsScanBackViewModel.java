package com.jgw.delingha.module.exchange_goods.order.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.utils.click_utils.listener.OnItemSingleClickListener;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.module.exchange_goods.order.adapter.OrderExchangeGoodsScanBackAdapter;

import java.util.List;

public class OrderExchangeGoodsScanBackViewModel extends BaseViewModel implements OnItemSingleClickListener {

    private List<OrderExchangeGoodsCodeBean> mList;
    private OrderExchangeGoodsScanBackAdapter mAdapter;

    private final MutableLiveData<Integer> codeCountLiveData = new MutableLiveData<>();

    public LiveData<Integer> getCodeCountLiveData() {
        return codeCountLiveData;
    }

    public OrderExchangeGoodsScanBackViewModel(@NonNull Application application) {
        super(application);
    }

    public OrderExchangeGoodsScanBackAdapter initAdapter() {
        if (mAdapter == null) {
            mAdapter = new OrderExchangeGoodsScanBackAdapter();
            mAdapter.setOnItemClickListener(this);
            mList = mAdapter.getDataList();
        }
        return mAdapter;
    }

    public void setCodeList(List<OrderExchangeGoodsCodeBean> list) {
        mAdapter.notifyRefreshList(list);
        updateCountView();
    }

    public void handleScanQRCode(String code) {
        OrderExchangeGoodsCodeBean bean = new OrderExchangeGoodsCodeBean(code);
        int i = mList.indexOf(bean);
        if (i != -1) {
            mAdapter.notifyRemoveItem(i);
        }
        updateCountView();
    }

    public List<OrderExchangeGoodsCodeBean> getList() {
        return mList;
    }

    public void clearList() {
        mAdapter.notifyRemoveListItem();
        updateCountView();
    }


    @Override
    public void onItemClick(View view, int position) {
        mAdapter.notifyRemoveItem(position);
        updateCountView();
    }

    private void updateCountView() {
        codeCountLiveData.setValue(mList.size());
    }

    @Override
    public void onItemClick(View view, int groupPosition, int subPosition) {
    }

    @Override
    public void onItemClick(View view, int firstPosition, int secondPosition, int thirdPosition) {
    }
}
