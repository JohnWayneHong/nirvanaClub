package com.jgw.delingha.module.packaging.statistics.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.module.packaging.statistics.model.PackageStatisticsModel;
import com.jgw.delingha.utils.PickerUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageStatisticsProductDetailsViewModel extends BaseViewModel {
    private final PackageStatisticsModel model;
    private final MutableLiveData<Map<String, Object>> mPackageStatisticsTwoDayLiveData = new ValueKeeperLiveData<>();
    private StatisticsFilterBean mFilterData;
    private List<PackageStatisticsBean> mRealList;
    private List<PackageStatisticsBean> mShowList;

    public PackageStatisticsProductDetailsViewModel(@NonNull @NotNull Application application) {
        super(application);
        model = new PackageStatisticsModel();
    }

    public void getProductStatistics() {
        StatisticsFilterBean filterData = getFilterData();
        HashMap<String, Object> map = new HashMap<>();
        map.put("current", 1);
        map.put("pageSize", 10000);
        map.put("timeType", getTimeType(mFilterData.dateType));
        map.put("startTime", mFilterData.dateType==0?filterData.startDate:
                mFilterData.dateType==1?mFilterData.selectMonth:mFilterData.selectYear);
        if (filterData.dateType == 0) {
            map.put("endTime", filterData.endDate);
        }
        mPackageStatisticsTwoDayLiveData.setValue(map);


    }

    private String getTimeType(int type) {
        switch (type) {
            case 0:
                return "DAY";
            case 1:
                return "MONTH";
            case 2:
                return "YEAR";
            default:
                return "DAY";
        }
    }

    public LiveData<Resource<List<PackageStatisticsBean>>> getProductStatisticsLiveData() {
        return Transformations.switchMap(mPackageStatisticsTwoDayLiveData, model::getProductStatistics);
    }

    public void setFilterData(StatisticsFilterBean filterData) {
        mFilterData = filterData;
    }

    public StatisticsFilterBean getFilterData() {
        if (mFilterData == null) {
            mFilterData = new StatisticsFilterBean();
            Date date = new Date();
            mFilterData.startDate = FormatUtils.formatDate(date, PickerUtils.PATTERN_DAY);
            mFilterData.endDate = FormatUtils.formatDate(date, PickerUtils.PATTERN_DAY);
        }
        return mFilterData;
    }

    public void setShowList(List<PackageStatisticsBean> list) {
        mShowList = list;
    }

    public void setRealList(List<PackageStatisticsBean> list) {
        mRealList = list;
    }

    public void notifyShowList(CustomRecyclerAdapter<PackageStatisticsBean> adapter) {
        if (mFilterData != null && !TextUtils.isEmpty(mFilterData.productName)) {
            List<PackageStatisticsBean> temp = new ArrayList<>();
            for (int i = 0; i < mRealList.size(); i++) {
                PackageStatisticsBean bean = mRealList.get(i);
                String productName = bean.productName;
                if (productName == null) {
                    break;
                }
                if (productName.contains(mFilterData.productName)) {
                    temp.add(bean);
                }
            }
            adapter.notifyRefreshList(temp);
        } else {
            adapter.notifyRefreshList(mRealList);
        }
    }
}
