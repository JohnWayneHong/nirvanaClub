package com.jgw.delingha.module.packaging.statistics.viewmodel;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.PackageStatisticsHeaderBean;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.module.packaging.statistics.model.PackageStatisticsModel;
import com.jgw.delingha.utils.PickerUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageStatisticsViewModel extends BaseViewModel {
    private final PackageStatisticsModel model;
    private BarData todayData;
    private final MutableLiveData<String> mPackageStatisticsTwoDayLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mProductStatisticsLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mTimeStatisticsLiveData = new ValueKeeperLiveData<>();
    private List<PackageStatisticsBean> mTimeList;
    private StatisticsFilterBean mFilterData;

    public PackageStatisticsViewModel(@NonNull @NotNull Application application) {
        super(application);
        model = new PackageStatisticsModel();
    }

    public BarData getTodayStatisticsData() {
        if (todayData == null) {
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            BarDataSet barDataSet = new BarDataSet(barEntries, null);
            barDataSet.setHighlightEnabled(false);
            barDataSet.setDrawIcons(false);
            barDataSet.setColor(Color.parseColor("#3FB27E"));
//            barDataSet.setColors(PackageStatisticsActivity.NORMAL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);
            todayData = new BarData(dataSets);
            todayData.setValueTextSize(10f);
        }
        return todayData;
    }

    public List<BarEntry> formatList(@NonNull List<PackageStatisticsBean> list) {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int x = 0; x < list.size(); x++) {
            PackageStatisticsBean value = list.get(x);
            values.add(new BarEntry(x, value.firstCount));
        }
        return values;
    }

    public void getPackageStatisticsTwoDay() {
        mPackageStatisticsTwoDayLiveData.setValue(null);
    }



    public LiveData<Resource<PackageStatisticsHeaderBean>> getPackageStatisticsTwoDayLiveData() {
        return Transformations.switchMap(mPackageStatisticsTwoDayLiveData, input -> model.getPackageStatisticsTwoDay());
    }

    public void getTodayProductStatistics() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("current",1);
        map.put("pageSize",10);
        map.put("timeType","DAY");

        Calendar instance = Calendar.getInstance();
        Date startDate = instance.getTime();
        String startTime = FormatUtils.formatDate(startDate, PickerUtils.PATTERN_DAY);
        map.put("startTime",startTime);
        String endTime = FormatUtils.formatDate(startDate, PickerUtils.PATTERN_DAY);
        map.put("endTime",endTime);
        mProductStatisticsLiveData.setValue(map);
    }

    public LiveData<Resource<List<PackageStatisticsBean>>> getProductStatisticsLiveData() {
        return Transformations.switchMap(mProductStatisticsLiveData,  model::getProductStatistics);
    }

    public void getMonthTimeStatistics() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("current",1);
        map.put("pageSize",10000);
        map.put("timeType","DAY");

        Calendar instance = Calendar.getInstance();
        Date endDate = instance.getTime();
        String endTime = FormatUtils.formatDate(endDate, PickerUtils.PATTERN_DAY);
        map.put("endTime",endTime);
        instance.add(Calendar.DATE,-30);
        Date startDate = instance.getTime();
        String startTime = FormatUtils.formatDate(startDate, PickerUtils.PATTERN_DAY);
        map.put("startTime",startTime);
        mTimeStatisticsLiveData.setValue(map);
    }

    public LiveData<Resource<List<PackageStatisticsBean>>> getTimeStatisticsLiveData() {
        return Transformations.switchMap(mTimeStatisticsLiveData,  model::getTimeStatistics);
    }

    public void setTimeList(List<PackageStatisticsBean> timeList) {
        mTimeList.clear();
        mTimeList.addAll(timeList);
    }

    public List<PackageStatisticsBean> getTimeList() {
        if (mTimeList==null){
            mTimeList=new ArrayList<>();
        }
        return mTimeList;
    }

    public StatisticsFilterBean getFilterData() {
        if (mFilterData ==null){
            mFilterData =new StatisticsFilterBean();
            Calendar instance = Calendar.getInstance();
            Date endDate = instance.getTime();
            mFilterData.endDate= FormatUtils.formatDate(endDate, PickerUtils.PATTERN_DAY);
            instance.add(Calendar.DATE,-30);
            Date startDate = instance.getTime();
            mFilterData.startDate= FormatUtils.formatDate(startDate, PickerUtils.PATTERN_DAY);
        }
        return mFilterData;
    }
}
