package com.jgw.delingha.module.stock_out.order.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityOrderWaitUploadListBinding;
import com.jgw.delingha.module.stock_out.order.adapter.CommonStockOutWaitUploadListRecyclerAdapter;
import com.jgw.delingha.module.stock_out.order.viewmodel.CommonStockOutWaitUploadViewModel;
import com.jgw.delingha.module.wait_upload_task.base.BaseWaitUploadListActivity;
import com.jgw.delingha.sql.entity.BaseOrderEntity;

/**
 * 出库待执行列表
 */
public class CommonStockOutWaitUploadListActivity extends BaseWaitUploadListActivity<CommonStockOutWaitUploadViewModel, ActivityOrderWaitUploadListBinding> {

    private CommonStockOutWaitUploadListRecyclerAdapter mAdapter;

    @Override
    protected void initData() {
        setTitle("待执行单据出库");
        mAdapter = new CommonStockOutWaitUploadListRecyclerAdapter();
        super.initData();
    }

    @Override
    protected CustomRecyclerAdapter<BaseOrderEntity> getWaitUploadListAdapter() {
        return mAdapter;
    }

    @Override
    protected int getTaskType() {
        return mViewModel.getTaskType();
    }

    @Override
    public void onItemClick(View view, int position) {
        BaseOrderEntity bean = mAdapter.getDataList().get(position);
        int id = view.getId();
        if (id == R.id.ll_stock_out_wait_upload_list_item) {
            CommonOrderStockOutDetailsActivity.start(this,bean.getId());
        } else if (id == R.id.rl_stock_out_wait_upload_list_select) {
            int select = bean.getItemSelect() == 1 ? 0 : 1;
            bean.setItemSelect(select);
            mAdapter.notifyItemChanged(position);
        }
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CommonStockOutWaitUploadListActivity.class);
            context.startActivity(intent);
        }
    }
}
