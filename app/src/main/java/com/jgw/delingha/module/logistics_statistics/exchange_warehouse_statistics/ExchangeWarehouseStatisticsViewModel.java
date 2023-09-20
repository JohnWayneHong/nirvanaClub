package com.jgw.delingha.module.logistics_statistics.exchange_warehouse_statistics;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

import java.util.Calendar;
import java.util.Date;


public class ExchangeWarehouseStatisticsViewModel extends BaseViewModel {

    private final ConfigInfoModel mConfigModel;
    private final MutableLiveData<Long> mProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mWareHouseInfoLiveData = new ValueKeeperLiveData<>();

    public ExchangeWarehouseStatisticsViewModel(@NonNull Application application) {
        super(application);
        mConfigModel = new ConfigInfoModel();

    }

    public void getProductInfo(long id) {
        mProductInfoLiveData.setValue(id);
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mProductInfoLiveData, mConfigModel::getProductInfoEntity);
    }

    public void getWareHouseInfo(long id) {
        mWareHouseInfoLiveData.setValue(id);
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoLiveData() {
        return Transformations.switchMap(mWareHouseInfoLiveData, mConfigModel::getWareHouseInfoEntity);
    }

    public boolean checkParams(StatisticsParamsBean data) {
        String dimensionInfo = data.getDimensionInfo();
        if (TextUtils.isEmpty(dimensionInfo)) {
            ToastUtils.showToast("请选择统计维度");
            return true;
        }
        String wareHouseNameOut = data.getWareHouseNameOut();
        String wareHouseNameIn = data.getWareHouseNameIn();
        if (!TextUtils.isEmpty(wareHouseNameOut)&&!TextUtils.isEmpty(wareHouseNameIn)){
            if (TextUtils.equals(wareHouseNameOut,wareHouseNameIn)){
                ToastUtils.showToast(String.format("%1s和%2s不能相同"
                        ,ResourcesUtils.getString(R.string.warehouse_out_name)
                        ,ResourcesUtils.getString(R.string.warehouse_in_name)));
                return true;
            }
        }
        String startTime = data.getStartTime();
        if (TextUtils.isEmpty(startTime)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.start_time_required_hint));
            return true;
        }
        String endTime = data.getEndTime();
        if (TextUtils.isEmpty(endTime)) {
            ToastUtils.showToast(ResourcesUtils.getString(R.string.end_time_required_hint));
            return true;
        }
        Calendar startCalendar = Calendar.getInstance();
        Date startDate = FormatUtils.decodeDate(startTime,"yyyy-MM-dd");
        startCalendar.setTime(startDate);
        int startYear = startCalendar.get(Calendar.YEAR);
        Calendar endCalendar = Calendar.getInstance();
        Date endDate = FormatUtils.decodeDate(endTime,"yyyy-MM-dd");
        endCalendar.setTime(endDate);
        int endYear = endCalendar.get(Calendar.YEAR);
        if (startDate.getTime()>endDate.getTime()){
            ToastUtils.showToast("起始时间不能晚于结束时间!");
            return true;
        }
        if (startYear!=endYear){
            ToastUtils.showToast("查询区间需为同一年!");
            return true;
        }
        return false;
    }
}
