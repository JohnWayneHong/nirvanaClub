package com.jgw.delingha.custom_module.delingha.select_list.pig_variety;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

import org.jetbrains.annotations.NotNull;

public class PigVarietyListActivity extends BaseSelectItemListActivity<PigVarietyListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "PigVarietyListActivity";
    public static final String TAG_DATA = "PigVarietyListActivityData";

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
        setTitle("品种");
        String productSortId = getIntent().getStringExtra("productSortId");
        mViewModel.setProductSortId(productSortId);
        super.initData();
    }

    @Override
    public String getExtraName() {
        return TAG;
    }

    public static void start(int requestCode, Activity context,@NonNull String productSortId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PigVarietyListActivity.class);
            intent.putExtra("productSortId",productSortId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
