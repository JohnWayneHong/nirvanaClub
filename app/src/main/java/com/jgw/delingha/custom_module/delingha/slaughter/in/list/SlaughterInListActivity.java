package com.jgw.delingha.custom_module.delingha.slaughter.in.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.base.ui.BaseActivity;
import com.jgw.common_library.event.CommonEvent;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.listener.OnLoadMoreListener;
import com.jgw.common_library.utils.CommonDialogUtil;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.SelectDialogUtil;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.common_library.widget.commonDialog.CommonDialog;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.SlaughterInListBean;
import com.jgw.delingha.custom_module.delingha.slaughter.in.details.SlaughterInDetailsActivity;
import com.jgw.delingha.databinding.ActivityCommonSelectListButtonBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.utils.CameraGalleryUtils;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.utils.PermissionUtils;
import com.jgw.scan_code_library.ScanCodeService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023年7月5日09:32:48
 * 养殖进场列表
 */
public class SlaughterInListActivity extends BaseActivity<SlaughterInListViewModel, ActivityCommonSelectListButtonBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private EditText searchView;
    private SlaughterInListRecyclerAdapter mAdapter;

    private int position;
    private Uri currentUri;

    @Override
    protected void initView() {
        searchView = getSearchView();
        if (searchView != null) {
            searchView.setHint("请输入屠宰批次模糊搜索");
        }
        mBindingView.refreshLayout.setColorSchemeResources(R.color.main_color);
        mBindingView.rvcSelectCommon.setEmptyLayout(R.layout.item_empty);
        mBindingView.tvSelectCommonBottomButton.setText("新增进场记录");
    }

    @Override
    protected void initData() {
        setTitle("屠宰进场记录");
        mAdapter = new SlaughterInListRecyclerAdapter();
        mViewModel.setDataList(mAdapter.getDataList());

        mBindingView.rvSelectCommon.setAdapter(mAdapter);
        mViewModel.refreshList();
    }

    @Override
    public void initLiveData() {
        super.initLiveData();
        mViewModel.getSlaughterInListLiveData().observe(this, resource -> {
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
        mViewModel.getSlaughterInDetailsLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();
                    SlaughterInDetailsActivity.start(this, resource.getData());
                    break;
                case Resource.ERROR:
                    dismissLoadingDialog();
                    break;
            }
        });
        mViewModel.getDeleteSlaughterInLiveData().observe(this, resource -> {
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
        mViewModel.getUpdateImageLiveData().observe(this, resource -> {
            switch (resource.getLoadingStatus()) {
                case Resource.LOADING:
                    showLoadingDialog();
                    break;
                case Resource.SUCCESS:
                    dismissLoadingDialog();

                    SelectItemSupport selectItemSupport = mAdapter.getDataList().get(position);
                    SlaughterInListBean listBean = null;
                    if (selectItemSupport instanceof SlaughterInListBean) {
                        listBean = (SlaughterInListBean) selectItemSupport;
                    }
                    if (listBean == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(mViewModel.getCertificateImageUrl())) {
                        listBean.setCertificateImageUrl(MMKVUtils.getString(ConstantUtil.SAVA_TEMP_IMAGE));

                    }else {
                        String[] split = mViewModel.getCertificateImageUrl().split(",");
                        ArrayList<String> strings = new ArrayList<>(Arrays.asList(split));
                        strings.add(MMKVUtils.getString(ConstantUtil.SAVA_TEMP_IMAGE));

                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < strings.size(); j++) {
                            sb.append(strings.get(j));
                            if (j != strings.size() - 1) {
                                sb.append(",");
                            }
                        }
                        listBean.setCertificateImageUrl(sb.toString());
                    }

                    mAdapter.notifyItemChanged(position);

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
            SlaughterInDetailsActivity.start(this, 1);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        SelectItemSupport selectItemSupport = mAdapter.getDataList().get(position);
        SlaughterInListBean listBean = null;
        if (selectItemSupport instanceof SlaughterInListBean) {
            listBean = (SlaughterInListBean) selectItemSupport;
        }
        if (listBean == null) {
            return;
        }
        String id1 = listBean.id;
        if (id == R.id.tv_slaughter_in_list_details) {
            mViewModel.getSlaughterInDetails(id1);
        } else if (id == R.id.tv_slaughter_in_list_delete) {
            CommonDialogUtil.showSelectDialog(this, "提示", "确认是否删除?", "取消", "确认", new CommonDialog.OnButtonClickListener() {
                @Override
                public void onRightClick() {
                    mViewModel.deleteSlaughterIn(id1);

                }
            });
        } else if (id == R.id.tv_slaughter_in_list_upload) {
            this.position = position;
            mViewModel.saveTempId(listBean.id);
            mViewModel.setCertificateImageUrl(listBean.certificateImageUrl);
            clickSelectImage();
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
                    isAllGranted = PermissionUtils.checkPermissionAllGranted(SlaughterInListActivity.this, CameraGalleryUtils.permissions);
                    if (isAllGranted) {
                        currentUri = CameraGalleryUtils.startCamera(SlaughterInListActivity.this);
                    } else {
                        ActivityCompat.requestPermissions(SlaughterInListActivity.this, CameraGalleryUtils.permissions, CameraGalleryUtils.MY_PERMISSION_REQUEST_CODE);
                    }
                    break;
                case 1:
                    isAllGranted = PermissionUtils.checkPermissionAllGranted(SlaughterInListActivity.this, CameraGalleryUtils.permissions);
                    if (isAllGranted) {
                        CameraGalleryUtils.startAlbum(SlaughterInListActivity.this);
                    } else {
                        ActivityCompat.requestPermissions(SlaughterInListActivity.this, CameraGalleryUtils.permissions, CameraGalleryUtils.MY_PERMISSION_REQUEST_CODE);
                    }
                    break;
            }
            selectDialog.dismissWithAnimation();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri data1;
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                mViewModel.refreshList();
                break;
            case CameraGalleryUtils.START_CAMERA:
                if (currentUri != null) {
                    mViewModel.selectImage(mAdapter, mViewModel.getSaveTempImageUrl(), currentUri);
                }
                break;
            case CameraGalleryUtils.CHOOSE_PHOTO:
                if (data != null) {
                    data1 = data.getData();
                    mViewModel.selectImage(mAdapter,mViewModel.getSaveTempImageUrl(), data1);
                }
                break;
        }
    }

    @Subscribe
    public void onScanQRCode(CommonEvent.ScanQRCodeEvent event) {
        ScanCodeService.playSuccess();
        if (searchView != null) {
            searchView.setText(event.mCode);
        }
    }


    @Override
    public void onRefresh() {
        mViewModel.refreshList();
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

    public static void start(Context context) {
        if (BaseActivity.isActivityNotFinished(context)) {
            Intent intent = new Intent(context, SlaughterInListActivity.class);
            context.startActivity(intent);
        }
    }


}
