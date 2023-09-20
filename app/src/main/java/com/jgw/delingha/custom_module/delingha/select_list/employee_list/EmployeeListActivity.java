package com.jgw.delingha.custom_module.delingha.select_list.employee_list;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class EmployeeListActivity extends BaseSelectItemListActivity<EmployeeListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "EmployeeListActivity";
    public static final String TAG_DATA = "EmployeeListActivityData";

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
        setTitle("员工");
        super.initData();
    }

    @Override
    public String getExtraName() {
        return TAG;
    }

    public static void start(Activity context,int requestCode) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, EmployeeListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
