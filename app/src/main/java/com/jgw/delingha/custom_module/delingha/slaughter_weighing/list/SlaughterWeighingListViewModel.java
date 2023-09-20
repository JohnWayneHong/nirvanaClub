package com.jgw.delingha.custom_module.delingha.slaughter_weighing.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.bean.SlaughterWeighingDetailsListBean;
import com.jgw.delingha.bean.SlaughterWeighingListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class SlaughterWeighingListViewModel extends BaseViewModel {
    private final SlaughterWeighingListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mSlaughterWeighingDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteSlaughterWeighingLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public SlaughterWeighingListViewModel(@NonNull Application application) {
        super(application);
        model = new SlaughterWeighingListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getSlaughterWeighingListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getSlaughterWeighingList(search, mPage));
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


    public void getSlaughterWeighingDetails(String breedInRecId) {
        mSlaughterWeighingDetailsLiveData.setValue(breedInRecId);
    }

    public LiveData<Resource<SlaughterWeighingDetailsListBean>> getSlaughterWeighingDetailsLiveData() {
        return Transformations.switchMap(mSlaughterWeighingDetailsLiveData, model::getSlaughterWeighingDetails);
    }

    public void deleteSlaughterWeighing(String inFactoryId) {
        mDeleteSlaughterWeighingLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteSlaughterWeighingLiveData() {
        return Transformations.switchMap(mDeleteSlaughterWeighingLiveData, model::deleteSlaughterWeighing);
    }
}
