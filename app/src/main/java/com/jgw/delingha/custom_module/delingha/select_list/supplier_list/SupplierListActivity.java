package com.jgw.delingha.custom_module.delingha.select_list.supplier_list;

import android.app.Activity;
import android.content.Intent;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class SupplierListActivity extends BaseSelectItemListActivity<SupplierListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "SupplierListActivity";
    public static final String TAG_DATA = "SupplierListActivityData";

    @Override
    public String getExtraDataName() {
        return TAG_DATA;
    }

    @Override
    public String getExtraIDName() {
        return TAG;
    }

    @Override
    protected void initData() {
        super.initData();
        String title = getIntent().getStringExtra("title");
        setTitle(title);
    }

    @Override
    public String getExtraName() {
        return null;
    }

    public static void start( Activity context,int requestCode,String titleName) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, SupplierListActivity.class);
            intent.putExtra("title",titleName);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
