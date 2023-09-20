package com.jgw.delingha.custom_module.delingha.daily_management.immunity.details;

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
 * description : 免疫记录 详情 ViewModel
 */
public class ImmunityDetailsViewModel extends BaseViewModel {
    private final ImmunityDetailsModel model;

    private final MutableLiveData<FeedingListBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    public ImmunityDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new ImmunityDetailsModel();
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
        map.put("animalsId", adapter.searchValueIdByKey("免疫对象"));
        map.put("animalsValue", adapter.searchValueStrByKey("免疫对象"));
        map.put("immunoTime", adapter.searchValueStrByKey("免疫日期"));
        map.put("immunoNumber", adapter.searchValueStrByKey("免疫数量"));
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
