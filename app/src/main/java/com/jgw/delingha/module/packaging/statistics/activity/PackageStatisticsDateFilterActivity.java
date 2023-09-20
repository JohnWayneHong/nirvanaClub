package com.jgw.delingha.module.packaging.statistics.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.databinding.ActivityPackageStatisticsDateFilterBinding;
import com.jgw.delingha.module.packaging.statistics.viewmodel.PackageStatisticsDateFilterViewModel;
import com.jgw.delingha.utils.PickerUtils;

import java.util.Calendar;
import java.util.Date;

public class PackageStatisticsDateFilterActivity extends BaseActivity<PackageStatisticsDateFilterViewModel, ActivityPackageStatisticsDateFilterBinding> {

    @Override
    protected void initView() {
        setTitle("数据统计分析");
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
                .addView(mBindingView.tvPackageStatisticsDateFilterDay)
                .addView(mBindingView.tvPackageStatisticsDateFilterMonth)
                .addView(mBindingView.tvPackageStatisticsDateFilterYear)
                .addView(mBindingView.tvPackageStatisticsDateFilterStartDate)
                .addView(mBindingView.tvPackageStatisticsDateFilterEndDate)
                .addView(mBindingView.tvPackageStatisticsDateFilterConfirm)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_package_statistics_date_filter_day) {
            switchDateType(0);
        } else if (id == R.id.tv_package_statistics_date_filter_month) {
            switchDateType(1);
        } else if (id == R.id.tv_package_statistics_date_filter_year) {
            switchDateType(2);
        } else if (id == R.id.tv_package_statistics_date_filter_start_date) {
            showStartDatePiker();
        } else if (id == R.id.tv_package_statistics_date_filter_end_date) {
            showEndDatePiker();
        } else if (id == R.id.tv_package_statistics_date_filter_confirm) {
            confirm();
        }
    }

    private void switchDateType(int type) {
        StatisticsFilterBean data = mViewModel.getData();

        mBindingView.tvPackageStatisticsDateFilterDay.setSelected(type == 0);
        mBindingView.tvPackageStatisticsDateFilterMonth.setSelected(type == 1);
        mBindingView.tvPackageStatisticsDateFilterYear.setSelected(type == 2);
        if (data.dateType == type) {
            return;
        }
        data.dateType = type;
        data.startDate = null;
        data.endDate = null;
        mBindingView.invalidateAll();
    }


    private void showStartDatePiker() {
        StatisticsFilterBean data = mViewModel.getData();
        Calendar selectDate = null;
        String strPattern = mViewModel.switchStrPattern();
        if (!TextUtils.isEmpty(data.startDate)) {
            Date date = FormatUtils.decodeDate(data.startDate, strPattern);
            selectDate = Calendar.getInstance();
            selectDate.setTime(date);
        }
        PickerUtils.showTimePicker(null, null, selectDate, strPattern, this, time -> {
            mViewModel.getData().startDate = time;
            mBindingView.invalidateAll();
        }, null);
    }

    private void showEndDatePiker() {
        StatisticsFilterBean data = mViewModel.getData();
        Calendar selectDate = null;
        String strPattern = mViewModel.switchStrPattern();
        if (!TextUtils.isEmpty(data.endDate)) {
            Date date = FormatUtils.decodeDate(data.endDate, strPattern);
            selectDate = Calendar.getInstance();
            selectDate.setTime(date);
        }
        PickerUtils.showTimePicker(null, null, selectDate, strPattern, this, time -> {
            mViewModel.getData().endDate = time;
            mBindingView.invalidateAll();
        }, null);
    }


    private void confirm() {
        StatisticsFilterBean filterBean = mViewModel.getData();
        if (mViewModel.checkFilterData()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("data", filterBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static void start(Activity context, int requestCode, StatisticsFilterBean bean) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStatisticsDateFilterActivity.class);
            intent.putExtra("data", bean);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
