package com.jgw.delingha.custom_module.delingha.breed.in.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.BreedInDetailsBean;
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class BreedInListViewModel extends BaseViewModel {
    private final BreedInListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mBreedInDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteBreedInLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public BreedInListViewModel(@NonNull Application application) {
        super(application);
        model = new BreedInListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedInListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getBreedInList(search, mPage));
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


    public void getBreedInDetails(String breedInRecId) {
        mBreedInDetailsLiveData.setValue(breedInRecId);
    }


    public LiveData<Resource<BreedInDetailsBean>> getBreedInDetailsLiveData() {
        return Transformations.switchMap(mBreedInDetailsLiveData, model::getBreedInDetails);
    }

    public void deleteBreedIn(String breedInRecId) {
        mDeleteBreedInLiveData.setValue(breedInRecId);
    }


    public LiveData<Resource<String>> getDeleteBreedInLiveData() {
        return Transformations.switchMap(mDeleteBreedInLiveData, model::deleteBreedIn);
    }
}
