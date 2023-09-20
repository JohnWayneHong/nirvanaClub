package com.jgw.delingha.custom_module.delingha.breed.in.details;

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
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedInDetailsBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.options_details.utils.OptionsDetailsUtils;
import com.jgw.delingha.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.zibin.luban.OnCompressListener;

/**
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 养殖进场 关联耳号 || 详情 ViewModel
 */
public class BreedInDetailsViewModel extends BaseViewModel {
    private final BreedInDetailsModel model;
    private final MutableLiveData<BreedInDetailsBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mEarCodeListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mAddEarCodeListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<String>> mRemoveEarCodeListLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    private int mPage = 1;

    private List<BreedInAssociateBean> mList = new ArrayList<>();

    private String earCodeByBatch = "";
    private BreedInDetailsBean breedInDetailsBean;

    public BreedInDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new BreedInDetailsModel();
    }

    public void setDataList(List<BreedInAssociateBean> dataList) {
        mList = dataList;
    }

    //从model获取数据
    public void loadList(BreedInDetailsBean data) {
        mContentListLiveData.setValue(data);
    }

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mContentListLiveData, model::getListData);
    }

    public void selectImage(CustomRecyclerAdapter adapter, InfoDetailsDemoBean bean, @NonNull Uri uri) {
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
                    int i = adapter.getDataList().indexOf(bean);
                    if (i != -1) {
                        if (bean.value == null) {
                            bean.value = new InfoDetailsDemoBean.ValueBean("", "");
                        }
                        if (TextUtils.isEmpty(bean.value.valueStr)) {
                            bean.value.valueStr = uri.toString();
                        } else {
                            String[] split = bean.value.valueStr.split(",");
                            ArrayList<String> strings = new ArrayList<>(Arrays.asList(split));
                            strings.add(uri.toString());
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < strings.size(); j++) {
                                sb.append(strings.get(j));
                                if (j != strings.size() - 1) {
                                    sb.append(",");
                                }
                            }
                            bean.value.valueStr = sb.toString();
                        }
                        adapter.notifyItemChanged(i);
                    }
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


    public void submit(InfoDetailsRecyclerAdapter adapter) {
        List<InfoDetailsDemoBean> dataList = adapter.getDataList();
        if (OptionsDetailsUtils.checkItem(dataList)){
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("inDate", adapter.searchValueStrByKey("进场日期"));

        map.put("inType", adapter.searchValueIdByKey("进场类型"));
        map.put("inTypeValue", adapter.searchValueStrByKey("进场类型"));

        map.put("inVariety", adapter.searchValueStrByKey("进场品种"));
        map.put("inVarietyId", adapter.searchValueIdByKey("进场品种"));

        map.put("inCount", adapter.searchValueStrByKey("进场数量"));

        map.put("inWeight", adapter.searchValueStrByKey("进场重量"));

        map.put("inAge", adapter.searchValueStrByKey("进场日龄"));

        map.put("customerId", adapter.searchValueIdByKey("供应商"));
        map.put("customerName", adapter.searchValueStrByKey("供应商"));

        map.put("acquirerId", adapter.searchValueIdByKey("收购人"));
        map.put("acquirerName", adapter.searchValueStrByKey("收购人"));

        map.put("recipientId", adapter.searchValueIdByKey("接收人"));
        map.put("recipientName", adapter.searchValueStrByKey("接收人"));

        map.put("acquisitionsCount", adapter.searchValueStrByKey("收购数量"));

        map.put("acquisitionsWeight", adapter.searchValueStrByKey("收购重量"));

        map.put("acquisitionsPrice", adapter.searchValueStrByKey("收购单价"));

        map.put("remark", adapter.searchValueStrByKey("备注"));

        String photo2 = adapter.searchValueStrByKey("图片");
        map.put("pic", photo2);

        mUploadLiveData.setValue(map);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::postPigIn);
    }

    public void removeImage(CustomRecyclerAdapter adapter, InfoDetailsDemoBean mBean, int mSubPosition) {
        String[] split = mBean.value.valueStr.split(",");
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(split));
        strings.remove(mSubPosition);
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < strings.size(); j++) {
            sb.append(strings.get(j));
            if (j != strings.size() - 1) {
                sb.append(",");
            }
        }
        mBean.value.valueStr = sb.toString();
        adapter.notifyDataSetChanged();
    }

    public void setClickBean(InfoDetailsClickBean clickBean) {
        mClickBean = clickBean;
    }

    public InfoDetailsClickBean getClickBean() {
        return mClickBean;
    }


    public int getCurrentPage() {
        return mPage;
    }

    public void loadEarCodeList(BreedInDetailsBean bean) {
        breedInDetailsBean = bean;
        earCodeByBatch = bean.breedInBatch;
        mEarCodeListLiveData.setValue(earCodeByBatch);
    }

    public LiveData<Resource<List<BreedInAssociateBean>>> getEarCodeListLiveData() {
        return Transformations.switchMap(mEarCodeListLiveData, earCodeByBatch -> model.getEarCodeByBatchList(earCodeByBatch,mPage));
    }
    public LiveData<String> getEarCodeListTotalLiveData() {
        return model.getEarCodeByBatchTotal();
    }

    public void onLoadMore() {
        if (mList.size() != CustomRecyclerAdapter.ITEM_PAGE_SIZE * mPage) {
            ToastUtils.showToast("没有更多了");
            return;
        }
        mPage++;
        mEarCodeListLiveData.setValue(earCodeByBatch);
    }

    public void refreshList() {
        mPage = 1;
        mEarCodeListLiveData.setValue(earCodeByBatch);
    }

    public void addEarCodeAssociation(String earCode) {
        mAddEarCodeListLiveData.setValue(earCode);
    }

    public LiveData<Resource<String>> getAddEarCodeAssociationLiveData() {
        return Transformations.switchMap(mAddEarCodeListLiveData, earCode -> model.postAddEarCodeAssociation(earCode,breedInDetailsBean.breedInBatch));
    }

    private String batchIn;

    public String getBatchIn() {
        return batchIn;
    }
    public void setBatchIn(String batchIn) {
        this.batchIn = batchIn;
    }

    private String scanCode;

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    public void removeEarCodeAssociation() {
        List<String> removeList = new ArrayList<>();
        removeList.add(getScanCode());
        mRemoveEarCodeListLiveData.setValue(removeList);
    }

    public LiveData<Resource<String>> getRemoveEarCodeAssociationLiveData() {
        return Transformations.switchMap(mRemoveEarCodeListLiveData, input -> model.postRemoveEarCodeAssociation(input,getBatchIn()));
    }

}
