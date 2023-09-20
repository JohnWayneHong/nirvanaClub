package com.jgw.delingha.module.batch_management.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.BatchManagementBean;
import com.jgw.delingha.module.batch_management.model.BatchManagementModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class BatchManagementListViewModel extends BaseViewModel {
    private final BatchManagementModel model;
    private final MutableLiveData<String> mSearchLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDeleteLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<BatchManagementBean> mList = new ArrayList<>();
    private String mSearch;

    public BatchManagementListViewModel(@NonNull Application application) {
        super(application);
        model = new BatchManagementModel();
    }

    public int getCurrentPage() {
        return mPage;
    }

    public void setDataList(List<BatchManagementBean> dataList) {
        mList = dataList;
    }

    public LiveData<Resource<List<BatchManagementBean>>> getBatchManagementLiveData() {
        return Transformations.switchMap(mSearchLiveData, search -> model.getBatchManagementList(search, mPage));
    }

    public void loadList(){
        mSearchLiveData.setValue(mSearch);
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        loadList();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        loadList();
    }

    public void refreshList() {
        mPage = 1;
        loadList();
    }

    public void deleteBatch(int id) {
        mDeleteLiveData.setValue(id);
    }
    public LiveData<Resource<String>> getDeleteLiveData() {
        return Transformations.switchMap(mDeleteLiveData, model::deleteBatch);
    }
}
