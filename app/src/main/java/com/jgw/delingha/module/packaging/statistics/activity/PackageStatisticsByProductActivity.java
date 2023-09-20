package com.jgw.delingha.module.packaging.statistics.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.bean.StatisticsFilterBean;
import com.jgw.delingha.databinding.ActivityPackageStatisticsProductDetailsBinding;
import com.jgw.delingha.module.packaging.statistics.adapter.PackageStatisticsProductAdapter;
import com.jgw.delingha.module.packaging.statistics.viewmodel.PackageStatisticsProductDetailsViewModel;

import java.util.List;

/**
 * 产品维度统计图
 * 按天(30天范围内) 按月(选择单月月份) 按年(选择单年年份)  一个产品的单位时间内数据是一条
 */
public class PackageStatisticsByProductActivity extends BaseActivity<PackageStatisticsProductDetailsViewModel, ActivityPackageStatisticsProductDetailsBinding> {

    private PackageStatisticsProductAdapter mAdapter;


    @Override
    protected void initView() {
        setTitle("包装关联统计");
        mBindingView.rvcPackageStatisticsPackageProduct.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        StatisticsFilterBean filterBean = mViewModel.getFilterData();
        mBindingView.setFilterData(filterBean);
        mAdapter = new PackageStatisticsProductAdapter();
        mViewModel.setShowList(mAdapter.getDataList());
        mBindingView.rvPackageStatisticsPackageProduct.setAdapter(mAdapter);
        mViewModel.getProductStatistics();
    }

    @Override
    public void initLiveData() {
        mViewModel.getProductStatisticsLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    List<PackageStatisticsBean> list = resource.getData();
                    mViewModel.setRealList(list);
                    mViewModel.notifyShowList(mAdapter);
                    PackageStatisticsBean bean;
                    if (!mAdapter.getDataList().isEmpty()) {
                        bean = mAdapter.getDataList().get(0);
                    }else{
                        bean=new PackageStatisticsBean();
                    }
                    mBindingView.setData(bean);
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
                .addView(mBindingView.rlPackageStatisticsDetailsProductFilter)
                .addOnClickListener()
                .submit();
        mAdapter.setOnItemClickListener(this);
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
        mViewModel.getProductStatistics();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.rl_package_statistics_details_product_filter) {
            PackageStatisticsProductFilterActivity.start(this, 1, mViewModel.getFilterData());
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        PackageStatisticsBean packageStatisticsBean = mAdapter.getDataList().get(position);
        mBindingView.setData(packageStatisticsBean);
    }

    public static void start(Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PackageStatisticsByProductActivity.class);
            context.startActivity(intent);
        }
    }
}
