package com.jgw.delingha.module.exchange_goods.order.viewmodel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.listener.OnItemSingleClickListener;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsBean;
import com.jgw.delingha.module.exchange_goods.order.adapter.OrderExchangeGoodsRecyclerAdapter;
import com.jgw.delingha.module.exchange_goods.order.model.OrderExchangeGoodsModel;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsViewModel extends BaseViewModel implements OnItemSingleClickListener {
    private final OrderExchangeGoodsModel model;
    private final MutableLiveData<String> searchLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> itemClickLiveData = new MutableLiveData<>();
    private OrderExchangeGoodsRecyclerAdapter mAdapter;
    private int mPage = 1;
    private List<OrderExchangeGoodsBean.ListBean> mList;
    private String mSearch;

    public OrderExchangeGoodsViewModel(@NonNull Application application) {
        super(application);
        model = new OrderExchangeGoodsModel();
    }

    public CustomRecyclerAdapter<OrderExchangeGoodsBean.ListBean> initAdapter() {
        if (mAdapter == null) {
            mAdapter = new OrderExchangeGoodsRecyclerAdapter();
            mAdapter.setOnItemClickListener(this);
            mList = mAdapter.getDataList();
        }
        return mAdapter;
    }

    public LiveData<String> getClickItemLiveData() {
        return itemClickLiveData;
    }

    public LiveData<Resource<OrderExchangeGoodsBean>> getOrderList() {
        return Transformations.switchMap(searchLiveData, search -> model.getOrderList(search, mPage));
    }

    public void setOrderList(List<OrderExchangeGoodsBean.ListBean> list) {
        if (mPage == 1) {
            mAdapter.notifyRemoveListItem();
        }
        mAdapter.notifyRefreshList(list);
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        searchLiveData.setValue(search);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        searchLiveData.setValue(mSearch);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.tv_order_exchange_goods_goto:
                OrderExchangeGoodsBean.ListBean listBean = mList.get(position);
                itemClickLiveData.setValue(listBean.houseList);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int groupPosition, int subPosition) {

    }

    @Override
    public void onItemClick(View view, int firstPosition, int secondPosition, int thirdPosition) {

    }

    public void refreshList() {
        mPage = 1;
        searchLiveData.setValue(mSearch);
    }
}
