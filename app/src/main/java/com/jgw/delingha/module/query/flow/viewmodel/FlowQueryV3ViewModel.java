package com.jgw.delingha.module.query.flow.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.FlowQueryBean;
import com.jgw.delingha.module.query.flow.model.FlowQueryV3Model;

/**
 * author : Cxz
 * data : 2019/11/29
 * description :
 */
public class FlowQueryV3ViewModel extends BaseViewModel {

    private FlowQueryV3Model model;
    private ValueKeeperLiveData<String> mGetFlowInfoLiveData = new ValueKeeperLiveData<>();

    public FlowQueryV3ViewModel(@NonNull Application application) {
        super(application);
        model = new FlowQueryV3Model();
    }


    public void queryFlow(String code) {
        mGetFlowInfoLiveData.setValue(code);
    }
    public LiveData<Resource<FlowQueryBean>> getFlowInfoLiveData(){
        return Transformations.switchMap(mGetFlowInfoLiveData, model::QueryFlow);
    }

}
