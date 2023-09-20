package com.jgw.delingha.module.select_list.product_packaging_list;

import android.app.Activity;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

/**
 * 选择产品列表
 */
public class ProductPackagingSelectListActivity extends BaseSelectItemListActivity<ProductPackagingSelectListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "package_product_id";

    @Override
    protected void initData() {
        setTitle(ResourcesUtils.getString(R.string.product_select_title));
        boolean haveStockIn = getIntent().getBooleanExtra("haveStockIn", false);
        mViewModel.setHaveStockIn(haveStockIn);
        super.initData();
    }


    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(int requestCode, Activity context) {
        start(requestCode, context, false);
    }

    public static void start(int requestCode, Activity context, boolean haveStockIn) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ProductPackagingSelectListActivity.class);
            intent.putExtra("haveStockIn", haveStockIn);
            context.startActivityForResult(intent, requestCode);
        }
    }

}

