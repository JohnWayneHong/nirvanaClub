package com.jgw.delingha.module.query.related.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.module.query.related.model.RelatedQueryModel;


public class RelatedQueryPDAViewModel extends BaseViewModel {

    private RelatedQueryModel mModel;
    private final MutableLiveData<String> mCodeRelationParamsLiveData = new ValueKeeperLiveData<>();
    private boolean isShowSingle;

    public RelatedQueryPDAViewModel(@NonNull Application application) {
        super(application);
        mModel = new RelatedQueryModel();
    }

    public LiveData<Resource<CodeRelationInfoResultBean>> getTaskList() {
        return Transformations.switchMap(mCodeRelationParamsLiveData, code -> mModel.getCodeRelationInfo(code,isShowSingle));
    }

    public void handleScanQRCode(String code) {
        mCodeRelationParamsLiveData.setValue(code);
    }

    public void setShowSingle(boolean isShowSingle) {
        this.isShowSingle = isShowSingle;
    }
}
