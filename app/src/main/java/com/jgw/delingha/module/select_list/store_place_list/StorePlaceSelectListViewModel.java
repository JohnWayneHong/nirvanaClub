package com.jgw.delingha.module.select_list.store_place_list;


import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class StorePlaceSelectListViewModel extends BaseSelectItemListViewModel {

    private final StorePlaceSelectListModel model;
    private String mWareHouseId;

    public StorePlaceSelectListViewModel(@NonNull Application application) {
        super(application);
        model = new StorePlaceSelectListModel();
    }

    public void setWareHouseId(String warehouseId) {
        if (TextUtils.isEmpty(warehouseId)) {
            return;
        }
        mWareHouseId = warehouseId;
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getStorePlaceList(mSearchStr, mWareHouseId, mCurrentPage);
    }
}

