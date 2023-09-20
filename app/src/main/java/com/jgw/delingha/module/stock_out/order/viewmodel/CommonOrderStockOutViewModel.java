package com.jgw.delingha.module.stock_out.order.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.CommonOrderStockOutListBean;
import com.jgw.delingha.module.stock_out.order.model.CommonOrderStockOutModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class CommonOrderStockOutViewModel extends BaseViewModel {
    private final CommonOrderStockOutModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<CommonOrderStockOutListBean> mList = new ArrayList<>();
    private String mSearch;

    public CommonOrderStockOutViewModel(@NonNull Application application) {
        super(application);
        model = new CommonOrderStockOutModel();
    }

    public void setDataList(List<CommonOrderStockOutListBean> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<CommonOrderStockOutListBean>>> getOrderListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getOrderStockOutList(search, mPage));
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        mOrderListLiveData.setValue(search);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        mOrderListLiveData.setValue(mSearch);
    }

    public void refreshList() {
        mPage = 1;
        mOrderListLiveData.setValue(mSearch);
    }


}
