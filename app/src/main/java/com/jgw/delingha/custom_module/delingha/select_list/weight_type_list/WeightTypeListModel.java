package com.jgw.delingha.custom_module.delingha.select_list.weight_type_list;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.WeightTypeBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

public class WeightTypeListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mProductListLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getWeightTypeList() {
        if (mProductListLiveData == null) {
            mProductListLiveData = new ValueKeeperLiveData<>();
        }

        List<WeightTypeBean> list = new ArrayList<>();

        WeightTypeBean bean = new WeightTypeBean();
        bean.weightTypeName = "整体盘点";
        bean.weightTypeId = "2";
        list.add(bean);

        bean = new WeightTypeBean();
        bean.weightTypeName = "按栏抽查";
        bean.weightTypeId = "0";
        list.add(bean);

        mProductListLiveData.postValue(new Resource<>(Resource.SUCCESS,list, null));

        return mProductListLiveData;
    }

}
