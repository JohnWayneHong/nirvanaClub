package com.jgw.delingha.module.select_list.logistics_company;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

/**
 * author : Cxz
 * data : 2019/11/20
 * description :
 */
public class LogisticsCompanyListViewModel extends BaseSelectItemListViewModel {

    private final LogisticsCompanyListModel model;

    public LogisticsCompanyListViewModel(@NonNull Application application) {
        super(application);
        model = new LogisticsCompanyListModel();
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getLogisticsCompanyList(mSearchStr, mCurrentPage);
    }
}
