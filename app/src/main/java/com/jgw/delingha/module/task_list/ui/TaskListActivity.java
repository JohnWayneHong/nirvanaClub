package com.jgw.delingha.module.task_list.ui;

import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_IN;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.SelectDialogUtil;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.TaskBean;
import com.jgw.delingha.databinding.ActivityTaskListBinding;
import com.jgw.delingha.module.fail_log.ui.FailLogListActivity;
import com.jgw.delingha.module.task_list.adapter.TaskListRecyclerAdapter;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.utils.PickerUtils;

import java.util.Date;

/**
 * 任务状态列表
 */
public class TaskListActivity extends BaseActivity<TaskListViewModel, ActivityTaskListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private TaskListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        setTitle(ResourcesUtils.getString(R.string.task_list_title));
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mViewModel.initMenu();
        Intent intent = getIntent();
        int taskType = intent.getIntExtra("taskType", TYPE_TASK_STOCK_IN);
        mViewModel.setTaskType(taskType);

        mAdapter = new TaskListRecyclerAdapter();
        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.setTaskList(mAdapter.getDataList());
        mViewModel.requestFirstData();
    }

    @Override
    public void initLiveData() {
        mViewModel.getTaskList().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.refreshLayout.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.refreshLayout.setRefreshing(false);
                    mViewModel.loadList(resource.getData(), mAdapter);
                    break;
                case Resource.ERROR:
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
                default:
            }
            mViewModel.getTaskTypeLiveData().observe(this, taskTypeStr -> mBindingView.tvTypeValue.setText(taskTypeStr));
        });
        mViewModel.getTryAgainRunTaskLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    mViewModel.onRefresh();
                    dismissLoadingDialog();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
                default:
            }
        });
    }



    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.tvTypeValue, mBindingView.tvStartTimeValue, mBindingView.tvEndTimeValue)
                .addOnClickListener()
                .submit();
        mBindingView.refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvSelectCommon.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
    }

    @Override
    public void onRefresh() {
        mViewModel.onRefresh();
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        if (id == R.id.tv_task_item_fail_info) {
            TaskBean data = mAdapter.getDataList().get(position);
            FailLogListActivity.start(this, data.taskType, data.houseList);
        } else if (id == R.id.tv_task_item_try_again) {
            mViewModel.tryAgainRunTask(position);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.tv_type_value) {
            showTypeSelectDialog(this);
        } else if (id == R.id.tv_start_time_value) {
            showTimePicker(true, this);
        } else if (id == R.id.tv_end_time_value) {
            showTimePicker(false, this);
        }
    }

    public void showTypeSelectDialog(Context context) {
        SelectDialogUtil.showSelectDialog(context, mViewModel.getTaskTypeDescList(), 300, (view, position, string, dialog) -> {
            Integer taskType = mViewModel.getTaskTypeMap().get(string);
            mViewModel.setTaskType(taskType == null ? TYPE_TASK_STOCK_IN : taskType);
        });
    }

    public void showTimePicker(boolean isStartTime, Context context) {
        String endTime = mBindingView.tvEndTimeValue.getText().toString();
        String startTime = mBindingView.tvStartTimeValue.getText().toString();
        Date startDate = null, endDate = null;
        if (!TextUtils.isEmpty(startTime) && !isStartTime) {
            startDate = FormatUtils.decodeDate(startTime,"yyyy-MM-dd");
        }
        if (!TextUtils.isEmpty(endTime) && isStartTime) {
            endDate = FormatUtils.decodeDate(endTime,"yyyy-MM-dd");
        }
        PickerUtils.showTimePicker(startDate, endDate, context, time -> {
            if (isStartTime) {
                mBindingView.tvStartTimeValue.setText(time);
                mViewModel.setStartTime(time);
            } else {
                mBindingView.tvEndTimeValue.setText(time);
                mViewModel.setEndTime(time);
            }
        });
    }

    /**
     * 通过菜单跳转 默认进入入库
     *
     * @param context context
     */
    public static void start(Context context) {
        start(TaskListViewModel.TYPE_TASK_STOCK_IN, context);
    }

    public static void start(int taskType, Context context) {
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, TaskListActivity.class);
            intent.putExtra("taskType", taskType);
            context.startActivity(intent);
        }
    }

}

