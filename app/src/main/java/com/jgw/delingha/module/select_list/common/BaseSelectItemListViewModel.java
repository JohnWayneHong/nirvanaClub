package com.jgw.delingha.module.select_list.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;

import java.util.List;

/**
 * author : Cxz
 * data : 2019/11/20
 * description :
 */
public abstract class BaseSelectItemListViewModel extends BaseViewModel {

    private List<SelectItemSupport> mList;
    public String mSearchStr;
    public int mCurrentPage = 1;
    public ValueKeeperLiveData<Integer> mGetListLiveData = new ValueKeeperLiveData<>();

    public BaseSelectItemListViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDataList(List<SelectItemSupport> dataList) {
        mList = dataList;
    }

    public void loadList() {
        mGetListLiveData.setValue(mCurrentPage);
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getListDataLiveData(){
        return Transformations.switchMap(mGetListLiveData, input -> getModelLiveData());

    }

    protected abstract LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData();


    public void doSearch(String str) {
        mSearchStr = str;
        onRefresh();
    }

    public void onRefresh() {
        mCurrentPage = 1;
        loadList();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        loadList();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
