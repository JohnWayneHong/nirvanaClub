package com.jgw.delingha.module.fail_log.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.delingha.bean.FailLogMenuBean;
import com.jgw.delingha.module.fail_log.model.FailLogMenuModel;

import java.util.List;

public class FailLogMenuViewModel extends BaseViewModel {

    private final FailLogMenuModel mModel;

    public FailLogMenuViewModel(@NonNull Application application) {
        super(application);
        mModel = new FailLogMenuModel();
    }

    public LiveData<Resource<List<FailLogMenuBean>>> getFailLogMeanBean() {
        return mModel.getFainMenuBeanListData();
    }

    public void requestFirstData() {
        getFailLogMeanBean();
    }
}

