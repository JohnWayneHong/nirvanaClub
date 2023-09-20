package com.jgw.delingha.custom_module.delingha.select_list.pigsty_list;

import static com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity.TYPE_IN_BATCH;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListViewModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

public class PigstyListViewModel extends BaseSelectItemListViewModel {

    private final PigstyListModel model;

    private String requestCode;

    public PigstyListViewModel(@NonNull Application application) {
        super(application);
        model = new PigstyListModel();
    }

    public void selectInOrOut(String requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    protected LiveData<Resource<List<? extends SelectItemSupport>>> getModelLiveData() {
        if (requestCode.equals(TYPE_IN_BATCH)) {
            return model.getOutList(mSearchStr,mCurrentPage);
        }
        return model.getList(mSearchStr,mCurrentPage);
    }

}
