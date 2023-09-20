package com.jgw.delingha.custom_module.delingha.daily_management.exchange_pigsty.details;

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
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.bean.ExchangePigstyDetailsBean;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.custom_module.delingha.breed.out.add.BreedOutAddModel;
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
 * @Description 肉猪屠宰进场ViewModel
 */
public class ExchangePigstyDetailsViewModel extends BaseViewModel {
    private final ExchangePigstyDetailsModel model;
    private final BreedOutAddModel mBreedInModel;

    private final MutableLiveData<ExchangePigstyDetailsBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetBreedInDataLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetBatchByCodeLiveData = new ValueKeeperLiveData<>();

    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;
    private String pigstyId;

    public ExchangePigstyDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new ExchangePigstyDetailsModel();
        mBreedInModel = new BreedOutAddModel();
    }

    //从model获取数据
    public void loadList(ExchangePigstyDetailsBean data) {
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
        map.put("outRankName", adapter.searchValueStrByKey("转出栏舍"));
        map.put("outRankId", adapter.searchValueIdByKey("转出栏舍"));

        map.put("breedBatch", adapter.searchValueStrByKey("养殖批次"));
        map.put("breedBatchId", adapter.searchValueIdByKey("养殖批次"));

        map.put("inMassName", adapter.searchValueStrByKey("转入栏舍"));
        map.put("inMassId", adapter.searchValueIdByKey("转入栏舍"));

        map.put("transferNumber", adapter.searchValueStrByKey("转栏数量"));

        map.put("weightTotal", adapter.searchValueStrByKey("转栏重量"));

        map.put("remarks", adapter.searchValueStrByKey("备注"));

        String photo2 = adapter.searchValueStrByKey("图片");
        map.put("operationPic", photo2);
        mUploadLiveData.setValue(map);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::postUpload);
    }

    public void removeImage(CustomRecyclerAdapter<?> adapter, InfoDetailsDemoBean mBean, int mSubPosition) {
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

    public void setPigstyId(String pigstyId) {
        this.pigstyId = pigstyId;
    }

    public String getPigstyId() {
        return pigstyId;
    }

    public void getBreedInData() {
        mGetBreedInDataLiveData.setValue(getPigstyId());
    }

    public LiveData<Resource<BreedInListBean>> getBreedInDataLiveData() {
        return Transformations.switchMap(mGetBreedInDataLiveData, mBreedInModel::getBreedInDataByPigstyId);
    }


    public void getBatchByCode(String mCode) {
        if (TextUtils.isEmpty(getPigstyId())) {
            ToastUtils.showToast("请先选择转出栏舍");
            return;
        }
        mGetBatchByCodeLiveData.setValue(mCode);
    }

    public LiveData<Resource<BreedInAssociateBean>> getBatchByCodeLiveData() {
        return Transformations.switchMap(mGetBatchByCodeLiveData, input -> mBreedInModel.getBatchByCode(input, getPigstyId()));
    }


}
