package com.jgw.delingha.module.scan_back.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.module.scan_back.model.CommonScanBackModel;

import java.util.List;

public class CommonJsonScanBackViewModel extends BaseViewModel {

    private final CommonScanBackModel model;
    public List<CodeBean> mList;
    private final MutableLiveData<Long> mCalculationTotalLiveData = new ValueKeeperLiveData<>();

    public CommonJsonScanBackViewModel(@NonNull Application application) {
        super(application);
        model = new CommonScanBackModel();
    }

    public void setDataList(List<CodeBean> list) {
        mList = list;
    }

    public void handleScanQRCode(String code, CustomRecyclerAdapter<?> adapter) {
        int i = mList.indexOf(new CodeBean(code));
        if (i != -1) {
            adapter.notifyRemoveItem(i);
        }
        calculationTotal();
    }

    /**
     * 查询统计firstId之后本次扫码数量
     */
    public void calculationTotal() {
        mCalculationTotalLiveData.setValue(null);
    }

    public LiveData<Resource<Long>> getCalculationTotalLiveData() {

        return Transformations.switchMap(mCalculationTotalLiveData, input -> model.calculationTotalByList(mList));
    }


}
