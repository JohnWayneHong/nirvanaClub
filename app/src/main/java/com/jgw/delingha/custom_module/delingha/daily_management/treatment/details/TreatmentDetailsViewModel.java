package com.jgw.delingha.custom_module.delingha.daily_management.treatment.details;

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
 * description : 诊疗记录 详情 ViewModel
 */
public class TreatmentDetailsViewModel extends BaseViewModel {
    private final TreatmentDetailsModel model;

    private final MutableLiveData<FeedingListBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    public TreatmentDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new TreatmentDetailsModel();
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
        map.put("animalsId", adapter.searchValueIdByKey("诊疗对象"));
        map.put("animalsValue", adapter.searchValueStrByKey("诊疗对象"));
        map.put("treatTime", adapter.searchValueStrByKey("诊疗日期"));
        map.put("attackNumber", adapter.searchValueStrByKey("诊疗数量"));
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
