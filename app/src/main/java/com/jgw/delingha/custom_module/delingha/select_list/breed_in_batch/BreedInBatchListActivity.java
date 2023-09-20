package com.jgw.delingha.custom_module.delingha.select_list.breed_in_batch;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

public class BreedInBatchListActivity extends BaseSelectItemListActivity<BreedInBatchListViewModel, ActivityCommonSelectListBinding> {
    public static final String TAG = "BreedInBatchListActivity";

    public static final String TAG_DATA = "BreedInBatchListActivityData";

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
        String PigstyId = getIntent().getStringExtra("selectByPigstyId");
        mViewModel.setSelectByFence(PigstyId);
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
            ToastUtils.showToast("批次"+selectItemSupport.getShowName()+"无可用在栏数量");
            return;
        }
        super.onSelectItem(view, position);
    }

    public static void start(Activity context, int requestCode, String titleName,String PigstyId) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, BreedInBatchListActivity.class);
            intent.putExtra("title", titleName);
            intent.putExtra("selectByPigstyId",PigstyId);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
