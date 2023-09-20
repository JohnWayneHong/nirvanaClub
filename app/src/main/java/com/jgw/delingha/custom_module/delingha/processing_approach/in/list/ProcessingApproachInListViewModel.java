package com.jgw.delingha.custom_module.delingha.processing_approach.in.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.ProcessingApproachInDetailsBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class ProcessingApproachInListViewModel extends BaseViewModel {
    private final ProcessingApproachInListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mProcessingApproachInDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteProcessingApproachInLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public ProcessingApproachInListViewModel(@NonNull Application application) {
        super(application);
        model = new ProcessingApproachInListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getProcessingApproachInListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getProcessingApproachInList(search, mPage));
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


    public void getProcessingApproachInDetails(String breedInRecId) {
        mProcessingApproachInDetailsLiveData.setValue(breedInRecId);
    }


    public LiveData<Resource<ProcessingApproachInDetailsBean>> getProcessingApproachInDetailsLiveData() {
        return Transformations.switchMap(mProcessingApproachInDetailsLiveData, model::getProcessingApproachInDetails);
    }

    public void deleteProcessingApproachIn(String inFactoryId) {
        mDeleteProcessingApproachInLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteProcessingApproachInLiveData() {
        return Transformations.switchMap(mDeleteProcessingApproachInLiveData, model::deleteProcessingApproachIn);
    }
}
