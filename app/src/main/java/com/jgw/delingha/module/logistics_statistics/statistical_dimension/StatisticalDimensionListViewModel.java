package com.jgw.delingha.module.logistics_statistics.statistical_dimension;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.StatisticsParamsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Cxz
 * data : 2019/11/20
 * description :
 */
public class StatisticalDimensionListViewModel extends BaseViewModel {

    private final StatisticalDimensionListModel model;
    private int mCurrentPage = 1;
    private List<StatisticsParamsBean.StatisticalDimension> mList;
    private final List<StatisticsParamsBean.StatisticalDimension> mSelectList=new ArrayList<>();
    private String mSearchStr;
    private ValueKeeperLiveData<Integer> mGetListLiveData = new ValueKeeperLiveData<>();
    private int mType;

    public StatisticalDimensionListViewModel(@NonNull Application application) {
        super(application);
        model = new StatisticalDimensionListModel();
    }

    public void setStatisticalDimensionType(int type) {
        mType = type;
    }

    public void setDataList(List<StatisticsParamsBean.StatisticalDimension> dataList) {
        mList = dataList;
    }

    public void loadList() {
        mGetListLiveData.setValue(mCurrentPage);
    }

    public LiveData<Resource<List<StatisticsParamsBean.StatisticalDimension>>> getListDataLiveData() {
        return Transformations.switchMap(mGetListLiveData, input -> model.getStatisticalDimensionList(mType, mSearchStr, mCurrentPage));
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

    public void updateSelectData(StatisticsParamsBean.StatisticalDimension bean) {
        if (bean.select) {
            mSelectList.add(bean);
        }else {
            mSelectList.remove(bean);
        }
    }

    public List<StatisticsParamsBean.StatisticalDimension> getSelectList() {
        return mSelectList;
    }
}
