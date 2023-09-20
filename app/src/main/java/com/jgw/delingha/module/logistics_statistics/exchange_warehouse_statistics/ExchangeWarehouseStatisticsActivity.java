package com.jgw.delingha.module.logistics_statistics.exchange_warehouse_statistics;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.databinding.ActivityExchangeWarehouseStatisticsBinding;
import com.jgw.delingha.module.logistics_statistics.statistical_dimension.StatisticalDimensionListActivity;
import com.jgw.delingha.module.logistics_statistics.statistics_list.StatisticsListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.utils.PickerUtils;

import java.util.List;


/**
 * Created by xsw
 * on 2021/11/12
 * 调仓统计设置
 */
public class ExchangeWarehouseStatisticsActivity extends BaseActivity<ExchangeWarehouseStatisticsViewModel, ActivityExchangeWarehouseStatisticsBinding> {


    private int currentRequestCode;

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("调仓统计");

        refreshData();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getProductInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            ProductInfoEntity entity = resource.getData();
            data.setProductInfo(entity);
        });
        mViewModel.getWareHouseInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            WareHouseEntity entity = resource.getData();
            if (currentRequestCode == 3) {
                data.setWarehouseOutInfo(entity);
            } else if (currentRequestCode == 4) {
                data.setWarehouseInInfo(entity);
            }
        });
    }

    private void refreshData() {
        mBindingView.setData(new StatisticsParamsBean());
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvExchangeWarehouseStatisticsDimension)
                .addView(mBindingView.tvExchangeWarehouseStatisticsProduct)
                .addView(mBindingView.tvExchangeWarehouseStatisticsWarehouseOut)
                .addView(mBindingView.tvExchangeWarehouseStatisticsWarehouseIn)
                .addView(mBindingView.tvExchangeWarehouseStatisticsStartTime)
                .addView(mBindingView.tvExchangeWarehouseStatisticsEndTiem)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsReset)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsNext)
                .submit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == mBindingView.tvExchangeWarehouseStatisticsDimension.getId()) {
            StatisticalDimensionListActivity.start(1, this, StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE);
        } else if (id == mBindingView.tvExchangeWarehouseStatisticsProduct.getId()) {
            ProductSelectListActivity.start(2, this);
        } else if (id == mBindingView.tvExchangeWarehouseStatisticsWarehouseOut.getId()) {
            WareHouseSelectListActivity.start(3, this);
        } else if (id == mBindingView.tvExchangeWarehouseStatisticsWarehouseIn.getId()) {
            WareHouseSelectListActivity.start(4, this);
        } else if (id == mBindingView.tvExchangeWarehouseStatisticsStartTime.getId()) {
            PickerUtils.showTimePicker(this, time -> mBindingView.getData().setStartTime(time));
        } else if (id == mBindingView.tvExchangeWarehouseStatisticsEndTiem.getId()) {
            PickerUtils.showTimePicker(this, time -> mBindingView.getData().setEndTime(time));
        } else if (id == mBindingView.layoutStatisticsFooter.tvStatisticsReset.getId()) {
            refreshData();
        } else if (id == mBindingView.layoutStatisticsFooter.tvStatisticsNext.getId()) {
            next();
        }
    }

    private void next() {
        StatisticsParamsBean data = mBindingView.getData();
        if (mViewModel.checkParams(data)) {
            return;
        }
        StatisticsListActivity.start(this, data, StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                //选择收货人 
                List<StatisticsParamsBean.StatisticalDimension> list = MMKVUtils.getTempDataList(StatisticsParamsBean.StatisticalDimension.class);
                StatisticsParamsBean statisticsParamsBean = mBindingView.getData();
                statisticsParamsBean.setList(list);
                break;
            case 2:
                //选择产品
                long id = data.getLongExtra(ProductSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductInfo(id);
                break;
            case 3://选择仓库调出
            case 4: //选择仓库调入
                currentRequestCode = requestCode;
                long warehouseInId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouseInfo(warehouseInId);
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ExchangeWarehouseStatisticsActivity.class);
        context.startActivity(intent);
    }
}
