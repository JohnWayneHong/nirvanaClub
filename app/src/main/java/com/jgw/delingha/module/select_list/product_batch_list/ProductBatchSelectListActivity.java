package com.jgw.delingha.module.select_list.product_batch_list;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;
import com.jgw.delingha.module.select_list.product_list.ProductSelectListActivity;

/**
 * 选择产品批次列表
 */
public class ProductBatchSelectListActivity extends BaseSelectItemListActivity<ProductBatchSelectListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "product_batch_id";

    @Override
    protected void initData() {
        setTitle(ResourcesUtils.getString(R.string.product_batch_select_title));
        String productId = getIntent().getStringExtra(ProductSelectListActivity.EXTRA_NAME);
        mViewModel.setProductId(productId);
        super.initData();
    }

    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(@NonNull String productId, int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ProductBatchSelectListActivity.class);
            intent.putExtra(ProductSelectListActivity.EXTRA_NAME, productId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}

