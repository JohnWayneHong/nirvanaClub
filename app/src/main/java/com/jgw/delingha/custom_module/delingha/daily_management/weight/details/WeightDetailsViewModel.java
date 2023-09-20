package com.jgw.delingha.custom_module.delingha.daily_management.weight.details;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.WeightTypeBean;
import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.options_details.utils.OptionsDetailsUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 肉猪屠宰进场ViewModel
 */
public class WeightDetailsViewModel extends BaseViewModel {
    private final WeightDetailsModel model;

    private final MutableLiveData<WeightTypeBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    public WeightDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new WeightDetailsModel();
    }

    //从model获取数据
    public void loadList(WeightTypeBean data) {
        mContentListLiveData.setValue(data);
    }

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mContentListLiveData, model::getListData);
    }

//    public void selectImage(CustomRecyclerAdapter adapter, InfoDetailsDemoBean bean, @NonNull Uri uri) {
//        File file = BitmapUtils.getRealPathFromUri(CustomApplication.getCustomApplicationContext(), uri);
//        BitmapUtils.getBitmap(file, new OnCompressListener() {
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onSuccess(File file) {
//                long length1 = file.length();
//                LogUtils.xswShowLog("");
//                if (length1 > 0) {
//                    Uri uri = Uri.fromFile(file);
//                    int i = adapter.getDataList().indexOf(bean);
//                    if (i != -1) {
//                        if (bean.value == null) {
//                            bean.value = new InfoDetailsDemoBean.ValueBean("", "");
//                        }
//                        if (TextUtils.isEmpty(bean.value.valueStr)) {
//                            bean.value.valueStr = uri.toString();
//                        } else {
//                            String[] split = bean.value.valueStr.split(",");
//                            ArrayList<String> strings = new ArrayList<>(Arrays.asList(split));
//                            strings.add(uri.toString());
//                            StringBuilder sb = new StringBuilder();
//                            for (int j = 0; j < strings.size(); j++) {
//                                sb.append(strings.get(j));
//                                if (j != strings.size() - 1) {
//                                    sb.append(",");
//                                }
//                            }
//                            bean.value.valueStr = sb.toString();
//                        }
//                        adapter.notifyItemChanged(i);
//                    }
//                } else {
//                    ToastUtils.showToast("图片损坏,请重新拍摄");
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                ToastUtils.showToast("未知错误");
//            }
//        });
//
//
//    }


    public void submit(InfoDetailsRecyclerAdapter adapter) {
        List<InfoDetailsDemoBean> dataList = adapter.getDataList();
        if (OptionsDetailsUtils.checkItem(dataList)){
            return;
        }
        HashMap<String, Object> map = new HashMap<>();

        String weightTypeId = adapter.searchValueIdByKey("称重类型");
        String weightTypeStr = adapter.searchValueStrByKey("称重类型");

        if (weightTypeStr.equals("按栏抽查")) {
            map.put("rankName", adapter.searchValueStrByKey("栏舍号"));
            map.put("rankId", adapter.searchValueIdByKey("栏舍号"));
        }

        map.put("operationType", weightTypeId);
        map.put("animalsValue", adapter.searchValueStrByKey("称重对象"));
        map.put("animalsId", adapter.searchValueIdByKey("称重对象"));
        map.put("weightNumber", adapter.searchValueStrByKey("称重数量"));
        map.put("weightTotal", adapter.searchValueStrByKey("称重总重"));
        mUploadLiveData.setValue(map);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::postUpload);
    }

//    public void removeImage(CustomRecyclerAdapter adapter, InfoDetailsDemoBean mBean, int mSubPosition) {
//        String[] split = mBean.value.valueStr.split(",");
//        ArrayList<String> strings = new ArrayList<>(Arrays.asList(split));
//        strings.remove(mSubPosition);
//        StringBuilder sb = new StringBuilder();
//        for (int j = 0; j < strings.size(); j++) {
//            sb.append(strings.get(j));
//            if (j != strings.size() - 1) {
//                sb.append(",");
//            }
//        }
//        mBean.value.valueStr = sb.toString();
//        adapter.notifyDataSetChanged();
//    }

    public void setClickBean(InfoDetailsClickBean clickBean) {
        mClickBean = clickBean;
    }

    public InfoDetailsClickBean getClickBean() {
        return mClickBean;
    }
}
