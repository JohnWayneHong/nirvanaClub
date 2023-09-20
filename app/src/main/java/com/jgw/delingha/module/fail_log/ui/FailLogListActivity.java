package com.jgw.delingha.module.fail_log.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FailLogBean;
import com.jgw.delingha.databinding.ActivityFailLogListBinding;
import com.jgw.delingha.module.fail_log.adapter.FailLogListRecyclerAdapter;
import com.jgw.delingha.module.fail_log.viewmodel.FailLogListViewModel;
import com.jgw.delingha.utils.PickerUtils;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 上传失败日志列表
 */
public class FailLogListActivity extends BaseActivity<FailLogListViewModel, ActivityFailLogListBinding> implements SwipeRefreshLayout.OnRefreshListener
        , TextWatcher {

    private FailLogListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        int logTypeToVM = intent.getIntExtra("failLogType", -1);
        String houseListToVM = intent.getStringExtra("houseList");
        mViewModel.saveData(logTypeToVM, houseListToVM);
        setTitle(ResourcesUtils.getString(mViewModel.getTitleRes()));

        mAdapter = new FailLogListRecyclerAdapter(logTypeToVM);
        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.requestFirstData();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getFailLogList().observe(this, new Observer<Resource<FailLogBean>>() {
            @Override
            public void onChanged(Resource<FailLogBean> failLogBeanResource) {
                switch (failLogBeanResource.getLoadingStatus()) {
                    case Resource.LOADING:
                        mBindingView.refreshLayout.setRefreshing(true);
                        break;
                    case Resource.SUCCESS:
                        mBindingView.refreshLayout.setRefreshing(false);
                        mViewModel.setFailLogListBean(failLogBeanResource.getData(), mAdapter);
                        break;
                    case Resource.ERROR:
                        mBindingView.refreshLayout.setRefreshing(false);
                        break;
                }
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvFailLogChooseDate)
                .addOnClickListener()
                .submit();
        mBindingView.refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvSelectCommon.addOnScrollListener(new com.jgw.common_library.listener.OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
        mBindingView.etFailLogScan.addTextChangedListener(this);
    }

    @Override
    public void onRefresh() {
        mViewModel.onRefresh();
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String code = event.mCode;
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            code = CheckCodeUtils.getMatcherDeliveryNumber(code);
            ScanCodeService.playSuccess();
            mBindingView.etFailLogScan.setText(code);
            mViewModel.setCode(code);
        } else {
            ScanCodeService.playError();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.tv_fail_log_choose_date) {
            showTimePicker(this);
        }
    }

    public void showTimePicker(Context context) {
        mBindingView.ivFailLogChooseDateArrow.setSelected(true);
        PickerUtils.showTimePicker(context, new PickerUtils.onTimePickedListener() {
            @Override
            public void onTimePicked(String time) {
                mBindingView.tvFailLogChooseDate.setText(time);
                mBindingView.ivFailLogChooseDateArrow.setSelected(false);
                mViewModel.setData(time);
                onRefresh();
            }
        }, new PickerUtils.onCancelPickedListener() {
            @Override
            public void onCancelPicked() {
                mBindingView.ivFailLogChooseDateArrow.setSelected(false);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        mViewModel.setCode(s.toString());
        onRefresh();
    }

    /**
     * from fail log page
     */
    public static void start(Context context, int failLogType) {
        start(context, failLogType, null);
    }

    /**
     * from task list page
     */
    public static void start(Context context, int failLogType, String houseList) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, FailLogListActivity.class);
            intent.putExtra("failLogType", failLogType);
            intent.putExtra("houseList", houseList);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}

