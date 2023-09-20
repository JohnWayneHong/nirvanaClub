package com.jgw.delingha.module.query.related.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.databinding.ActivityRelatedQueryListBinding;
import com.jgw.delingha.module.query.related.adapter.RelatedQueryCodeListRecyclerAdapter;

public class RelatedQueryListActivity extends BaseActivity<BaseViewModel, ActivityRelatedQueryListBinding> {

    private CodeRelationInfoResultBean mInfoBean;
    private boolean isShowSingle;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.related_query_title));
        mBindingView.rvcLowerCode.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String mInfoJson = intent.getStringExtra("json");
        isShowSingle = intent.getBooleanExtra("isShowSingle", false);
        saveEntity(mInfoJson);
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.rlRelatedQueryShowDetails)
                .addOnClickListener()
                .submit();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.rl_related_query_show_details) {
            mBindingView.getData().switchShowDetailsStatus();
        }
    }

    public void saveEntity(String mInfoJson) {
        updateAmountView(0);
        mInfoBean = JsonUtils.parseObject(mInfoJson, CodeRelationInfoResultBean.class);
        if (mInfoBean == null) {
            return;
        }
        mBindingView.setData(mInfoBean);
        updateAmountView(mInfoBean.getSonNumber());
        if (mInfoBean.getSonNumber() <= 0 && mInfoBean.parentCode == null) {
            showEmptyView();
            return;
        }
        if (mInfoBean.getSonNumber() > 0) {
            showLowerCodeInfoView();
        }
        if (!TextUtils.isEmpty(mInfoBean.parentCode)) {
            showUpperCodeInfoView();
        }
    }

    private void showEmptyView() {
        mBindingView.tvEmpty.setVisibility(View.VISIBLE);
        updateAmountView(0);
    }

    private void showUpperCodeInfoView() {
        mBindingView.constraintLayoutUpperCode.setVisibility(View.VISIBLE);
    }

    private void showLowerCodeInfoView() {
        RelatedQueryCodeListRecyclerAdapter mAdapter = new RelatedQueryCodeListRecyclerAdapter();
        mBindingView.rvLowerCode.setAdapter(mAdapter);
        mAdapter.notifyRefreshList(mInfoBean.sonCodeVoList);
        mBindingView.constraintLayoutLowerCode.setVisibility(View.VISIBLE);
    }

    private void updateAmountView(int amount) {
        if (isShowSingle) {
            int singleCodeNumber = 0;
            if (mInfoBean != null) {
                singleCodeNumber = mInfoBean.sonLeafCount;
            }
            String text = "下级码：" + amount + "条，最小单位：" + singleCodeNumber + "条";
            mBindingView.tvCodeAmount.setText(text);
        } else {
            Spanned text = Html.fromHtml("共<font color='#03A9F4'>" + amount + "</font>条");
            mBindingView.tvCodeAmount.setText(text);
        }

    }

    public static void start(Context context, CodeRelationInfoResultBean bean, boolean isShowSingle) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, RelatedQueryListActivity.class);
            intent.putExtra("json", JsonUtils.toJsonString(bean));
            intent.putExtra("isShowSingle", isShowSingle);
            context.startActivity(intent);
        }
    }


}
