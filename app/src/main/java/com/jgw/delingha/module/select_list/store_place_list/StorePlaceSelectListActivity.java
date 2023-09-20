package com.jgw.delingha.module.select_list.store_place_list;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;
import com.jgw.delingha.module.select_list.warehouse_list.WareHouseSelectListActivity;

public class StorePlaceSelectListActivity extends BaseSelectItemListActivity<StorePlaceSelectListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "store_place_id";

    @Override
    protected void initData() {
        setTitle(ResourcesUtils.getString(R.string.store_place_select_title));
        String warehouse_id = getIntent().getStringExtra(WareHouseSelectListActivity.EXTRA_NAME);
        mViewModel.setWareHouseId(warehouse_id);
        super.initData();
    }

    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(@NonNull String wareHouseId, int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, StorePlaceSelectListActivity.class);
            intent.putExtra(WareHouseSelectListActivity.EXTRA_NAME, wareHouseId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}

