package com.jgw.delingha.module.select_list.product_list;

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
public class ProductSelectListActivity extends BaseSelectItemListActivity<ProductSelectListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_NAME = "product_id";

    @Override
    protected void initData() {
        setTitle(ResourcesUtils.getString(R.string.product_select_title));
        super.initData();
    }

    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ProductSelectListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}

