package com.jgw.delingha.module.select_list.customer_list;

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
public class CustomerListViewModel extends BaseSelectItemListViewModel {

    private final CustomerListModel model;
    private int mCustomerType = 0;

    public CustomerListViewModel(@NonNull Application application) {
        super(application);
        model = new CustomerListModel();
    }

    /**
     * 请求顾客所需参数
     */
    public void setCustomerType(int customerType) {
        mCustomerType = customerType;
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getCustomerList(mSearchStr, mCustomerType, mCurrentPage);
    }
}
