package com.jgw.delingha.module.packaging.statistics.view.barchart.value_format;

import android.text.TextUtils;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.StatisticsFilterBean;

import java.util.List;

public class MyAxisValueFormatter extends ValueFormatter {


    private final List<PackageStatisticsBean> mList;
    private StatisticsFilterBean mFilterData;

    public MyAxisValueFormatter(List<PackageStatisticsBean> list, StatisticsFilterBean filterData) {
        mList = list;
        mFilterData = filterData;
    }

    public void setFilterData(StatisticsFilterBean filterData) {
        mFilterData = filterData;
    }

    @Override
    public String getFormattedValue(float value) {
        if (value>=mList.size()){
            return "";
        }
        String str="";
        if (mFilterData == null) {
            return mList.get((int) value).packageTime;
        } else {
            switch (mFilterData.dateType) {
                case 0:
                    str= mList.get((int) value).packageTime;
                    break;
                case 1:
                    str= mList.get((int) value).month;
                    break;
                case 2:
                    str= mList.get((int) value).year;
                    break;
            }
        }
        if (TextUtils.isEmpty(str)){
            return "";
        }
        return str;
    }
}
