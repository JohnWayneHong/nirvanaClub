package com.jgw.delingha.module.logistics_statistics.exchange_goods_statistics;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.databinding.ActivityExchangeGoodsStatisticsBinding;
import com.jgw.delingha.module.logistics_statistics.statistical_dimension.StatisticalDimensionListActivity;
import com.jgw.delingha.module.logistics_statistics.statistics_list.StatisticsListActivity;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.utils.PickerUtils;

import java.util.List;


/**
 * Created by xsw
 * on 2021/11/12
 * 调仓统计设置
 */
public class ExchangeGoodsStatisticsActivity extends BaseActivity<ExchangeGoodsStatisticsViewModel, ActivityExchangeGoodsStatisticsBinding> {


    private int currentRequestCode;


    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("调货统计");

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
        mViewModel.getCustomerInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            CustomerEntity entity = resource.getData();
            data.setCustomerInfo(entity);
            if (currentRequestCode == 3) {
                data.setCustomerInfoOut(entity);
            } else if (currentRequestCode == 4) {
                data.setCustomerInfoIn(entity);
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
                .addView(mBindingView.tvExchangeGoodsStatisticsDimension)
                .addView(mBindingView.tvExchangeGoodsStatisticsProduct)
                .addView(mBindingView.tvExchangeGoodsStatisticsCustomerOut)
                .addView(mBindingView.tvExchangeGoodsStatisticsCustomerIn)
                .addView(mBindingView.tvExchangeGoodsStatisticsStartTime)
                .addView(mBindingView.tvExchangeGoodsStatisticsEndTiem)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsReset)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsNext)
                .submit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == mBindingView.tvExchangeGoodsStatisticsDimension.getId()) {
            StatisticalDimensionListActivity.start(1, this, StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_GOODS);
        } else if (id == mBindingView.tvExchangeGoodsStatisticsProduct.getId()) {
            ProductSelectListActivity.start(2, this);
        } else if (id == mBindingView.tvExchangeGoodsStatisticsCustomerOut.getId()) {
            CustomerListActivity.start(3, this, CustomerListActivity.TYPE_ADMIN_ALL);
        } else if (id == mBindingView.tvExchangeGoodsStatisticsCustomerIn.getId()) {
            CustomerListActivity.start(4, this, CustomerListActivity.TYPE_ADMIN_ALL);
        } else if (id == mBindingView.tvExchangeGoodsStatisticsStartTime.getId()) {
            PickerUtils.showTimePicker(this, time -> mBindingView.getData().setStartTime(time));
        } else if (id == mBindingView.tvExchangeGoodsStatisticsEndTiem.getId()) {
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
        StatisticsListActivity.start(this, data, StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_GOODS);
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
                long warehouseInId = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCustomerInfo(warehouseInId);
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ExchangeGoodsStatisticsActivity.class);
        context.startActivity(intent);
    }
}
