package com.jgw.delingha.module.packaging.statistics.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.databinding.ActivityPackageStatisticsBinding;
import com.jgw.delingha.module.packaging.statistics.adapter.PackageStatisticsProductAdapter;
import com.jgw.delingha.module.packaging.statistics.view.barchart.value_format.MyAxisValueFormatter;
import com.jgw.delingha.module.packaging.statistics.viewmodel.PackageStatisticsViewModel;

import java.util.List;

public class PackageStatisticsActivity extends BaseActivity<PackageStatisticsViewModel, ActivityPackageStatisticsBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final int[] NORMAL_COLORS = {
            Color.parseColor("#52B27C"),
            Color.parseColor("#97CC71"),
            Color.parseColor("#5971C9"),
            Color.parseColor("#7EC0DF"),
            Color.parseColor("#F4834C"),

            Color.parseColor("#F5C74E"),
            Color.parseColor("#E56564"),
            Color.parseColor("#E37CCE"),
            Color.parseColor("#9760B6"),
            Color.parseColor("#5971C9")
    };
    private PackageStatisticsProductAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle("包装统计");
        mBindingView.rvcPackageStatisticsPackageToday.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new PackageStatisticsProductAdapter();
        mBindingView.rvPackageStatisticsPackageToday.setAdapter(mAdapter);

        onRefresh();

        initDateChart();
    }

    @Override
    public void initLiveData() {
        mViewModel.getPackageStatisticsTwoDayLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mBindingView.setTodayData(resource.getData().today);
                    mBindingView.setYesterdayData(resource.getData().yesterday);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getProductStatisticsLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mAdapter.notifyRefreshList(resource.getData());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
        mViewModel.getTimeStatisticsLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.SUCCESS:
                    mViewModel.setTimeList(resource.getData());
                    notifyTodayStatistics(mViewModel.getTimeList());
                    break;
                case Resource.ERROR:
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });

    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvPackageStatisticsTodayView)
                .addView(mBindingView.tvPackageStatisticsMonthView)
                .addOnClickListener()
                .submit();
        mBindingView.srlPackageStatistics.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_package_statistics_today_view) {
            PackageStatisticsByProductActivity.start(this);
        } else if (id == R.id.tv_package_statistics_month_view) {
            PackageStatisticsByDateActivity.start(this);
        }
    }

    private void initDateChart() {
        BarChart chart = mBindingView.hbcPackageStatisticsMonth;
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        ValueFormatter custom = new MyAxisValueFormatter(mViewModel.getTimeList(), mViewModel.getFilterData());

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setGranularity(1f); // only intervals of 1 day
        xl.setLabelCount(4);
        xl.setValueFormatter(custom);

        YAxis al = chart.getAxisLeft();
        al.setAxisMinimum(0);
        al.setGranularity(1f);
        YAxis ar = chart.getAxisRight();
        ar.setAxisMinimum(0);
        ar.setGranularity(1f);
        mBindingView.hbcPackageStatisticsMonth.setData(mViewModel.getTodayStatisticsData());

    }

    protected static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void notifyTodayStatistics(List<PackageStatisticsBean> list) {
        BarData barData = mViewModel.getTodayStatisticsData();
        BarDataSet barDataSet = (BarDataSet) barData.getDataSetByIndex(0);
        List<BarEntry> values = mViewModel.formatList(list);
        barDataSet.setValues(values);
        barData.notifyDataChanged();

        mBindingView.hbcPackageStatisticsMonth.notifyDataSetChanged();
        mBindingView.hbcPackageStatisticsMonth.invalidate();
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStatisticsActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.getPackageStatisticsTwoDay();
        mViewModel.getTodayProductStatistics();
        mViewModel.getMonthTimeStatistics();
        mBindingView.srlPackageStatistics.setRefreshing(false);
    }
}
