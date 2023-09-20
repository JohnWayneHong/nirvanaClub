package com.jgw.delingha.custom_module.delingha.select_list.breed_in_batch;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.custom_module.delingha.breed.in.list.BreedInListModel;
import com.jgw.delingha.custom_module.delingha.breed.out.list.BreedOutListModel;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class BreedInBatchListViewModel extends BaseSelectItemListViewModel {

    private final BreedInBatchListModel model;

    private String PigstyId = "";

    public BreedInBatchListViewModel(@NonNull Application application) {
        super(application);
        model = new BreedInBatchListModel();
    }

    public void setSelectByFence(String PigstyId) {
        this.PigstyId = PigstyId;
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        return model.getBreedInByFenceList(mSearchStr,mCurrentPage,PigstyId);
    }
}
