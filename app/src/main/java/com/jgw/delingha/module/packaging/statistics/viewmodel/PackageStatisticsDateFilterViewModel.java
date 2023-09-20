package com.jgw.delingha.module.packaging.statistics.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.utils.PickerUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class PackageStatisticsDateFilterViewModel extends BaseViewModel {

    private StatisticsFilterBean mData;

    public PackageStatisticsDateFilterViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public StatisticsFilterBean getData() {
        return mData;
    }

    public void setData(StatisticsFilterBean data) {
        mData =data;
    }

    public String switchStrPattern() {
        switch (mData.dateType) {
            case 0:
                return PickerUtils.PATTERN_DAY;
            case 1:
                return PickerUtils.PATTERN_MONTH;
            case 2:
                return PickerUtils.PATTERN_YEAR;
            default:
                return "";
        }
    }

    public boolean checkFilterData() {
        if (mData.dateType == 0) {
            if (TextUtils.isEmpty(mData.startDate) || TextUtils.isEmpty(mData.endDate)) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return true;
            }
            Date startDate = FormatUtils.decodeDate(mData.startDate, PickerUtils.PATTERN_DAY);
            Calendar instance = Calendar.getInstance();
            instance.setTime(startDate);
            instance.add(Calendar.DATE, 30);
            Date startRangeTime = instance.getTime();
            Date endDate = FormatUtils.decodeDate(mData.endDate, PickerUtils.PATTERN_DAY);
            if (startDate.getTime() > endDate.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return true;
            }
            if (endDate.getTime() > startRangeTime.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return true;
            }
            instance.setTime(endDate);
            instance.add(Calendar.DATE, -30);
            Date endRangeTime = instance.getTime();
            if (startDate.getTime() < endRangeTime.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return true;
            }

        } else if (mData.dateType == 1) {
            if (TextUtils.isEmpty(mData.startDate) || TextUtils.isEmpty(mData.endDate)) {
                ToastUtils.showToast("请选择正确的时间范围(按月最长选择12个月)");
                return true;
            }
            Date startDate = FormatUtils.decodeDate(mData.startDate, PickerUtils.PATTERN_MONTH);
            Calendar instance = Calendar.getInstance();
            instance.setTime(startDate);
            instance.add(Calendar.MONTH, 12);
            Date startRangeTime = instance.getTime();
            Date endDate = FormatUtils.decodeDate(mData.endDate, PickerUtils.PATTERN_MONTH);
            if (startDate.getTime() > endDate.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return true;
            }
            if (endDate.getTime() > startRangeTime.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按月最长选择12个月)");
                return true;
            }
            instance.setTime(endDate);
            instance.add(Calendar.MONTH, -12);
            Date endRangeTime = instance.getTime();
            if (startDate.getTime() < endRangeTime.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按月最长选择12个月)");
                return true;
            }
        } else if (mData.dateType == 2) {
            if (TextUtils.isEmpty(mData.startDate) || TextUtils.isEmpty(mData.endDate)) {
                ToastUtils.showToast("请选择年份");
                return true;
            }
            Date startDate = FormatUtils.decodeDate(mData.startDate, PickerUtils.PATTERN_YEAR);
            Date endDate = FormatUtils.decodeDate(mData.endDate, PickerUtils.PATTERN_YEAR);
            if (startDate.getTime() > endDate.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围");
                return true;
            }
        }
        return false;
    }
}
