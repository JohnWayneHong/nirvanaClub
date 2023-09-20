package com.jgw.delingha.custom_module.delingha.breed.out.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.BreedOutDetailsBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class BreedOutListViewModel extends BaseViewModel {
    private final BreedOutListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mBreedOutDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteBreedOutLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public BreedOutListViewModel(@NonNull Application application) {
        super(application);
        model = new BreedOutListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedOutListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getBreedOutList(search, mPage));
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


    public void getBreedOutDetails(String breedInRecId) {
        mBreedOutDetailsLiveData.setValue(breedInRecId);
    }


    public LiveData<Resource<BreedOutDetailsBean>> getBreedOutDetailsLiveData() {
        return Transformations.switchMap(mBreedOutDetailsLiveData, model::getBreedOutDetails);
    }

    public void deleteBreedOut(String breedInRecId) {
        mDeleteBreedOutLiveData.setValue(breedInRecId);
    }


    public LiveData<Resource<String>> getDeleteBreedOutLiveData() {
        return Transformations.switchMap(mDeleteBreedOutLiveData, model::deleteBreedOut);
    }
}
