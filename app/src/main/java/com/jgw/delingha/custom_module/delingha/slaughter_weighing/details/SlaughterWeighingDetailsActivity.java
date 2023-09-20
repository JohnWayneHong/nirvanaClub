package com.jgw.delingha.custom_module.delingha.slaughter_weighing.details;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.SlaughterWeighingDetailsListBean;
import com.jgw.delingha.custom_module.delingha.select_list.slaughter_weighing.SlaughterWeighingBatchListActivity;
import com.jgw.delingha.databinding.ActivityCommonInfoDetailsBinding;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author CJM
 * @Date 2023/6/14 09:32
 * @Description 屠宰管理 宰后称重 Activity
 */
public class SlaughterWeighingDetailsActivity extends BaseActivity<SlaughterWeighingDetailsViewModel, ActivityCommonInfoDetailsBinding> {

    public InfoDetailsRecyclerAdapter mAdapter;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle("详情");
        mAdapter = new InfoDetailsRecyclerAdapter();
        mBindingView.rvCommonInfoDetails.setAdapter(mAdapter);

        SlaughterWeighingDetailsListBean tempData = MMKVUtils.getTempData(SlaughterWeighingDetailsListBean.class);
        if (tempData == null) {
            mBindingView.tvCommonInfoDetailsSubmit.setVisibility(View.VISIBLE);
            mViewModel.loadList(null);
            return;
        }
        mBindingView.tvCommonInfoDetailsSubmit.setVisibility(View.GONE);

        List<String> weights = tempData.getWeights();
        if (weights == null) {
            ToastUtils.showToast("数据异常");
            return;
        }
        mViewModel.setCount(weights.size());
        mViewModel.loadList(tempData);
    }

    @Override
    public void initLiveData() {
        mViewModel.getLoadListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mAdapter.notifyAddListItem(resource.getData());
                    if (MMKVUtils.getTempData(SlaughterWeighingDetailsListBean.class) != null) {
                        return;
                    }
                    List<InfoDetailsDemoBean> item = mViewModel.getItem(true);
                    mAdapter.notifyAddListItem(item);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getUploadLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    saveDataAndFinish();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void saveDataAndFinish() {
        ToastUtils.showToast("上传成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvCommonInfoDetailsSubmit)
                .addOnClickListener()
                .submit();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == mBindingView.tvCommonInfoDetailsSubmit.getId()) {
            mViewModel.submit(mAdapter);
        }
    }


    //监听条目点击事件 需要跳转其他Activity所以交由Activity处理
    @Override
    public void onItemClick(View view, int position) {
        onItemClick(view, position, 0);
    }

    @Override
    public void onItemClick(View view, int groupPosition, int subPosition) {
        InfoDetailsClickBean bean = new InfoDetailsClickBean();
        bean.setData(mAdapter.getContentItemData(groupPosition));
        bean.setPosition(groupPosition);
        bean.setSubPosition(subPosition);
        bean.setViewId(view.getId());
        onInfoDetailsItemClick(bean);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case 101:
                    updateSelectData(data, SlaughterWeighingBatchListActivity.TAG_DATA);
                    break;
            }
        }
    }

    private void updateSelectData(@Nullable Intent data, String tag) {
        if (data == null) {
            ToastUtils.showToast("数据异常");
            return;
        }
        SelectItemSupport listBean = (SelectItemSupport) data.getSerializableExtra(tag);
        update(mViewModel.getClickBean().getData().key, new InfoDetailsDemoBean.ValueBean(listBean.getShowName(), listBean.getStringItemId()));

        update("屠宰数量", new InfoDetailsDemoBean.ValueBean(listBean.getExtraData(), null));
        update("重量1", new InfoDetailsDemoBean.ValueBean());

        int batchCount = Integer.parseInt(listBean.getExtraData());
        mViewModel.setCount(batchCount);
        ArrayList<InfoDetailsDemoBean> temp = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            InfoDetailsDemoBean bean = mAdapter.getDataList().get(i);
            temp.add(bean);
        }
        temp.addAll(mViewModel.getItem(true));
        mAdapter.notifyRefreshList(temp);
    }

    public void update(String key, InfoDetailsDemoBean.ValueBean value) {
        mAdapter.update(key, value);
    }

    @Subscribe
    public void onInfoDetailsItemClick(InfoDetailsClickBean clickBean) {
        mViewModel.setClickBean(clickBean);

        InfoDetailsDemoBean data = mViewModel.getClickBean().getData();
        switch (data.key) {
            case "屠宰批次":
                SlaughterWeighingBatchListActivity.start(this, 101, "选择屠宰批次");
                break;

        }
    }

    public static void start(Activity context, int requestCode) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(null);
            Intent intent = new Intent(context, SlaughterWeighingDetailsActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void start(Activity context, SlaughterWeighingDetailsListBean data) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(data);
            Intent intent = new Intent(context, SlaughterWeighingDetailsActivity.class);
            context.startActivity(intent);
        }
    }
}
