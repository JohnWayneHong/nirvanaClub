package com.jgw.delingha.custom_module.delingha.breed.enter_fence.details;

import static com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity.TYPE_IN_ALL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.SelectDialogUtil;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.EnterFenceDetailsBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.common.BaseScanActivity;
import com.jgw.delingha.custom_module.delingha.image_preview.ImagePreviewActivity;
import com.jgw.delingha.custom_module.delingha.select_list.enter_fence_list.EnterFenceBatchListActivity;
import com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity;
import com.jgw.delingha.databinding.ActivityCommonInfoDetailsBinding;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.utils.CameraGalleryUtils;
import com.jgw.delingha.utils.PermissionUtils;
import com.jgw.scan_code_library.CheckCodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author CJM
 * @Date 2023/6/14 09:32
 * @Description 入栏记录 详情 || 添加 Activity
 */
public class EnterFenceDetailsActivity extends BaseScanActivity<EnterFenceDetailsViewModel, ActivityCommonInfoDetailsBinding> {

    public InfoDetailsRecyclerAdapter mAdapter;
    private Uri currentUri;
    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle("入栏");
        mAdapter = new InfoDetailsRecyclerAdapter();
        mBindingView.rvCommonInfoDetails.setAdapter(mAdapter);

        EnterFenceDetailsBean tempData = MMKVUtils.getTempData(EnterFenceDetailsBean.class);
        mBindingView.tvCommonInfoDetailsSubmit.setVisibility(tempData == null ? View.VISIBLE : View.GONE);
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
        mViewModel.getCustomerInfoLiveData().observe(this, resource -> {
            CustomerEntity entity = resource.getData();
            update(mViewModel.getClickBean().getData().key, new InfoDetailsDemoBean.ValueBean(entity.getCustomerName(), entity.getCustomerId()));
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
        Uri data1;
        if (resultCode == -1) {
            switch (requestCode) {
                case 101:
                    updateSelectData(data, PigstyListActivity.TAG_DATA);
                    SelectItemSupport listBean = (SelectItemSupport) data.getSerializableExtra(PigstyListActivity.TAG_DATA);
                    mViewModel.setPigstyId(listBean.getStringItemId());
                    break;
                case 102:
                    updateSelectData(data, EnterFenceBatchListActivity.TAG_DATA);
                    break;
                case CameraGalleryUtils.START_CAMERA:
                    if (currentUri != null) {
                        mViewModel.selectImage(mAdapter, mViewModel.getClickBean().getData(), currentUri);
                    }
                    break;
                case CameraGalleryUtils.CHOOSE_PHOTO:
                    if (data != null) {
                        data1 = data.getData();
                        mViewModel.selectImage(mAdapter, mViewModel.getClickBean().getData(), data1);
                    }
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
                PigstyListActivity.start(this, 101,TYPE_IN_ALL);
                break;
            case "养殖批次":
                EnterFenceBatchListActivity.start(this, 102, "选择养殖批次");
                break;
            case "图片":
                int viewId = clickBean.getViewId();
                InfoDetailsDemoBean.ValueBean value = data.value;
                int subPosition = clickBean.getSubPosition();
                if (viewId == R.id.rl_info_details_img) {
                    if (value != null && !TextUtils.isEmpty(value.valueStr)) {
                        String[] split = value.valueStr.split(",");
                        if (subPosition < split.length) {
                            ImagePreviewActivity.start(this, split[subPosition]);
                        } else {
                            clickSelectImage();
                        }
                    } else {
                        clickSelectImage();
                    }
                } else if (viewId == R.id.iv_info_details_img_delete) {
                    mViewModel.removeImage(mAdapter, data, subPosition);
                }
                break;
        }
    }

    boolean isAllGranted;

    private void clickSelectImage() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("拍照");
        strings.add("从相册中选择");
        SelectDialogUtil.showSelectDialog(this, strings, (view, position, s, selectDialog) -> {
            switch (position) {
                case 0:
                    isAllGranted = PermissionUtils.checkPermissionAllGranted(EnterFenceDetailsActivity.this, CameraGalleryUtils.permissions);
                    if (isAllGranted) {
                        currentUri = CameraGalleryUtils.startCamera(EnterFenceDetailsActivity.this);
                    } else {
                        ActivityCompat.requestPermissions(EnterFenceDetailsActivity.this, CameraGalleryUtils.permissions, CameraGalleryUtils.MY_PERMISSION_REQUEST_CODE);
                    }
                    break;
                case 1:
                    isAllGranted = PermissionUtils.checkPermissionAllGranted(EnterFenceDetailsActivity.this, CameraGalleryUtils.permissions);
                    if (isAllGranted) {
                        CameraGalleryUtils.startAlbum(EnterFenceDetailsActivity.this);
                    } else {
                        ActivityCompat.requestPermissions(EnterFenceDetailsActivity.this, CameraGalleryUtils.permissions, CameraGalleryUtils.MY_PERMISSION_REQUEST_CODE);
                    }
                    break;
            }
            selectDialog.dismissWithAnimation();
        });
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public static void start(Activity context, int requestCode) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(null);
            Intent intent = new Intent(context, EnterFenceDetailsActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }

    public static void start(Activity context, EnterFenceDetailsBean data) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(data);
            Intent intent = new Intent(context, EnterFenceDetailsActivity.class);
            context.startActivity(intent);
        }
    }
}
