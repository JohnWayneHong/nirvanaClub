package com.jgw.delingha.custom_module.delingha.select_list.breed_task;

import android.app.Activity;
import android.content.Intent;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class BreedTaskTypeListActivity extends BaseSelectItemListActivity<BreedTaskTypeListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "BreedTaskListActivity";
    public static final String TAG_DATA = "BreedTaskListActivityData";

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
        setTitle("状态");
        super.initData();
    }

    @Override
    public String getExtraName() {
        return TAG;
    }

    public static void start(Activity context,int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, BreedTaskTypeListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
