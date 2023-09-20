package com.jgw.delingha.custom_module.delingha.select_list.pigsty_list;

import android.app.Activity;
import android.content.Intent;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class PigstyListActivity extends BaseSelectItemListActivity<PigstyListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "PigstyListActivity";
    public static final String TAG_DATA = "PigstyListActivityData";

    public static final String TYPE_IN_ALL="PigstyAllData";
    public static final String TYPE_IN_BATCH="PigstyInBatchData";
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
        setTitle("栏舍");
        mViewModel.selectInOrOut(getIntent().getStringExtra("type"));
    }

    @Override
    public String getExtraName() {
        return null;
    }

    public static void start( Activity context,int requestCode,String type) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, PigstyListActivity.class);
            intent.putExtra("type",type);
            context.startActivityForResult(intent, requestCode);
        }
    }

}
