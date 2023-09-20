package com.jgw.delingha.module.select_list.logistics_company;

import android.app.Activity;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class LogisticsCompanyListActivity extends BaseSelectItemListActivity<LogisticsCompanyListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "logistics_company_id";

    @Override
    protected void initData() {
        super.initData();
        setTitle("快递公司");
    }

    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, LogisticsCompanyListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }

}
