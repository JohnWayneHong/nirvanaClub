package com.jgw.delingha.custom_module.delingha.order_stock_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.custom_module.delingha.order_stock_out.model.OrderStockOutModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockOutViewModel extends BaseViewModel {
    private final OrderStockOutModel model;
    private final MutableLiveData<String> searchLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<OrderStockOutListBean.ListBean> mList = new ArrayList<>();
    private String mSearch;

    public OrderStockOutViewModel(@NonNull Application application) {
        super(application);
        model = new OrderStockOutModel();
    }

    public void setDataList(List<OrderStockOutListBean.ListBean> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<OrderStockOutListBean.ListBean>>> getOrderStockOutList() {
        return Transformations.switchMap(searchLiveData, search -> model.getOrderStockOutList(search, mPage));
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
