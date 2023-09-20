package com.jgw.delingha.module.select_list.common;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.ViewDataBinding;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public abstract class BaseSelectItemListActivity<VM extends BaseSelectItemListViewModel, SV extends ViewDataBinding> extends BaseActivity<VM, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public BaseSelectItemRecyclerAdapter mAdapter;
    private EditText searchView;

    @Override
    protected void initView() {
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initData() {
        mAdapter = new BaseSelectItemRecyclerAdapter();
        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.setDataList(mAdapter.getDataList());
        mViewModel.loadList();
    }

    @Override
    public void initLiveData() {
        mViewModel.getListDataLiveData().observe(this, resource -> {
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
                    ToastUtils.showToast(resource.getErrorMsg());
                    break;
            }
        });
    }

    private void loadList(List<? extends SelectItemSupport> newList) {
        int currentPage = mViewModel.getCurrentPage();
        if (currentPage == 1) {
            mAdapter.notifyRefreshList(newList);
        } else {
            mAdapter.notifyAddListItem(newList);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        searchView = getSearchView();
        if (searchView != null) {
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    mViewModel.doSearch(str);
                }
            });
        }
        ClickUtils.register(this)
                .addOnClickListener()
                .addView(mBindingView.getRoot())
                .submit();
        mBindingView.refreshLayout.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
        mBindingView.rvSelectCommon.addOnScrollListener(new com.jgw.common_library.listener.OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        String scanCode = event.mCode;
        if (searchView != null) {
            searchView.setText(scanCode);
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

    @Override
    public void onItemClick(View view, int position) {
        onSelectItem(view, position);
    }

    protected void onSelectItem(View view, int position) {
        Intent intent = new Intent();
        SelectItemSupport selectItemSupport = mAdapter.getDataList().get(position);
        intent.putExtra(getExtraName(), mAdapter.getDataList().get(position).getItemId());
        String extraDataName = getExtraDataName();
        if (!TextUtils.isEmpty(extraDataName)) {
            intent.putExtra(extraDataName, selectItemSupport);
        }
        String extraIDName = getExtraIDName();
        if (!TextUtils.isEmpty(extraIDName)) {
            intent.putExtra(extraIDName, selectItemSupport.getItemId());
        }
        setResult(RESULT_OK, intent);
        finish();
    }
    public abstract String getExtraName();

    public String getExtraIDName() {
        return null;
    }

    public String getExtraDataName() {
        return null;
    }

    @Override
    public void onRefresh() {
        mViewModel.onRefresh();
    }
}
