package com.jgw.delingha.custom_module.delingha.order_stock_in.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.OrderStockInListBean;
import com.jgw.delingha.custom_module.delingha.order_stock_in.model.OrderStockInModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockInViewModel extends BaseViewModel {
    private final OrderStockInModel model;
    private final MutableLiveData<String> searchLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<OrderStockInListBean.ListBean> mList = new ArrayList<>();
    private String mSearch;

    public OrderStockInViewModel(@NonNull Application application) {
        super(application);
        model = new OrderStockInModel();
    }

    public void setDataList(List<OrderStockInListBean.ListBean> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<OrderStockInListBean.ListBean>>> getInventoryList() {
        return Transformations.switchMap(searchLiveData, search -> model.getOrderStockInList(search, mPage));
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

    public void refreshList() {
        mPage = 1;
        searchLiveData.setValue(mSearch);
    }


}
