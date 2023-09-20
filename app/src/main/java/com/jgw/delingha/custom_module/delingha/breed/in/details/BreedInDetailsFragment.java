package com.jgw.delingha.custom_module.delingha.breed.in.details;


import android.text.TextUtils;
import android.view.View;


import com.jgw.common_library.base.ui.BaseFragment;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInDetailsBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.custom_module.delingha.image_preview.ImagePreviewActivity;
import com.jgw.delingha.databinding.FragmentCommonBreedDetailsBinding;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;


import org.greenrobot.eventbus.Subscribe;


/**
 * @Author CJM
 * @Date 2023/6/14 09:32
 * @Description 养殖进场 详情 Fragment
 */
public class BreedInDetailsFragment extends BaseFragment<BreedInDetailsViewModel, FragmentCommonBreedDetailsBinding> {

    public InfoDetailsRecyclerAdapter mAdapter;

    @Override
    protected void initView() {

    }

    @Override
    protected void initFragmentData() {
        mAdapter = new InfoDetailsRecyclerAdapter();
        mBindingView.rvCommonInfoDetails.setAdapter(mAdapter);

        BreedInDetailsBean tempData = MMKVUtils.getTempData(BreedInDetailsBean.class);
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
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });

    }

    @Override
    protected void initListener() {
        super.initListener();
        ClickUtils.register(this)
                .addOnClickListener()
                .submit();
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

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

    @Subscribe
    public void onInfoDetailsItemClick(InfoDetailsClickBean clickBean) {
        mViewModel.setClickBean(clickBean);

        InfoDetailsDemoBean data = mViewModel.getClickBean().getData();
        switch (data.key) {
            case "图片":
                int viewId = clickBean.getViewId();
                InfoDetailsDemoBean.ValueBean value = data.value;
                int subPosition = clickBean.getSubPosition();
                if (viewId == R.id.rl_info_details_img) {
                    if (value != null && !TextUtils.isEmpty(value.valueStr)) {
                        String[] split = value.valueStr.split(",");
                        if (subPosition < split.length) {
                            ImagePreviewActivity.start(context, split[subPosition]);
                        }
                    }
                } else if (viewId == R.id.iv_info_details_img_delete) {
                    mViewModel.removeImage(mAdapter, data, subPosition);
                }
                break;
        }
    }
}
