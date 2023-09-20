package com.jgw.delingha.module.inventory.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.delingha.bean.InventoryDetailsBean;
import com.jgw.delingha.module.inventory.model.InventoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class InventoryDetailsViewModel extends BaseViewModel {

    private final InventoryModel model;
    private List<InventoryDetailsBean> mList = new ArrayList<>();

    private final MutableLiveData<String> mGetInventoryDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> uploadLiveData = new MutableLiveData<>();

    public InventoryDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new InventoryModel();
    }
    public void getInventoryDetails(String houseList) {
        mGetInventoryDetailsLiveData.setValue(houseList);
    }
    public LiveData<Resource<List<InventoryDetailsBean.ListBean>>> getOrderDetails() {
        return Transformations.switchMap(mGetInventoryDetailsLiveData, model::getInventoryDetails);
    }
}
