package com.jgw.delingha.custom_module.delingha.daily_management.weight.details;

import static com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity.TYPE_IN_ALL;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.Nullable;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.WeightTypeBean;
import com.jgw.delingha.custom_module.delingha.select_list.common_list.CommonSelectListActivity;
import com.jgw.delingha.custom_module.delingha.select_list.pigsty_list.PigstyListActivity;
import com.jgw.delingha.custom_module.delingha.select_list.weight_type_list.WeightTypeListActivity;
import com.jgw.delingha.databinding.ActivityCommonInfoDetailsBinding;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.utils.PickerUtils;

import org.greenrobot.eventbus.Subscribe;

/**
 * @Author CJM
 * @Date 2023/6/14 09:32
 * @Description 称重详情
 */
public class WeightDetailsActivity extends BaseActivity<WeightDetailsViewModel, ActivityCommonInfoDetailsBinding> {

    public InfoDetailsRecyclerAdapter mAdapter;
//    private Uri currentUri;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        setTitle("新增");
        mAdapter = new InfoDetailsRecyclerAdapter();
        mBindingView.rvCommonInfoDetails.setAdapter(mAdapter);

//        SlaughterInDetailsBean tempData = MMKVUtils.getTempData(SlaughterInDetailsBean.class);
//        mBindingView.tvCommonInfoDetailsSubmit.setVisibility(tempData == null ? View.VISIBLE : View.GONE);
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
                    break;
                case 102:
                    updateSelectData(data, WeightTypeListActivity.TAG_DATA);
                    break;
                case 103:
                    updateSelectData(data, CommonSelectListActivity.TAG_DATA);
                    break;
//                case CameraGalleryUtils.START_CAMERA:
//                    if (currentUri != null) {
//                        mViewModel.selectImage(mAdapter, mViewModel.getClickBean().getData(), currentUri);
//                    }
//                    break;
//                case CameraGalleryUtils.CHOOSE_PHOTO:
//                    if (data != null) {
//                        data1 = data.getData();
//                        mViewModel.selectImage(mAdapter, mViewModel.getClickBean().getData(), data1);
//                    }
//                    break;
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
        //整体盘点 隐藏栏舍号
        if (key.equals("称重类型")) {
            mAdapter.notifyRemoveListItem();
            WeightTypeBean bean = new WeightTypeBean();
            bean.weightTypeName = value.valueStr;
            bean.weightTypeId = value.valueId;
            mViewModel.loadList(bean);
        }

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
            case "称重类型":
                WeightTypeListActivity.start(this, 102);
                break;
            case "称重对象":
                CommonSelectListActivity.start(103, this, CommonSelectListActivity.ANIMALS_CLASSIFICATION);
                break;
//            case "图片":
//            case "动检合格证图片":
//                int viewId = clickBean.getViewId();
//                InfoDetailsDemoBean.ValueBean value = data.value;
//                int subPosition = clickBean.getSubPosition();
//                if (viewId == R.id.rl_info_details_img) {
//                    if (value != null && !TextUtils.isEmpty(value.valueStr)) {
//                        String[] split = value.valueStr.split(",");
//                        if (subPosition < split.length) {
//                            ImagePreviewActivity.start(this, split[subPosition]);
//                        } else {
//                            clickSelectImage();
//                        }
//                    } else {
//                        clickSelectImage();
//                    }
//                } else if (viewId == R.id.iv_info_details_img_delete) {
//                    mViewModel.removeImage(mAdapter, data, subPosition);
//                }
//                break;
        }
    }

//    boolean isAllGranted;
//
//    private void clickSelectImage() {
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("拍照");
//        strings.add("从相册中选择");
//        SelectDialogUtil.showSelectDialog(this, strings, (view, position, s, selectDialog) -> {
//            switch (position) {
//                case 0:
//                    isAllGranted = PermissionUtils.checkPermissionAllGranted(FeedingDetailsActivity.this, CameraGalleryUtils.permissions);
//                    if (isAllGranted) {
//                        currentUri = CameraGalleryUtils.startCamera(FeedingDetailsActivity.this);
//                    } else {
//                        ActivityCompat.requestPermissions(FeedingDetailsActivity.this, CameraGalleryUtils.permissions, CameraGalleryUtils.MY_PERMISSION_REQUEST_CODE);
//                    }
//                    break;
//                case 1:
//                    isAllGranted = PermissionUtils.checkPermissionAllGranted(FeedingDetailsActivity.this, CameraGalleryUtils.permissions);
//                    if (isAllGranted) {
//                        CameraGalleryUtils.startAlbum(FeedingDetailsActivity.this);
//                    } else {
//                        ActivityCompat.requestPermissions(FeedingDetailsActivity.this, CameraGalleryUtils.permissions, CameraGalleryUtils.MY_PERMISSION_REQUEST_CODE);
//                    }
//                    break;
//            }
//            selectDialog.dismissWithAnimation();
//        });
//    }

    public static void start(Activity context, int requestCode) {
        if (isActivityNotFinished(context)) {
            MMKVUtils.saveTempData(null);
            Intent intent = new Intent(context, WeightDetailsActivity.class);
            context.startActivityForResult(intent, requestCode);
        }
    }
//
//    public static void start(Activity context, SlaughterInDetailsBean data) {
//        if (isActivityNotFinished(context)) {
//            MMKVUtils.saveTempData(data);
//            Intent intent = new Intent(context, FeedingDetailsActivity.class);
//            context.startActivity(intent);
//        }
//    }
}
