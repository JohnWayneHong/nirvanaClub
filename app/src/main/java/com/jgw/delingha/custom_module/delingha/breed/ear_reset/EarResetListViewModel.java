package com.jgw.delingha.custom_module.delingha.breed.ear_reset;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.BreedEarAssociateBean;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedInDetailsBean;
import com.jgw.delingha.bean.EnterFenceDetailsBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023-8-4 09:42:37
 * 养殖管理 耳号重置 ViewModel
 */
public class EarResetListViewModel extends BaseViewModel {
    private final EarResetListModel model;

    private final MutableLiveData<List<String>> mRemoveEarCodeListLiveData = new ValueKeeperLiveData<>();

    public EarResetListViewModel(@NonNull Application application) {
        super(application);
        model = new EarResetListModel();
    }

    public void removeEarCodeAssociation(List<String> earList) {
        mRemoveEarCodeListLiveData.setValue(earList);
    }

    public LiveData<Resource<String>> getRemoveEarCodeAssociationLiveData() {
        return Transformations.switchMap(mRemoveEarCodeListLiveData, model::postResetEarCodeAssociation);
    }

}
