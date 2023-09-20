package com.jgw.delingha.module.select_list.organization_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.OrganizationBean;

import java.util.List;

/**
 * author : Cxz
 * data : 2019/11/20
 * description :
 */
public class OrganizationListViewModel extends BaseViewModel {

    private final OrganizationListModel model;
    private int mCurrentPage = 1;
    private List<OrganizationBean.ListBean> mList;
    private String mSearchStr;
    private final ValueKeeperLiveData<Integer> mGetListLiveData = new ValueKeeperLiveData<>();

    public OrganizationListViewModel(@NonNull Application application) {
        super(application);
        model = new OrganizationListModel();
    }

    public void setDataList(List<OrganizationBean.ListBean> dataList) {
        mList = dataList;
    }

    public void loadList() {
        mGetListLiveData.setValue(mCurrentPage);
    }

    public LiveData<Resource<List<OrganizationBean.ListBean>>> getListDataLiveData() {
        return Transformations.switchMap(mGetListLiveData, input -> model.getOrganizationList(mSearchStr, mCurrentPage));
    }

    public void doSearch(String str) {
        mSearchStr = str;
        onRefresh();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mCurrentPage) {
            return;
        }
        mCurrentPage++;
        loadList();
    }

    public void onRefresh() {
        mCurrentPage = 1;
        loadList();
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
