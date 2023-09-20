package com.jgw.delingha.custom_module.delingha.breed.out.details;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedEarAssociateBean;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedOutDetailsBean;
import com.jgw.delingha.custom_module.delingha.breed.in.details.BreedInAssociateEarAdapter;
import com.jgw.delingha.databinding.ActivityCommonBreedDetailsEarBinding;
import com.jgw.scan_code_library.CheckCodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Objects;

/**
 * @Author CJM
 * @Date 2023/6/14 09:32
 * @Description 养殖离场 关联耳号 Fragment
 */
public class BreedOutDetailsEarFragment extends BaseFragment<BreedOutDetailsViewModel, ActivityCommonBreedDetailsEarBinding> implements SwipeRefreshLayout.OnRefreshListener{

    public BreedInAssociateEarAdapter mAdapter;

    private BreedOutDetailsBean tempData;

    @Override
    protected void initView() {
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
    }

    @Override
    protected void initFragmentData() {
        mAdapter = new BreedInAssociateEarAdapter();
        mViewModel.setDataList(mAdapter.getDataList());
        mBindingView.rvBreedEarInfo.setAdapter(mAdapter);

        tempData = MMKVUtils.getTempData(BreedOutDetailsBean.class);
        mViewModel.loadEarCodeList(tempData);
    }


    @Override
    public void initLiveData() {
        mViewModel.getEarCodeListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    mBindingView.refreshLayout.setRefreshing(true);
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    loadList(resource.getData());
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    mBindingView.refreshLayout.setRefreshing(false);
                    break;
            }
        });
        mViewModel.getEarCodeListTotalLiveData().observe(this, resource -> {
            updateCountView(Long.parseLong(resource));
        });
        mViewModel.getAddEarCodeAssociationLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    ToastUtils.showToast("耳号关联成功！");
                    mViewModel.loadEarCodeList(tempData);
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

    }

    private void loadList(List<BreedInAssociateBean> data) {
        if (mViewModel.getCurrentPage() == 1) {
            mAdapter.notifyRefreshList(data);
        } else {
            mAdapter.notifyAddListItem(data);
        }
        updateCountView(mAdapter.getDataList().size());
    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addView(mBindingView.layoutBreedActionFooter.tvScanBack)
                .addView(mBindingView.layoutBreedActionFooter.tvInputCode)
                .addOnClickListener()
                .submit();

        mBindingView.refreshLayout.setOnRefreshListener(this);
        mBindingView.rvBreedEarInfo.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onScrollToLastItem() {
                mViewModel.onLoadMore();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == mBindingView.layoutBreedActionFooter.tvScanBack.getId()) {
            jumpScanBack();
        }else if (view.getId() == mBindingView.layoutBreedActionFooter.tvInputCode.getId()) {
            CommonDialogUtil.showInputDialog(context, "耳号", "请输入", "取消", "确定", new CommonDialog.OnButtonClickListener() {
                @Override
                public boolean onInput(String input) {
                    ((BreedOutDetailsActivity) Objects.requireNonNull(getActivity())).scanable = true;
                    EventBus.getDefault().post(new CommonEvent.ScanRFIDEvent(input));
                    return true;
                }
            });
        }
    }

    private void jumpScanBack() {
        if (mAdapter.isEmpty()) {
            ToastUtils.showToast("请先扫码关联");
            return;
        }
        BreedOutScanBackActivity.start(this, 1,tempData.breedOutBatch);
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanRFIDEvent event) {

        if (getActivity() instanceof BreedOutDetailsActivity) {
            if (((BreedOutDetailsActivity) getActivity() ).scanable){
                ((BreedOutDetailsActivity) getActivity() ).scanable = false;
                String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
                if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
                    mViewModel.addEarCodeAssociation(event.mCode);
                }
            }
        }
    }

    public void updateCountView(long count) {
        mBindingView.layoutBreedActionFooter.tvCodeAmount.setText(Html.fromHtml("共<font color='#03A9F4'>" + count + "</font>条"));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.refreshList();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        mViewModel.refreshList();
    }
}
