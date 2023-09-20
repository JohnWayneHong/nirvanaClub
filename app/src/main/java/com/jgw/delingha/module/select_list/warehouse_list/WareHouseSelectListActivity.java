package com.jgw.delingha.module.select_list.warehouse_list;

import android.app.Activity;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class WareHouseSelectListActivity extends BaseSelectItemListActivity<WareHouseSelectListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "warehouse_id";

    @Override
    protected void initData() {
        super.initData();
        setTitle(ResourcesUtils.getString(R.string.ware_house_select_title));
    }

    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, WareHouseSelectListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}

