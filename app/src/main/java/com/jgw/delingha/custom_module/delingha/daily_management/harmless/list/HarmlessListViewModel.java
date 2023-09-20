package com.jgw.delingha.custom_module.delingha.daily_management.harmless.list;

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
 * description : 无害化记录 列表 ViewModel
 */
public class HarmlessListViewModel extends BaseViewModel {
    private final HarmlessListModel model;
    private final MutableLiveData<String> mHarmlessListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteHarmlessLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public HarmlessListViewModel(@NonNull Application application) {
        super(application);
        model = new HarmlessListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getListLiveData() {
        return Transformations.switchMap(mHarmlessListLiveData, search -> model.getList(search, mPage));
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        mHarmlessListLiveData.setValue(search);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        mHarmlessListLiveData.setValue(mSearch);
    }

    public void refreshList() {
        mPage = 1;
        mHarmlessListLiveData.setValue(mSearch);
    }

    public void deleteHarmless(String inFactoryId) {
        mDeleteHarmlessLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteHarmlessLiveData() {
        return Transformations.switchMap(mDeleteHarmlessLiveData, model::deleteHarmless);
    }
}
