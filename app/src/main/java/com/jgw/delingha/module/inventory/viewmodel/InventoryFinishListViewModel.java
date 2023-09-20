package com.jgw.delingha.module.inventory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.InventoryCodeBean;
import com.jgw.delingha.module.inventory.model.InventoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class InventoryFinishListViewModel extends BaseViewModel {
    private final InventoryModel model;
    private final MutableLiveData<String> searchLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<InventoryCodeBean.ListBean> mList = new ArrayList<>();
    private String mSearch;
    private String mInventoryProductId;

    public InventoryFinishListViewModel(@NonNull Application application) {
        super(application);
        model = new InventoryModel();
    }

    public void setDataList(List<InventoryCodeBean.ListBean> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<InventoryCodeBean.ListBean>>> getInventoryList() {
        return Transformations.switchMap(searchLiveData, search -> model.getInventoryFinishListByProduct(mInventoryProductId,search, mPage));
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
    public void setInventoryProductId(String inventoryProductId) {
        mInventoryProductId =inventoryProductId;
    }

}
