package com.jgw.delingha.custom_module.delingha.select_list.slaughter_weighing;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class SlaughterWeighingBatchListViewModel extends BaseSelectItemListViewModel {

    private final SlaughterWeighingBatchListModel model;

    public SlaughterWeighingBatchListViewModel(@NonNull Application application) {
        super(application);
        model = new SlaughterWeighingBatchListModel();
    }
    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getSlaughterWeighingList(mSearchStr,mCurrentPage);
    }
}
