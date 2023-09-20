package com.jgw.delingha.custom_module.delingha.select_list.employee_list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class EmployeeListViewModel extends BaseSelectItemListViewModel {

    private final EmployeeListModel model;

    public EmployeeListViewModel(@NonNull Application application) {
        super(application);
        model = new EmployeeListModel();
    }
    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getEmployeeList(mSearchStr,mCurrentPage);
    }

}
