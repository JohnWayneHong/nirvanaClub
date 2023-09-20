package com.jgw.delingha.custom_module.delingha.slaughter.in.list;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.CustomApplication;
import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import top.zibin.luban.OnCompressListener;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class SlaughterInListViewModel extends BaseViewModel {
    private final SlaughterInListModel model;
    private final MutableLiveData<String> mOrderListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mSlaughterInDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mDeleteSlaughterInLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mGetUpdateImageLiveData = new MutableLiveData<>();
    private int mPage = 1;
    private List<? extends SelectItemSupport> mList = new ArrayList<>();
    private String mSearch;

    public SlaughterInListViewModel(@NonNull Application application) {
        super(application);
        model = new SlaughterInListModel();
    }

    public void setDataList(List<? extends SelectItemSupport> dataList) {
        mList = dataList;
    }

    public int getCurrentPage() {
        return mPage;
    }

    public LiveData<Resource<List<? extends SelectItemSupport>>> getSlaughterInListLiveData() {
        return Transformations.switchMap(mOrderListLiveData, search -> model.getSlaughterInList(search, mPage));
    }

    public void setSearchText(String search) {
        mPage = 1;
        mSearch = search;
        mOrderListLiveData.setValue(search);
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        mOrderListLiveData.setValue(mSearch);
    }

    public void refreshList() {
        mPage = 1;
        mOrderListLiveData.setValue(mSearch);
    }


    public void getSlaughterInDetails(String breedInRecId) {
        mSlaughterInDetailsLiveData.setValue(breedInRecId);
    }


    public LiveData<Resource<SlaughterInDetailsBean>> getSlaughterInDetailsLiveData() {
        return Transformations.switchMap(mSlaughterInDetailsLiveData, model::getSlaughterInDetails);
    }

    public void deleteSlaughterIn(String inFactoryId) {
        mDeleteSlaughterInLiveData.setValue(inFactoryId);
    }


    public LiveData<Resource<String>> getDeleteSlaughterInLiveData() {
        return Transformations.switchMap(mDeleteSlaughterInLiveData, model::deleteSlaughterIn);
    }

    public void selectImage(CustomRecyclerAdapter adapter, String certificateImageUrl,@NonNull Uri uri) {
        File file = BitmapUtils.getRealPathFromUri(CustomApplication.getCustomApplicationContext(), uri);
        BitmapUtils.getBitmap(file, new OnCompressListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(File file) {
                long length1 = file.length();
                LogUtils.xswShowLog("");
                if (length1 > 0) {
                    Uri uri = Uri.fromFile(file);

                    tempImageUrl = uri.toString();
                    getUpdateImage();

                } else {
                    ToastUtils.showToast("图片损坏,请重新拍摄");
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showToast("未知错误");
            }
        });


    }
    private String tempImageUrl;

    private String certificateImageUrl;
    private String tempId;
    public void saveTempId(String id ) {
        tempId = id;
    }

    public String getSaveTempImageUrl() {
        return tempImageUrl;
    }

    public String getCertificateImageUrl() {
        return certificateImageUrl;
    }

    public void setCertificateImageUrl(String certificateImageUrl) {
        this.certificateImageUrl = certificateImageUrl;
    }


    public void getUpdateImage() {
        mGetUpdateImageLiveData.setValue(getSaveTempImageUrl());
    }

    public LiveData<Resource<String>> getUpdateImageLiveData() {
        return Transformations.switchMap(mGetUpdateImageLiveData, input -> model.getUpdateImage(tempId,input,getCertificateImageUrl()));
    }
}
