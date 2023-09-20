package com.jgw.delingha.module.select_list.product_list;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class ProductSelectListViewModel extends BaseSelectItemListViewModel {

    private final ProductSelectListModel model;


    public ProductSelectListViewModel(@NonNull Application application) {
        super(application);
        model = new ProductSelectListModel();
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getProductList(mSearchStr, mCurrentPage);
    }

}

