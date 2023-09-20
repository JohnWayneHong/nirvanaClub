package com.jgw.delingha.custom_module.delingha.select_list.slaughter_in_batch;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class SlaughterInBatchListActivity extends BaseSelectItemListActivity<SlaughterInBatchListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "SlaughterInBatchListActivity";

    public static final String TAG_DATA = "SlaughterInBatchListActivityData";

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

    @Override
    protected void onSelectItem(View view, int position) {
        SelectItemSupport selectItemSupport = mAdapter.getDataList().get(position);
        String data = selectItemSupport.getExtraData();

        if (!TextUtils.isEmpty(data) && (Integer.parseInt(data) <= 0)) {
            ToastUtils.showToast("批次"+selectItemSupport.getShowName()+"无可用在场数量");
            return;
        }
        super.onSelectItem(view, position);
    }

    public static void start(Activity context, int requestCode, String titleName) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, SlaughterInBatchListActivity.class);
            intent.putExtra("title", titleName);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
