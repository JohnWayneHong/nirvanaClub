package com.jgw.delingha.module.packaging.statistics.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.databinding.ActivityPackageStatisticsDateDetailsBinding;
import com.jgw.delingha.module.packaging.statistics.view.barchart.value_format.MyAxisValueFormatter;
import com.jgw.delingha.module.packaging.statistics.viewmodel.PackageStatisticsDateDetailsViewModel;

import java.util.List;

/**
 * 时间维度统计图
 * 是按天(30天范围内) 按月(12个月范围内) 按年(历史-当年范围) 的以单位时间内所有产品数量柱状数据的统计图
 * 30天就是30条天  12个月就是12条月份数据
 */
public class PackageStatisticsByDateActivity extends BaseActivity<PackageStatisticsDateDetailsViewModel, ActivityPackageStatisticsDateDetailsBinding> {

    private MyAxisValueFormatter valueFormatter;

    @Override
    protected void initView() {
        setTitle("数据统计分析");
    }

    @Override
    protected void initData() {
        StatisticsFilterBean filterBean = mViewModel.getFilterData();
        mBindingView.setFilterData(filterBean);
        mViewModel.getDateStatistics();
        initDateChart();
    }

    @Override
    public void initLiveData() {
        mViewModel.getDateStatisticsLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    List<PackageStatisticsBean> list = resource.getData();
                    if (!list.isEmpty()) {
                        PackageStatisticsBean bean = new PackageStatisticsBean();
                        for (PackageStatisticsBean b : list) {
                            bean.firstCount += b.firstCount;
                            bean.secondCount += b.secondCount;
                            bean.thirdCount += b.thirdCount;
                        }
                        mBindingView.setData(bean);
                    }
                    mViewModel.setList(resource.getData());
                    notifyTodayStatistics(list);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.rlPackageStatisticsDetailsDateFilter)
                .addOnClickListener()
                .submit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        StatisticsFilterBean filterBean = (StatisticsFilterBean) data.getSerializableExtra("data");
        mViewModel.setFilterData(filterBean);
        mBindingView.setFilterData(filterBean);
        mViewModel.getDateStatistics();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.rl_package_statistics_details_date_filter) {
            PackageStatisticsDateFilterActivity.start(this, 1, mViewModel.getFilterData());
        }
    }

    private void initDateChart() {
        BarChart chart = mBindingView.hbcPackageStatisticsDate;
//        chart.setOnTouchListener(new View.OnTouchListener() {
//            float startX = 0;
//            float startY = 0;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = event.getX();
//                        startY = event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (Math.abs(distance(event.getX(), startX, event.getY(),
//                                startY)) > Utils.convertDpToPixel(3f)) {
//                            mBindingView.hbcPackageStatisticsDate.disableScroll();
//                        } else {
//                            mBindingView.hbcPackageStatisticsDate.enableScroll();
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
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

        valueFormatter = new MyAxisValueFormatter(mViewModel.getList(),
                mViewModel.getFilterData());

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setGranularity(1f); // only intervals of 1 day
        xl.setLabelCount(4);
        xl.setValueFormatter(valueFormatter);

        YAxis al = chart.getAxisLeft();
        al.setAxisMinimum(0);
        al.setGranularity(1f);
        YAxis ar = chart.getAxisRight();
        ar.setAxisMinimum(0);
        ar.setGranularity(1f);
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
//
//        ValueFormatter custom = new MyAxisValueFormatter(mViewModel.getList(),
//                mViewModel.getFilterData());
//        mBindingView.hbcPackageStatisticsDate.clear().getXAxis().setValueFormatter(custom);
        valueFormatter.setFilterData(mViewModel.getFilterData());
        mBindingView.hbcPackageStatisticsDate.setData(barData);
        mBindingView.hbcPackageStatisticsDate.notifyDataSetChanged();
        mBindingView.hbcPackageStatisticsDate.invalidate();
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStatisticsByDateActivity.class);
            context.startActivity(intent);
        }
    }
}
