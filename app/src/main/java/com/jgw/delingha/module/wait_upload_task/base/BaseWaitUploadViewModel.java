package com.jgw.delingha.module.wait_upload_task.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.sql.entity.BaseOrderEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseWaitUploadViewModel extends BaseViewModel {

    public ValueKeeperLiveData<String> mGetListLiveData = new ValueKeeperLiveData<>();
    public ValueKeeperLiveData<List<BaseOrderEntity>> mDeleteSelectListLiveData = new ValueKeeperLiveData<>();
    public ValueKeeperLiveData<List<BaseOrderEntity>> mUploadSelectListLiveData = new ValueKeeperLiveData<>();
    private List<BaseOrderEntity> mList;

    public BaseWaitUploadViewModel(@NonNull Application application) {
        super(application);
    }

    public void setDataList(List<BaseOrderEntity> dataList) {
        mList =dataList;
    }

    public List<BaseOrderEntity> getDataList() {
        return mList;
    }

    public void loadList() {
        mGetListLiveData.setValue(null);
    }

    public LiveData<Resource<List<? extends BaseOrderEntity>>> getListDataLiveData(){
        return Transformations.switchMap(mGetListLiveData, input -> getCustomListLiveData());
    }

    protected abstract LiveData<Resource<List<? extends BaseOrderEntity>>> getCustomListLiveData();


    public boolean checkSelectList() {
        return getSelectedList().isEmpty();
    }

    public List<BaseOrderEntity> getSelectedList(){
        ArrayList<BaseOrderEntity> temp = new ArrayList<>();
        for (BaseOrderEntity b:getDataList()){
            if (b.getItemSelect()==1){
                temp.add(b);
            }
        }
        return temp;
    }

    public void deleteDataBySelect() {
        mDeleteSelectListLiveData.setValue(getSelectedList());
    }

    public LiveData<Resource<String>> getDeleteListLiveData(){
        return Transformations.switchMap(mDeleteSelectListLiveData, this::getCustomDeleteLiveData);
    }

    protected abstract LiveData<Resource<String>> getCustomDeleteLiveData(List<BaseOrderEntity> list);

}
