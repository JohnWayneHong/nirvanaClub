package com.jgw.delingha.custom_module.delingha.breed.out.details;

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
import com.jgw.delingha.bean.BreedEarAssociateBean;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedOutDetailsBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.common.model.ConfigInfoModel;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.options_details.utils.OptionsDetailsUtils;
import com.jgw.delingha.sql.entity.CustomerEntity;
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
 * on 2023-8-4 10:55:53
 * 养殖离场 耳号反扫 ViewModel
 */
public class BreedOutDetailsViewModel extends BaseViewModel {
    private final BreedOutDetailsModel model;

    private final MutableLiveData<BreedOutDetailsBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<String> mEarCodeListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mAddEarCodeListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mCustomerInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<List<String>> mRemoveEarCodeListLiveData = new ValueKeeperLiveData<>();

    private final ConfigInfoModel mConfigModel;

    private InfoDetailsClickBean mClickBean;

    private int mPage = 1;
    private List<BreedInAssociateBean> mList = new ArrayList<>();

    private String earCodeByBatch = "";
    private BreedOutDetailsBean breedOutDetailsBean;

    public BreedOutDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new BreedOutDetailsModel();
        mConfigModel = new ConfigInfoModel();
    }

    public void setDataList(List<BreedInAssociateBean> dataList) {
        mList = dataList;
    }

    //从model获取数据
    public void loadList(BreedOutDetailsBean data) {
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
        map.put("breedInBatch", adapter.searchValueStrByKey("养殖批次"));
        
        map.put("outDate", adapter.searchValueStrByKey("离场日期"));

        map.put("outType", adapter.searchValueIdByKey("离场类型"));
        map.put("outTypeValue", adapter.searchValueStrByKey("离场类型"));

        map.put("outCount", adapter.searchValueStrByKey("离场数量"));

        map.put("outWeight", adapter.searchValueStrByKey("离场重量"));

        map.put("outPrice", adapter.searchValueStrByKey("离场单价"));

        map.put("destinationId", adapter.searchValueIdByKey("去向"));
        map.put("destinationName", adapter.searchValueStrByKey("去向"));

        map.put("remark", adapter.searchValueStrByKey("备注"));

        String photo2 = adapter.searchValueStrByKey("图片");
        map.put("pic", photo2);

        mUploadLiveData.setValue(map);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::postPigOut);
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

    public void getCustomerInfo(long id) {
        mCustomerInfoLiveData.setValue(id);
    }

    public LiveData<Resource<CustomerEntity>> getCustomerInfoLiveData() {
        return Transformations.switchMap(mCustomerInfoLiveData, mConfigModel::getCustomerInfo);
    }

    public int getCurrentPage() {
        return mPage;
    }

    public void loadEarCodeList(BreedOutDetailsBean bean) {
        breedOutDetailsBean = bean;
        earCodeByBatch = breedOutDetailsBean.breedOutBatch;
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
        return Transformations.switchMap(mAddEarCodeListLiveData, earCode -> model.postAddEarCodeAssociation(earCode,breedOutDetailsBean.breedOutBatch));
    }

    private String batchOut;

    public String getBatchOut() {
        return batchOut;
    }

    public void setBatchOut(String batchOut) {
        this.batchOut = batchOut;
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
        return Transformations.switchMap(mRemoveEarCodeListLiveData, input -> model.postRemoveEarCodeAssociation(input,getBatchOut()));
    }

}
