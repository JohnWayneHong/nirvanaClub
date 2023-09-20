package com.jgw.delingha.module.logistics_statistics.stock_return_statistics;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.databinding.ActivityStockReturnStatisticsBinding;
import com.jgw.delingha.module.logistics_statistics.statistical_dimension.StatisticalDimensionListActivity;
import com.jgw.delingha.module.logistics_statistics.statistics_list.StatisticsListActivity;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.module.select_list.product_batch_list.ProductBatchSelectListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.utils.PickerUtils;

import java.util.List;


/**
 * Created by xsw
 * on 2021/11/12
 * 退货统计设置
 */
public class StockReturnStatisticsActivity extends BaseActivity<StockReturnStatisticsViewModel, ActivityStockReturnStatisticsBinding> {

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        setTitle("退货统计");
        refreshData();
        int type = MMKVUtils.getInt(ConstantUtil.CURRENT_CUSTOMER_TYPE);
        boolean isAdmin = type == 1;
        mBindingView.tvStockReturnStatisticsWarehouse.setEnabled(isAdmin);
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
        mViewModel.getCustomerInfoLiveData().observe(this, resource -> {
            StatisticsParamsBean data = mBindingView.getData();
            CustomerEntity entity = resource.getData();
            data.setCustomerInfo(entity);
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
                .addView(mBindingView.tvStockReturnStatisticsDimension)
                .addView(mBindingView.tvStockReturnStatisticsProduct)
                .addView(mBindingView.tvStockReturnStatisticsBatch)
                .addView(mBindingView.tvStockReturnStatisticsCustomer)
                .addView(mBindingView.tvStockReturnStatisticsWarehouse)
                .addView(mBindingView.tvStockReturnStatisticsStartTime)
                .addView(mBindingView.tvStockReturnStatisticsEndTiem)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsReset)
                .addView(mBindingView.layoutStatisticsFooter.tvStatisticsNext)
                .submit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == mBindingView.tvStockReturnStatisticsDimension.getId()) {
            StatisticalDimensionListActivity.start(1, this, StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_RETURN);
        } else if (id == mBindingView.tvStockReturnStatisticsProduct.getId()) {
            ProductSelectListActivity.start(2, this);
        } else if (id == mBindingView.tvStockReturnStatisticsBatch.getId()) {
            StatisticsParamsBean data = mBindingView.getData();
            String productId = data.getProductId();
            if (TextUtils.isEmpty(productId)) {
                ToastUtils.showToast("请先选择产品");
                return;
            }
            ProductBatchSelectListActivity.start(productId, 3, this);
        } else if (id == mBindingView.tvStockReturnStatisticsCustomer.getId()) {
            CustomerListActivity.start(4, this, CustomerListActivity.TYPE_ADMIN_ALL);
        } else if (id == mBindingView.tvStockReturnStatisticsWarehouse.getId()) {
            WareHouseSelectListActivity.start(5, this);
        } else if (id == mBindingView.tvStockReturnStatisticsStartTime.getId()) {
            PickerUtils.showTimePicker(this, time -> mBindingView.getData().setStartTime(time));
        } else if (id == mBindingView.tvStockReturnStatisticsEndTiem.getId()) {
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
        StatisticsListActivity.start(this, data, StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_RETURN);
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
                //选择客户
                long customerId = data.getLongExtra(CustomerListActivity.EXTRA_NAME, -1);
                mViewModel.getCustomerInfo(customerId);
                break;
            case 5:
                //选择仓库
                long warehouseId = data.getLongExtra(WareHouseSelectListActivity.EXTRA_NAME, -1);
                mViewModel.getWareHouseInfo(warehouseId);
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, StockReturnStatisticsActivity.class);
        context.startActivity(intent);
    }
}
