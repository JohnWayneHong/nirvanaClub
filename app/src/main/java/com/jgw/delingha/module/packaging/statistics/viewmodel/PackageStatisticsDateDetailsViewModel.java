package com.jgw.delingha.module.packaging.statistics.viewmodel;

import android.app.Application;

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
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.module.packaging.statistics.activity.PackageStatisticsActivity;
import com.jgw.delingha.module.packaging.statistics.model.PackageStatisticsModel;
import com.jgw.delingha.utils.PickerUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageStatisticsDateDetailsViewModel extends BaseViewModel {
    private final PackageStatisticsModel model;
    private BarData barData;
    private final MutableLiveData<Map<String,Object>> mDateStatisticsLiveData = new ValueKeeperLiveData<>();
    private StatisticsFilterBean mFilterData;
    private List<PackageStatisticsBean> mList=new ArrayList<>();

    public PackageStatisticsDateDetailsViewModel(@NonNull @NotNull Application application) {
        super(application);
        model = new PackageStatisticsModel();
    }

    public BarData getTodayStatisticsData() {
        if (barData == null) {
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            BarDataSet barDataSet = new BarDataSet(barEntries, null);
            barDataSet.setHighlightEnabled(false);
            barDataSet.setDrawIcons(false);
            barDataSet.setColors(PackageStatisticsActivity.NORMAL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);
            barData = new BarData(dataSets);
            barData.setValueTextSize(10f);
        }
        return barData;
    }

    public List<BarEntry> formatList(@NonNull List<PackageStatisticsBean> list) {
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int x = 0; x < list.size(); x++) {
            PackageStatisticsBean value = list.get(x);
            values.add(new BarEntry(x, value.firstCount));
        }
        return values;
    }

    public void getDateStatistics() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("current",1);
        map.put("pageSize",10000);
        map.put("timeType", getTimeType(mFilterData.dateType));
        map.put("startTime",getFilterData().startDate);
        map.put("endTime",getFilterData().endDate);
        mDateStatisticsLiveData.setValue(map);
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
    public LiveData<Resource<List<PackageStatisticsBean>>> getDateStatisticsLiveData() {
        return Transformations.switchMap(mDateStatisticsLiveData, model::getTimeStatistics);
    }

    public void setFilterData(StatisticsFilterBean filterData) {
        mFilterData = filterData;
    }

    public StatisticsFilterBean getFilterData() {
        if (mFilterData==null){
            mFilterData=new StatisticsFilterBean();
            Calendar instance = Calendar.getInstance();
            Date endDate = instance.getTime();
            mFilterData.endDate= FormatUtils.formatDate(endDate, PickerUtils.PATTERN_DAY);
            instance.add(Calendar.DATE,-30);
            Date startDate = instance.getTime();
            mFilterData.startDate= FormatUtils.formatDate(startDate, PickerUtils.PATTERN_DAY);
        }
        return mFilterData;
    }

    public void setList(List<PackageStatisticsBean> data) {
        if (data==null){
            return;
        }
        mList.clear();
        mList.addAll(data);
    }

    public List<PackageStatisticsBean> getList() {
        return mList;
    }
}
