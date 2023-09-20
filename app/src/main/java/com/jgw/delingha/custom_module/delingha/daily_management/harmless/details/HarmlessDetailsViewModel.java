package com.jgw.delingha.custom_module.delingha.daily_management.harmless.details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.bean.FeedingListBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.options_details.utils.OptionsDetailsUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 无害化记录 详情 ViewModel
 */
public class HarmlessDetailsViewModel extends BaseViewModel {
    private final HarmlessDetailsModel model;
    private String pigstyId = "";
    private String pigstyName = "";
    private final MutableLiveData<FeedingListBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetBatchByCodeLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetBreedInByPigstyListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    public HarmlessDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new HarmlessDetailsModel();
    }

    public void setPigstyId(String pigstyId) {
        this.pigstyId = pigstyId;
    }

    public String getPigstyId() {
        return pigstyId;
    }

    public void setPigstyName(String pigstyName) {
        this.pigstyName = pigstyName;
    }

    public String getPigstyName() {
        return pigstyName;
    }

    //从model获取数据
    public void loadList(FeedingListBean data) {
        mContentListLiveData.setValue(data);
    }

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mContentListLiveData, model::getListData);
    }

    public void getBatchByCode(String code) {
        mGetBatchByCodeLiveData.setValue(code);
    }

    public LiveData<Resource<BreedInAssociateBean>> getBatchByCodeLiveData() {
        return Transformations.switchMap(mGetBatchByCodeLiveData, model::getBatchByCode);
    }

    public void getBreedInByPigstyList(String pigstyId) {
        mGetBreedInByPigstyListLiveData.setValue(pigstyId);
    }

    public LiveData<Resource<BreedInListBean>> getBreedInByPigstyListLiveData() {
        return Transformations.switchMap(mGetBreedInByPigstyListLiveData, model::getBreedInDataByPigstyId);
    }

    public void submit(InfoDetailsRecyclerAdapter adapter) {
        List<InfoDetailsDemoBean> dataList = adapter.getDataList();
        if (OptionsDetailsUtils.checkItem(dataList)){
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("fenceName", adapter.searchValueStrByKey("栏舍号"));
        map.put("fenceId", adapter.searchValueIdByKey("栏舍号"));
        map.put("breedInBatch", adapter.searchValueStrByKey("养殖批次"));
        map.put("animalsId", adapter.searchValueIdByKey("处理对象"));
        map.put("animalsValue", adapter.searchValueStrByKey("处理对象"));
        map.put("innocentNumber", adapter.searchValueStrByKey("处理数量"));
        map.put("innocentTime", adapter.searchValueStrByKey("处理日期"));
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
