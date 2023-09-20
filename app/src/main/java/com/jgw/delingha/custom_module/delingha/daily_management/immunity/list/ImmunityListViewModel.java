package com.jgw.delingha.custom_module.delingha.daily_management.immunity.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 免疫记录 列表 ViewModel
 */
public class ImmunityListViewModel extends BaseViewModel {
    private final ImmunityListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteImmunityLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public ImmunityListViewModel(@NonNull Application application) {
        super(application);
        model = new ImmunityListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getList(search, mPage));
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

    public void deleteImmunity(String inFactoryId) {
        mDeleteImmunityLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteImmunityLiveData() {
        return Transformations.switchMap(mDeleteImmunityLiveData, model::deleteImmunity);
    }
}
