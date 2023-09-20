package com.jgw.delingha.module.logistics_statistics.stock_in_statistics;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.databinding.ActivityStockInStatisticsBinding;
import com.jgw.delingha.module.logistics_statistics.statistical_dimension.StatisticalDimensionListActivity;
import com.jgw.delingha.module.logistics_statistics.statistics_list.StatisticsListActivity;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.utils.PickerUtils;

import java.util.List;


/**
 * Created by xsw
 * on 2021/11/12
 * 入库统计设置
 */
public class StockInStatisticsActivity extends BaseActivity<StockInStatisticsViewModel, ActivityStockInStatisticsBinding> {

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("入库统计");
        refreshData();
        int type = MMKVUtils.getInt(ConstantUtil.CURRENT_CUSTOMER_TYPE);
        boolean isAdmin = type == 1;
        mBindingView.tvStockInStatisticsWarehouse.setEnabled(isAdmin);
        if (!isAdmin) {
            mViewModel.getCurrentWarehouse();
        }
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getProductInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            ProductInfoEntity entity = resource.getData();
            data.setProductInfo(entity);
        });
        mViewModel.getProductBatchInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            ProductBatchEntity entity = resource.getData();
            data.setBatchInfo(entity);
        });
        mViewModel.getWareHouseInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            WareHouseEntity entity = resource.getData();
            data.setWarehouseInfo(entity);
        });
        mViewModel.getCurrentWarehouseLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            WareHouseEntity entity = resource.getData();
            data.setWarehouseInfo(entity);
        });
    }

    private void refreshData() {
        StatisticsParamsBean oldData = mBindingView.getData();
        StatisticsParamsBean data = new StatisticsParamsBean();
        int type = MMKVUtils.getInt(ConstantUtil.CURRENT_CUSTOMER_TYPE);
        boolean isAdmin = type == 1;
        if (oldData != null && !isAdmin) {
            data.setWareHouseName(oldData.getWareHouseName());
            data.setWareHouseCode(oldData.getWareHouseCode());
            data.setWareHouseId(oldData.getWareHouseId());
        }
        mBindingView.setData(data);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvStockInStatisticsDimension)
                .addView(mBindingView.tvStockInStatisticsProduct)
                .addView(mBindingView.tvStockInStatisticsBatch)
                .addView(mBindingView.tvStockInStatisticsWarehouse)
                .addView(mBindingView.tvStockInStatisticsStartTime)
                .addView(mBindingView.tvStockInStatisticsEndTiem)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsReset)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsNext)
                .submit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == mBindingView.tvStockInStatisticsDimension.getId()) {
            StatisticalDimensionListActivity.start(1, this, StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_IN);
        } else if (id == mBindingView.tvStockInStatisticsProduct.getId()) {
            ProductSelectListActivity.start(2, this);
        } else if (id == mBindingView.tvStockInStatisticsBatch.getId()) {
            StatisticsParamsBean data = mBindingView.getData();
            String productId = data.getProductId();
            if (TextUtils.isEmpty(productId)) {
                ToastUtils.showToast("请先选择产品");
                return;
            }
            ProductBatchSelectListActivity.start(productId, 3, this);
        } else if (id == mBindingView.tvStockInStatisticsWarehouse.getId()) {
            WareHouseSelectListActivity.start(4, this);
        } else if (id == mBindingView.tvStockInStatisticsStartTime.getId()) {
            PickerUtils.showTimePicker(this, time -> mBindingView.getData().setStartTime(time));
        } else if (id == mBindingView.tvStockInStatisticsEndTiem.getId()) {
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
        StatisticsListActivity.start(this, data, StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_IN);
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
            case 3:
                //选择产品批次
                id = data.getLongExtra(ProductBatchSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getProductBatchInfo(id);
                break;
            case 4:
                //选择仓库
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouseInfo(warehouseId);
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, StockInStatisticsActivity.class);
        context.startActivity(intent);
    }
}
