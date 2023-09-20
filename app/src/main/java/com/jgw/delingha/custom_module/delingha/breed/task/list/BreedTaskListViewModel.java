package com.jgw.delingha.custom_module.delingha.breed.task.list;

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
import java.util.HashMap;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023-8-8 10:03:40
 * description : 养殖任务 列表 ViewModel
 */
public class BreedTaskListViewModel extends BaseViewModel {
    private final BreedTaskListModel model;
    private final MutableLiveData<String> mTaskListLiveData = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String,Object>> mCompleteBreedTaskLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private String mTaskType = "";
    private int mTaskStatus = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public BreedTaskListViewModel(@NonNull Application application) {
        super(application);
        model = new BreedTaskListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public String getTaskType() {
        return mTaskType;
    }

    public int getTaskStatus() {
        return mTaskStatus;
    }

    public void setTaskType(String mTaskType) {
        this.mTaskType = mTaskType;
    }

    public void setTaskStatus(int mTaskStatus) {
        this.mTaskStatus = mTaskStatus;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedTaskListLiveData() {
        return Transformations.switchMap(mTaskListLiveData, search -> model.getBreedTaskList(search, mPage,getTaskType(),getTaskStatus()));
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        mTaskListLiveData.setValue(search);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        mTaskListLiveData.setValue(mSearch);
    }

    public void refreshList() {
        mPage = 1;
        mTaskListLiveData.setValue(mSearch);
    }


    public void completeBreedTask(HashMap<String,Object> hashMap) {
        mCompleteBreedTaskLiveData.setValue(hashMap);
    }


    public LiveData<Resource<String>> getCompleteBreedTaskLiveData() {
        return Transformations.switchMap(mCompleteBreedTaskLiveData, model::completeBreedTask);
    }
}
