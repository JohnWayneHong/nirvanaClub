package com.jgw.delingha.module.select_list.product_packaging_list;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class ProductPackagingSelectListViewModel extends BaseSelectItemListViewModel {

    private final ProductPackagingSelectListModel model;
    private boolean mHaveStockIn;


    public ProductPackagingSelectListViewModel(@NonNull Application application) {
        super(application);
        model = new ProductPackagingSelectListModel();
    }

    public void setHaveStockIn(boolean haveStockIn) {
        mHaveStockIn = haveStockIn;
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getProductList(mSearchStr, mCurrentPage,mHaveStockIn);
    }
}

