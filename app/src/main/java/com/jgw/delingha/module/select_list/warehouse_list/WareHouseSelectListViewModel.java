package com.jgw.delingha.module.select_list.warehouse_list;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class WareHouseSelectListViewModel extends BaseSelectItemListViewModel {

    private final WareHouseSelectListModel model;

    public WareHouseSelectListViewModel(@NonNull Application application) {
        super(application);
        model = new WareHouseSelectListModel();
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getWareHouseList(mSearchStr, mCurrentPage);
    }
}

