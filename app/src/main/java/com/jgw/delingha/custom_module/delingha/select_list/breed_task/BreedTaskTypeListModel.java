package com.jgw.delingha.custom_module.delingha.select_list.breed_task;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.BreedTaskBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.ArrayList;
import java.util.List;

public class BreedTaskTypeListModel {

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mProductListLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getBreedTaskList() {
        if (mProductListLiveData == null) {
            mProductListLiveData = new ValueKeeperLiveData<>();
        }

        List<BreedTaskBean> list = new ArrayList<>();

        BreedTaskBean bean = new BreedTaskBean();
        bean.status = "全部";
        bean.statusId = "0";
        list.add(bean);

        bean = new BreedTaskBean();
        bean.status = "未完成";
        bean.statusId = "1";
        list.add(bean);

        bean = new BreedTaskBean();
        bean.status = "已完成";
        bean.statusId = "2";
        list.add(bean);

        bean = new BreedTaskBean();
        bean.status = "超期完成";
        bean.statusId = "3";
        list.add(bean);

        mProductListLiveData.postValue(new Resource<>(Resource.SUCCESS,list, null));

        return mProductListLiveData;
    }

}
