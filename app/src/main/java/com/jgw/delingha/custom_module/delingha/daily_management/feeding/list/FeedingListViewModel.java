package com.jgw.delingha.custom_module.delingha.daily_management.feeding.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class FeedingListViewModel extends BaseViewModel {
    private final FeedingListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
//    private final MutableLiveData<String> mSlaughterInDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteSlaughterInLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public FeedingListViewModel(@NonNull Application application) {
        super(application);
        model = new FeedingListModel();
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


//    public void getSlaughterInDetails(String breedInRecId) {
//        mSlaughterInDetailsLiveData.setValue(breedInRecId);
//    }
//
//
//    public LiveData<Resource<SlaughterInDetailsBean>> getSlaughterInDetailsLiveData() {
//        return Transformations.switchMap(mSlaughterInDetailsLiveData, model::getSlaughterInDetails);
//    }

    public void deleteSlaughterIn(String inFactoryId) {
        mDeleteSlaughterInLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteSlaughterInLiveData() {
        return Transformations.switchMap(mDeleteSlaughterInLiveData, model::deleteSlaughterIn);
    }
}
