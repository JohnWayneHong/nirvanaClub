package com.jgw.delingha.module.select_list.scan_rule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.delingha.bean.ScanRuleBean;
import com.jgw.delingha.databinding.ActivityScanRuleSelectListBinding;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

public class ScanRuleSelectListActivity extends BaseActivity<BaseViewModel, ActivityScanRuleSelectListBinding> {

    public static final int LESS_OR_EQUAL = -1;
    public static final int EQUAL = 0;
    public static final int GREATER_OR_EQUAL = 1;
    private ScanRuleSelectListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle("设置");
        setRight("保存");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        mAdapter = new ScanRuleSelectListRecyclerAdapter();
        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        ArrayList<ScanRuleBean> list = new ArrayList<>();
        ScanRuleBean e = new ScanRuleBean();
        e.setType(LESS_OR_EQUAL);
        list.add(e);

        e = new ScanRuleBean();
        e.setType(EQUAL);
        list.add(e);

        e = new ScanRuleBean();
        e.setType(GREATER_OR_EQUAL);
        list.add(e);

        mAdapter.setDataList(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        List<ScanRuleBean> list = mAdapter.getDataList();
        for (int i = 0; i < list.size(); i++) {
            ScanRuleBean b = list.get(i);
            if (b.isSelected()) {
                b.setSelected(false);
                mAdapter.notifyItemChanged(i);
            }
            if (i == position) {
                b.setSelected(true);
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    protected void clickRight() {
        for (ScanRuleBean b : mAdapter.getDataList()) {
            if (b.isSelected()) {
                MMKVUtils.save(ConstantUtil.SCAN_RULE, b.getType());
                break;
            }
        }
        setResult(RESULT_OK);
        finish();
    }

    public static void start(int requestCode, Activity context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, ScanRuleSelectListActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}

