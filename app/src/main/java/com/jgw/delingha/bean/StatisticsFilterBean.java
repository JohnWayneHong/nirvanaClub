package com.jgw.delingha.bean;

import android.view.View;

import com.jgw.common_library.utils.FormatUtils;
import com.jgw.delingha.utils.PickerUtils;

import java.io.Serializable;

public class StatisticsFilterBean implements Serializable {
    //0 day 1 month 2 year
    public int dateType;
    public String selectMonth;
    public String selectYear;
    public String startDate;
    public String endDate;
    public String productName;

    public String getShowDate(){
        switch (dateType){
            case 0:
                return FormatUtils.formatDateToChineseCharacter(startDate,
                        PickerUtils.PATTERN_DAY,PickerUtils.PATTERN_CHINESE_DAY)
                        +"-"
                        +FormatUtils.formatDateToChineseCharacter(endDate,
                        PickerUtils.PATTERN_DAY,PickerUtils.PATTERN_CHINESE_DAY);
            case 1:
                return FormatUtils.formatDateToChineseCharacter(selectMonth,
                        PickerUtils.PATTERN_MONTH,PickerUtils.PATTERN_CHINESE_MONTH);
            case 2:
                return FormatUtils.formatDateToChineseCharacter(selectYear,
                        PickerUtils.PATTERN_YEAR,PickerUtils.PATTERN_CHINESE_YEAR);
            default:
                return null;
        }
    }
    public String getDateShowDate(){
        String pattern1 = PickerUtils.PATTERN_DAY;
        String pattern2 = PickerUtils.PATTERN_CHINESE_DAY;

        switch (dateType){
            case 0:
                pattern1=PickerUtils.PATTERN_DAY;
                pattern2=PickerUtils.PATTERN_CHINESE_DAY;
                break;
            case 1:
                pattern1=PickerUtils.PATTERN_MONTH;
                pattern2=PickerUtils.PATTERN_CHINESE_MONTH;
                break;
            case 2:
                pattern1=PickerUtils.PATTERN_YEAR;
                pattern2=PickerUtils.PATTERN_CHINESE_YEAR;
        }
        return FormatUtils.formatDateToChineseCharacter(startDate,
                pattern1,pattern2)
                +"-"
                +FormatUtils.formatDateToChineseCharacter(endDate,
                pattern1,pattern2);
    }
    public int getDayVisible(){
        return dateType==0? View.VISIBLE:View.GONE;
    }
    public int getMonthVisible(){
        return dateType==1? View.VISIBLE:View.GONE;
    }
    public int getYearVisible(){
        return dateType==2? View.VISIBLE:View.GONE;
    }
}
