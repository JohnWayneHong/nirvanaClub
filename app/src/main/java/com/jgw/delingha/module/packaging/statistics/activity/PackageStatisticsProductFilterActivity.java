package com.jgw.delingha.module.packaging.statistics.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.databinding.ActivityPackageStatisticsProductFilterBinding;
import com.jgw.delingha.module.packaging.statistics.viewmodel.PackageStatisticsProductFilterViewModel;
import com.jgw.delingha.utils.PickerUtils;

import java.util.Calendar;
import java.util.Date;

public class PackageStatisticsProductFilterActivity extends BaseActivity<PackageStatisticsProductFilterViewModel, ActivityPackageStatisticsProductFilterBinding> {

    @Override
    protected void initView() {
        setTitle("包装关联统计");
    }

    @Override
    protected void initData() {
        StatisticsFilterBean data = (StatisticsFilterBean) getIntent().getSerializableExtra("data");
        mViewModel.setData(data);
        mBindingView.setData(data);
        switchDateType(data.dateType);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvPackageStatisticsProductFilterDay)
                .addView(mBindingView.tvPackageStatisticsProductFilterMonth)
                .addView(mBindingView.tvPackageStatisticsProductFilterYear)
                .addView(mBindingView.tvPackageStatisticsProductFilterStartDate)
                .addView(mBindingView.tvPackageStatisticsProductFilterEndDate)
                .addView(mBindingView.tvPackageStatisticsProductFilterSelectMonth)
                .addView(mBindingView.tvPackageStatisticsProductFilterSelectYear)
                .addView(mBindingView.tvPackageStatisticsProductFilterConfirm)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_package_statistics_product_filter_day) {
            switchDateType(0);
        } else if (id == R.id.tv_package_statistics_product_filter_month) {
            switchDateType(1);
        } else if (id == R.id.tv_package_statistics_product_filter_year) {
            switchDateType(2);
        } else if (id == R.id.tv_package_statistics_product_filter_start_date) {
            showStartDatePiker();
        } else if (id == R.id.tv_package_statistics_product_filter_end_date) {
            showEndDatePiker();
        } else if (id == R.id.tv_package_statistics_product_filter_select_month) {
            showMonthPiker();
        } else if (id == R.id.tv_package_statistics_product_filter_select_year) {
            showYearPiker();
        } else if (id == R.id.tv_package_statistics_product_filter_confirm) {
            confirm();
        }
    }

    private void switchDateType(int type) {
        mBindingView.tvPackageStatisticsProductFilterDay.setSelected(type==0);
        mBindingView.tvPackageStatisticsProductFilterMonth.setSelected(type==1);
        mBindingView.tvPackageStatisticsProductFilterYear.setSelected(type==2);
        mViewModel.getData().dateType = type;
        mBindingView.invalidateAll();
    }


    private void showStartDatePiker() {
        StatisticsFilterBean data = mViewModel.getData();
        Calendar selectDate = null;
        if (!TextUtils.isEmpty(data.startDate)) {
            Date date = FormatUtils.decodeDate(data.startDate, PickerUtils.PATTERN_DAY);
            selectDate = Calendar.getInstance();
            selectDate.setTime(date);
        }
        PickerUtils.showTimePicker(null, null, selectDate, this, time -> {
            mViewModel.getData().startDate = time;
            mBindingView.invalidateAll();
        });
    }

    private void showEndDatePiker() {
        StatisticsFilterBean data = mViewModel.getData();
        Calendar selectDate = null;
        if (!TextUtils.isEmpty(data.endDate)) {
            Date date = FormatUtils.decodeDate(data.endDate, PickerUtils.PATTERN_DAY);
            selectDate = Calendar.getInstance();
            selectDate.setTime(date);
        }
        PickerUtils.showTimePicker(null, null, selectDate, this, time -> {
            mViewModel.getData().endDate = time;
            mBindingView.invalidateAll();
        });
    }

    private void showMonthPiker() {
        StatisticsFilterBean data = mViewModel.getData();
        Calendar selectDate = null;
        if (!TextUtils.isEmpty(data.selectMonth)) {
            Date date = FormatUtils.decodeDate(data.selectMonth, PickerUtils.PATTERN_MONTH);
            selectDate = Calendar.getInstance();
            selectDate.setTime(date);
        }
        PickerUtils.showTimePicker(null, null, selectDate, PickerUtils.PATTERN_MONTH, this, time -> {
            mViewModel.getData().selectMonth = time;
            mBindingView.invalidateAll();
        }, null);
    }

    private void showYearPiker() {
        StatisticsFilterBean data = mViewModel.getData();
        Calendar selectDate = null;
        if (!TextUtils.isEmpty(data.selectYear)) {
            Date date = FormatUtils.decodeDate(data.selectYear, PickerUtils.PATTERN_YEAR);
            selectDate = Calendar.getInstance();
            selectDate.setTime(date);
        }
        PickerUtils.showTimePicker(null, null, selectDate, PickerUtils.PATTERN_YEAR, this, time -> {
            mViewModel.getData().selectYear = time;
            mBindingView.invalidateAll();
        }, null);
    }

    private void confirm() {
        StatisticsFilterBean filterBean = mViewModel.getData();
        if (filterBean.dateType == 0) {
            if (TextUtils.isEmpty(filterBean.startDate) || TextUtils.isEmpty(filterBean.endDate)) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return;
            }
            Date startDate = FormatUtils.decodeDate(filterBean.startDate, PickerUtils.PATTERN_DAY);
            Calendar instance = Calendar.getInstance();
            instance.setTime(startDate);
            instance.add(Calendar.DATE, 30);
            Date startRangeTime = instance.getTime();
            Date endDate = FormatUtils.decodeDate(filterBean.endDate, PickerUtils.PATTERN_DAY);
            if (endDate.getTime() > startRangeTime.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return;
            }
            instance.setTime(endDate);
            instance.add(Calendar.DATE, -30);
            Date endRangeTime = instance.getTime();
            if (startDate.getTime() < endRangeTime.getTime()) {
                ToastUtils.showToast("请选择正确的时间范围(按天最长选择30天)");
                return;
            }

        }else if (filterBean.dateType == 1){
            if (TextUtils.isEmpty(filterBean.selectMonth)){
                ToastUtils.showToast("请选择月份");
                return;
            }
        }else if (filterBean.dateType == 2){
            if (TextUtils.isEmpty(filterBean.selectYear)){
                ToastUtils.showToast("请选择年份");
                return;
            }
        }
        Intent intent = new Intent();
        intent.putExtra("data", filterBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void start(Activity context, int requestCode, StatisticsFilterBean bean) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStatisticsProductFilterActivity.class);
            intent.putExtra("data", bean);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
