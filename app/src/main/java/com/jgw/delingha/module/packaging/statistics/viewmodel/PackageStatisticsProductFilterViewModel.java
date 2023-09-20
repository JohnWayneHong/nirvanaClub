package com.jgw.delingha.module.packaging.statistics.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.delingha.bean.StatisticsFilterBean;

import org.jetbrains.annotations.NotNull;

public class PackageStatisticsProductFilterViewModel extends BaseViewModel {

    private StatisticsFilterBean mData;

    public PackageStatisticsProductFilterViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    public StatisticsFilterBean getData() {
        return mData;
    }

    public void setData(StatisticsFilterBean data) {
        mData =data;
    }
}
