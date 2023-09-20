package com.jgw.delingha.module.wait_upload_task.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.WaitUploadMenuBean;
import com.jgw.delingha.module.wait_upload_task.model.WaitUploadMenuModel;

import java.util.List;

public class WaitUploadMenuViewModel extends BaseViewModel {

    private final WaitUploadMenuModel mModel;
    private final MutableLiveData<Long> mLoadListLiveData = new ValueKeeperLiveData<>();

    public WaitUploadMenuViewModel(@NonNull Application application) {
        super(application);
        mModel = new WaitUploadMenuModel();
    }

    public void loadList() {
        mLoadListLiveData.setValue(null);
    }

    public LiveData<Resource<List<WaitUploadMenuBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mLoadListLiveData, input -> mModel.getListData());
    }

}

