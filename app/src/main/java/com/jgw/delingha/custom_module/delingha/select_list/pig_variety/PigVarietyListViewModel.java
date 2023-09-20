package com.jgw.delingha.custom_module.delingha.select_list.pig_variety;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class PigVarietyListViewModel extends BaseSelectItemListViewModel {

    private final PigVarietyListModel model;
    private String mProductSortId;

    public PigVarietyListViewModel(@NonNull Application application) {
        super(application);
        model = new PigVarietyListModel();
    }
    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getPigVarietyList(mSearchStr,mCurrentPage,mProductSortId);
    }

    public void setProductSortId(String productSortId) {
        mProductSortId =productSortId;
    }
}
