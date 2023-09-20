package com.jgw.delingha.custom_module.delingha.select_list.breed_task;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class BreedTaskTypeListViewModel extends BaseSelectItemListViewModel {

    private final BreedTaskTypeListModel model;

    public BreedTaskTypeListViewModel(@NonNull Application application) {
        super(application);
        model = new BreedTaskTypeListModel();
    }
    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getBreedTaskList();
    }

}
