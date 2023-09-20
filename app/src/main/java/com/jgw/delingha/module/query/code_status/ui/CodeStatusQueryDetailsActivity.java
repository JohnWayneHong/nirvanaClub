package com.jgw.delingha.module.query.code_status.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeStatusQueryDetailsDataBean;
import com.jgw.delingha.bean.CodeStatusQueryDetailsLabelBean;
import com.jgw.delingha.databinding.ActivityCodeStatusQueryDetailsBinding;
import com.jgw.delingha.module.query.code_status.adapter.CodeStatusQueryDetailsInfoAdapter;
import com.jgw.delingha.module.query.code_status.adapter.CodeStatusQueryDetailsLabelAdapter;
import com.jgw.delingha.module.query.code_status.viewmodel.CodeStatusQueryDetailsViewModel;

/**
 * @author : J-T
 * @date : 2022/7/21 11:18
 * description :扫码状态查询Activity
 */
public class CodeStatusQueryDetailsActivity extends BaseActivity<CodeStatusQueryDetailsViewModel,
        ActivityCodeStatusQueryDetailsBinding> {

    private CodeStatusQueryDetailsInfoAdapter mInfoAdapter;
    private CodeStatusQueryDetailsLabelAdapter mLabelAdapter;
    /**
     * 上次选择标签
     */
    private int lastSelectPosition = -1;

    public static final int TYPE_STOCK_IN = 0;
    public static final int TYPE_STOCK_OUT = 1;
    public static final int TYPE_EXCHANGE_WAREHOUSE = 2;
    public static final int TYPE_EXCHANGE_GOODS = 3;
    public static final int TYPE_STOCK_RETURN = 4;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.code_status_query_details));
        mBindingView.rvcCodeStatusQueryInfoList.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mLabelAdapter = new CodeStatusQueryDetailsLabelAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CodeStatusQueryDetailsActivity.this, 5);
        mBindingView.rvCodeStatusQueryLabelList.setAdapter(mLabelAdapter);
        mBindingView.rvCodeStatusQueryLabelList.setLayoutManager(gridLayoutManager);
        CodeStatusQueryDetailsDataBean dataBean = new CodeStatusQueryDetailsDataBean();
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        mViewModel.setCode(code);
        dataBean.code = code;
        mBindingView.setData(dataBean);
        mInfoAdapter = new CodeStatusQueryDetailsInfoAdapter();
        mBindingView.rvCodeStatusQueryInfoList.setAdapter(mInfoAdapter);
        mViewModel.initLabel();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mLabelAdapter.setOnItemClickListener(CodeStatusQueryDetailsActivity.this);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (lastSelectPosition == position) {
            //无事发生
            return;
        }
        changeLabel(position);
        mBindingView.getData().setSelect(position);
        mViewModel.getCodeStatusInfo(position);
    }

    @Override
    public void initLiveData() {
        mViewModel.getLabelListLiveData().observe(this, listResource -> {
            mLabelAdapter.notifyRefreshList(listResource.getData());
        });
        mViewModel.getCodeInfoLiveData().observe(this, resource -> {
            int loadingStatus = resource.getLoadingStatus();
            switch (loadingStatus) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mInfoAdapter.notifyRefreshList(resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    mInfoAdapter.notifyRemoveListItem();
                    break;
            }
        });
    }

    public void changeLabel(int position) {
        if (lastSelectPosition != -1) {
            //取消上次选中
            CodeStatusQueryDetailsLabelBean bean = mLabelAdapter.getDataList().get(lastSelectPosition);
            bean.selected = false;
            mLabelAdapter.notifyRefreshItem(bean);
        }
        CodeStatusQueryDetailsLabelBean bean = mLabelAdapter.getDataList().get(position);
        bean.selected = true;
        mLabelAdapter.notifyRefreshItem(bean);
        lastSelectPosition = position;
    }


    public static void start(Context context, String code) {
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast("码有误!");
            return;
        }
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CodeStatusQueryDetailsActivity.class);
            intent.putExtra("code", code);
            context.startActivity(intent);
        }
    }
}
