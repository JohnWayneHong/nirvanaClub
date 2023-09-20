package com.jgw.delingha.custom_module.delingha.daily_management.disinfect.details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.FeedingListBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.options_details.utils.OptionsDetailsUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 消毒记录 详情 ViewModel
 */
public class DisinfectDetailsViewModel extends BaseViewModel {
    private final DisinfectDetailsModel model;

    private final MutableLiveData<FeedingListBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    public DisinfectDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new DisinfectDetailsModel();
    }

    //从model获取数据
    public void loadList(FeedingListBean data) {
        mContentListLiveData.setValue(data);
    }

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mContentListLiveData, model::getListData);
    }

    public void submit(InfoDetailsRecyclerAdapter adapter) {
        List<InfoDetailsDemoBean> dataList = adapter.getDataList();
        if (OptionsDetailsUtils.checkItem(dataList)){
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("fenceName", adapter.searchValueStrByKey("栏舍号"));
        map.put("fenceId", adapter.searchValueIdByKey("栏舍号"));

        map.put("disinfectTime", adapter.searchValueStrByKey("消毒日期"));

        map.put("reasonValue", adapter.searchValueStrByKey("类型"));
        map.put("reasonId", adapter.searchValueIdByKey("类型"));

        map.put("remarks", adapter.searchValueStrByKey("备注"));
        mUploadLiveData.setValue(map);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::postUpload);
    }

    public void setClickBean(InfoDetailsClickBean clickBean) {
        mClickBean = clickBean;
    }

    public InfoDetailsClickBean getClickBean() {
        return mClickBean;
    }
}
