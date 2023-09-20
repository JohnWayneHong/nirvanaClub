package com.jgw.delingha.custom_module.delingha.breed.enter_fence.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.EnterFenceDetailsBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023年7月6日13:57:58
 * 入栏记录列表 ViewModel
 */
public class EnterFenceListViewModel extends BaseViewModel {
    private final EnterFenceListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mEnterFenceDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteEnterFenceLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public EnterFenceListViewModel(@NonNull Application application) {
        super(application);
        model = new EnterFenceListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getEnterFenceListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getEnterFenceList(search, mPage));
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


    public void getEnterFenceDetails(String inFenceId) {
        mEnterFenceDetailsLiveData.setValue(inFenceId);
    }

    public LiveData<Resource<EnterFenceDetailsBean>> getEnterFenceDetailsLiveData() {
        return Transformations.switchMap(mEnterFenceDetailsLiveData, model::getEnterFenceDetails);
    }

    public void deleteEnterFence(String inFenceId) {
        mDeleteEnterFenceLiveData.setValue(inFenceId);
    }


    public LiveData<Resource<String>> getDeleteEnterFenceLiveData() {
        return Transformations.switchMap(mDeleteEnterFenceLiveData, model::deleteEnterFence);
    }
}
