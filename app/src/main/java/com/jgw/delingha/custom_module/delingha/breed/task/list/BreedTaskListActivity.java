package com.jgw.delingha.custom_module.delingha.breed.task.list;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedTaskListBean;
import com.jgw.delingha.custom_module.delingha.select_list.breed_task.BreedTaskTypeListActivity;
import com.jgw.delingha.custom_module.delingha.select_list.common_list.CommonSelectListActivity;
import com.jgw.delingha.databinding.ActivityBreedTaskListBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.HashMap;
import java.util.List;

/**
 * @author : hwj
 * @date : 2023-8-8 10:03:40
 * description : 养殖任务 列表 Activity
 */

public class BreedTaskListActivity extends BaseActivity<BreedTaskListViewModel, ActivityBreedTaskListBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private BreedTaskListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请输入搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        setTitle("任务详情");
        mAdapter = new BreedTaskListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());

        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getBreedTaskListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    mBindingView.refreshLayout.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    mBindingView.refreshLayout.setRefreshing(false);
                    loadList(resource.getData());
                    break;
                case Resource.ERROR:
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
            }
        });
        mViewModel.getCompleteBreedTaskLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    mViewModel.refreshList();
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
    }

    private void loadList(List<? extends SelectItemSupport> data) {
        if (mViewModel.getCurrentPage() == 1) {
            mAdapter.notifyRefreshList(data);
        } else {
            mAdapter.notifyAddListItem(data);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();

        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.tvSelectCommonBottomButton)
                .addView(mBindingView.includeSelect.tvTaskStatus)
                .addView(mBindingView.includeSelect.tvTaskType)
                .addView(mBindingView.includeSelect.ivTaskStatusRemove)
                .addView(mBindingView.includeSelect.ivTaskTypeRemove)
                .submit();
        mBindingView.refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);

        if (searchView != null) {
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mViewModel.setSearchText(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        mBindingView.includeSelect.tvTaskType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mBindingView.includeSelect.ivTaskTypeRemove.setVisibility(TextUtils.isEmpty(editable.toString()) ? View.GONE : View.VISIBLE);
            }
        });
        mBindingView.includeSelect.tvTaskStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mBindingView.includeSelect.ivTaskStatusRemove.setVisibility(TextUtils.isEmpty(editable.toString()) ? View.GONE : View.VISIBLE);

            }
        });
        mBindingView.rvSelectCommon.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == mBindingView.includeSelect.tvTaskType.getId()) {
            CommonSelectListActivity.start(101, this, CommonSelectListActivity.BREED_TASK_TYPE);
        }else if (id == mBindingView.includeSelect.tvTaskStatus.getId()) {
            BreedTaskTypeListActivity.start(this,102);
        }else if (id == mBindingView.includeSelect.ivTaskStatusRemove.getId()) {
            mBindingView.includeSelect.tvTaskStatus.setText("");
            mViewModel.setTaskStatus(1);
            mViewModel.refreshList();
        }else if (id == mBindingView.includeSelect.ivTaskTypeRemove.getId()) {
            mBindingView.includeSelect.tvTaskType.setText("");
            mViewModel.setTaskType("");
            mViewModel.refreshList();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        SelectItemSupport selectItemSupport = mAdapter.getDataList().get(position);
        BreedTaskListBean listBean = null;
        if (selectItemSupport instanceof BreedTaskListBean) {
            listBean = (BreedTaskListBean) selectItemSupport;
        }
        if (listBean==null){
            return;
        }
        String breedRecId = listBean.getTaskId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("taskId",breedRecId);
        if (id == R.id.tv_breed_task_list_complete) {
            mViewModel.completeBreedTask(map);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                mViewModel.refreshList();
                break;
            case 101:
                SelectItemSupport listBean = (SelectItemSupport) data.getSerializableExtra(CommonSelectListActivity.TAG_DATA);
                mBindingView.includeSelect.tvTaskType.setText(listBean.getShowName());
                mViewModel.setTaskType(listBean.getShowName());
                mViewModel.refreshList();
                break;
            case 102:
                SelectItemSupport listBean2 = (SelectItemSupport) data.getSerializableExtra(BreedTaskTypeListActivity.TAG_DATA);
                mBindingView.includeSelect.tvTaskStatus.setText(listBean2.getShowName());
                mViewModel.setTaskStatus(Integer.parseInt(listBean2.getStringItemId()));
                mViewModel.refreshList();
                break;
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }


    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, BreedTaskListActivity.class);
            context.startActivity(intent);
        }
    }

}
