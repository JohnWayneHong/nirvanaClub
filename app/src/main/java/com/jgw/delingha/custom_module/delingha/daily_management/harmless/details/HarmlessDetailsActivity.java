package com.jgw.delingha.custom_module.delingha.daily_management.harmless.details;

import static com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity.TYPE_IN_BATCH;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.common.BaseScanActivity;
import com.jgw.delingha.custom_module.delingha.select_list.breed_in_batch.BreedInBatchListActivity;
import com.jgw.delingha.custom_module.delingha.select_list.common_list.CommonSelectListActivity;
import com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity;
import com.jgw.delingha.databinding.ActivityCommonInfoDetailsBinding;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.utils.PickerUtils;
import com.jgw.scan_code_library.CheckCodeUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 无害化记录 详情 Activity
 */
public class HarmlessDetailsActivity extends BaseScanActivity<HarmlessDetailsViewModel, ActivityCommonInfoDetailsBinding> {

    public InfoDetailsRecyclerAdapter mAdapter;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle("新增");
        mAdapter = new InfoDetailsRecyclerAdapter();
        mBindingView.rvCommonInfoDetails.setAdapter(mAdapter);

        mViewModel.loadList(null);
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
        mViewModel.getBatchByCodeLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    if (resource.getData().breedInBatch.isEmpty()) {
                        ToastUtils.showToast("耳号暂未关联批次");
                        return;
                    }
                    update("养殖批次", new InfoDetailsDemoBean.ValueBean(resource.getData().breedInBatch, null));
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getBreedInByPigstyListLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.NEXT:
                    BreedInListBean data = resource.getData();
                    update("养殖批次", new InfoDetailsDemoBean.ValueBean(data.breedInBatch, data.breedInRecId));
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
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
                    SelectItemSupport listBean = (SelectItemSupport) data.getSerializableExtra(PigstyListActivity.TAG_DATA);
                    mViewModel.setPigstyId(listBean.getStringItemId());
                    mViewModel.setPigstyName(listBean.getShowName());
                    update("养殖批次", new InfoDetailsDemoBean.ValueBean());
                    update("栏舍号", new InfoDetailsDemoBean.ValueBean(listBean.getShowName(),listBean.getStringItemId()));
                    mViewModel.getBreedInByPigstyList(listBean.getStringItemId());
                    break;
                case 102:
                    updateSelectData(data, BreedInBatchListActivity.TAG_DATA);
                    break;
                case 103:
                    updateSelectData(data, CommonSelectListActivity.TAG_DATA);
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
    }

    public void update(String key, InfoDetailsDemoBean.ValueBean value) {
        mAdapter.update(key, value);
    }

    @Subscribe
    public void onInfoDetailsItemClick(InfoDetailsClickBean clickBean) {
        mViewModel.setClickBean(clickBean);

        InfoDetailsDemoBean data = mViewModel.getClickBean().getData();
        switch (data.key) {
            case "栏舍号":
                PigstyListActivity.start(this, 101,TYPE_IN_BATCH);
                break;
            case "养殖批次":
                if (TextUtils.isEmpty(mViewModel.getPigstyId())){
                    ToastUtils.showToast("请先选择栏舍");
                    break;
                }
                BreedInBatchListActivity.start(this, 102, "选择养殖批次",mViewModel.getPigstyId());
                break;
            case "处理日期":
                PickerUtils.showTimePicker(this, time -> update(mViewModel.getClickBean().getData().key, new InfoDetailsDemoBean.ValueBean(time, null)));
                break;
            case "处理对象":
                CommonSelectListActivity.start(103, this, CommonSelectListActivity.ANIMALS_CLASSIFICATION);
                break;
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanRFIDEvent event) {
        if (!scanable){
            return;
        }
        scanable = false;
        if (TextUtils.isEmpty(mViewModel.getPigstyId())){
            ToastUtils.showToast("请先选择栏舍");
            return;
        }
        String code = CheckCodeUtils.getMatcherDeliveryNumber(event.mCode);
        if (CheckCodeUtils.checkCode(code)) {//检查码是否符合规范
            mViewModel.getBatchByCode(code);
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


    public static void start(Activity context, int requestCode) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(null);
            Intent intent = new Intent(context, HarmlessDetailsActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
}
