package com.jgw.delingha.custom_module.delingha.daily_management.health_care.list;

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
 * description : 保健记录 列表 ViewModel
 */
public class HealthCareListViewModel extends BaseViewModel {
    private final HealthCareListModel model;
    private final MutableLiveData<String> mHealthCareListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteHealthCareLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public HealthCareListViewModel(@NonNull Application application) {
        super(application);
        model = new HealthCareListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getListLiveData() {
        return Transformations.switchMap(mHealthCareListLiveData, search -> model.getList(search, mPage));
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        mHealthCareListLiveData.setValue(search);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        mHealthCareListLiveData.setValue(mSearch);
    }

    public void refreshList() {
        mPage = 1;
        mHealthCareListLiveData.setValue(mSearch);
    }

    public void deleteHealthCare(String inFactoryId) {
        mDeleteHealthCareLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteHealthCareLiveData() {
        return Transformations.switchMap(mDeleteHealthCareLiveData, model::deleteHealthCare);
    }
}
