package com.jgw.delingha.module.select_list.product_batch_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class ProductBatchSelectListViewModel extends BaseSelectItemListViewModel {

    private final ProductBatchSelectListModel model;
    private String mProductId;

    public ProductBatchSelectListViewModel(@NonNull Application application) {
        super(application);
        model = new ProductBatchSelectListModel();
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public void loadList() {
        mGetListLiveData.setValue(mCurrentPage);
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getProductBatchList(mSearchStr, mProductId, mCurrentPage);
    }
}

