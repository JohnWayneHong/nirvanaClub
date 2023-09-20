package com.jgw.delingha.custom_module.delingha.breed.enter_fence.list;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.EnterFenceListBean;
import com.jgw.delingha.custom_module.delingha.breed.enter_fence.details.EnterFenceDetailsActivity;
import com.jgw.delingha.databinding.ActivityCommonSelectListButtonBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023年7月6日13:57:58
 * 入栏记录列表 Activity
 */
public class EnterFenceListActivity extends BaseActivity<EnterFenceListViewModel, ActivityCommonSelectListButtonBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private EnterFenceListRecyclerAdapter mAdapter;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请输入栏舍号模糊搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
        mBindingView.tvSelectCommonBottomButton.setText("新增入栏记录");

    }

    @Override
    protected void initData() {
        setTitle("入栏记录");
        mAdapter = new EnterFenceListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());

        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getEnterFenceListLiveData().observe(this, resource -> {
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
        mViewModel.getEnterFenceDetailsLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    EnterFenceDetailsActivity.start(this, resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getDeleteEnterFenceLiveData().observe(this, resource -> {
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
        if (id == mBindingView.tvSelectCommonBottomButton.getId()) {
            EnterFenceDetailsActivity.start(this, 1);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        SelectItemSupport selectItemSupport = mAdapter.getDataList().get(position);
        EnterFenceListBean listBean = null;
        if (selectItemSupport instanceof EnterFenceListBean) {
            listBean = (EnterFenceListBean) selectItemSupport;
        }
        if (listBean==null){
            return;
        }
        String inFenceId = listBean.inFenceId;
        if (id == R.id.tv_enter_fence_list_details) {
            mViewModel.getEnterFenceDetails(inFenceId);
        } else if (id == R.id.tv_enter_fence_list_delete) {
            CommonDialogUtil.showSelectDialog(this, "提示", "确认是否删除?", "取消", "确认", new CommonDialog.OnButtonClickListener() {
                @Override
                public void onRightClick() {
                    mViewModel.deleteEnterFence(inFenceId);
                }
            });
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
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, EnterFenceListActivity.class);
            context.startActivity(intent);
        }
    }


}
