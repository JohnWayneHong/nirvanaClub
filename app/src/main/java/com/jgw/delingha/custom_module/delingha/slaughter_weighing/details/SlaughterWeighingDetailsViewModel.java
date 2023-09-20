package com.jgw.delingha.custom_module.delingha.slaughter_weighing.details;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;


import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;

import com.jgw.common_library.utils.MathUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.bean.InfoDetailsClickBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.SlaughterWeighingDetailsListBean;

import com.jgw.delingha.module.options_details.adapter.InfoDetailsRecyclerAdapter;
import com.jgw.delingha.module.options_details.utils.OptionsDetailsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 屠宰管理 宰后称重 ViewModel
 */
public class SlaughterWeighingDetailsViewModel extends BaseViewModel {
    private final SlaughterWeighingDetailsModel model;
    private final MutableLiveData<SlaughterWeighingDetailsListBean> mContentListLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Map<String, Object>> mUploadLiveData = new ValueKeeperLiveData<>();
    private InfoDetailsClickBean mClickBean;

    private int slaughterCount = 0;

    public SlaughterWeighingDetailsViewModel(@NonNull Application application) {
        super(application);
        model = new SlaughterWeighingDetailsModel();
    }

    //从model获取数据
    public void loadList(SlaughterWeighingDetailsListBean data) {
        mContentListLiveData.setValue(data);
    }

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getLoadListLiveData() {
        return Transformations.switchMap(mContentListLiveData, model::getListData);
    }


    public void submit(InfoDetailsRecyclerAdapter adapter) {
        List<InfoDetailsDemoBean> dataList = adapter.getDataList();
        if (OptionsDetailsUtils.checkItem(dataList)){
            return;
        }

        List<String> weighList = new ArrayList<>();
        double totalWeight = 0;

        for (int i = 0; i < getCount(); i++) {
            String tempWeight = adapter.searchValueStrByKey("重量"+(i+1));
            if (!TextUtils.isEmpty(tempWeight)) {
                weighList.add(tempWeight);
                totalWeight = MathUtils.add(Double.parseDouble(tempWeight),totalWeight);
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("batchName", adapter.searchValueStrByKey("屠宰批次"));

        map.put("totalWeight", totalWeight);

        map.put("weights", weighList);

        mUploadLiveData.setValue(map);
    }

    public LiveData<Resource<String>> getUploadLiveData() {
        return Transformations.switchMap(mUploadLiveData, model::getUpload);
    }

    public void setClickBean(InfoDetailsClickBean clickBean) {
        mClickBean = clickBean;
    }

    public InfoDetailsClickBean getClickBean() {
        return mClickBean;
    }

    public void setCount(int butcherCount) {
        slaughterCount=butcherCount;
    }

    public int getCount(){
        return slaughterCount;
    }

    public List<InfoDetailsDemoBean> getItem(boolean enable) {
       return model.getWeights(getCount()-1, enable);
    }
}
