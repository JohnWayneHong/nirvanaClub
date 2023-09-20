package com.jgw.delingha.custom_module.delingha.select_list.weight_type_list;

import android.app.Activity;
import android.content.Intent;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class WeightTypeListActivity extends BaseSelectItemListActivity<WeightTypeListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "WeightTypeListActivity";
    public static final String TAG_DATA = "WeightTypeListActivityData";

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
        setTitle("称重类型");
        super.initData();
    }

    @Override
    public String getExtraName() {
        return TAG;
    }

    public static void start(Activity context,int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, WeightTypeListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
