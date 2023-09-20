package com.jgw.delingha.custom_module.delingha.slaughter_weighing.details;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.SlaughterWeighingDetailsListBean;
import com.jgw.delingha.custom_module.delingha.image.ImageUploadModel;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 屠宰管理 宰后称重 Model
 */
public class SlaughterWeighingDetailsModel {

    private final ImageUploadModel uploadModel;

    public SlaughterWeighingDetailsModel() {
        uploadModel = new ImageUploadModel();
    }

    private ValueKeeperLiveData<Resource<List<InfoDetailsDemoBean>>> mListLiveData;

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getListData(SlaughterWeighingDetailsListBean data) {
        if (mListLiveData == null) {
            mListLiveData = new ValueKeeperLiveData<>();
        }
        Observable<List<InfoDetailsDemoBean>> observable;
        if (data == null) {
            observable = Observable.just("whatever")
                    .map(s -> initInputListData());
        } else {
            observable = Observable.just(data)
                    .map(this::initDetailsListData);
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<InfoDetailsDemoBean>>(mListLiveData) {
                });
        return mListLiveData;
    }

    /**
     * 构造出数据源展示对应的条目类型和数据
     * 界面录入信息由DataBinding驱动直接映射到数据源
     * itemType类型
     * 0 文本输入条目 不可编辑
     * 1 文本输入条目 可编辑
     * 2 文本选择条目 选择设置 不可输入
     * 3 备注条目     多行输入
     * 4 照片选择
     * 5 文本输入条目 可编辑 带单位
     *
     * @return 构造出的数据集合
     */
    private List<InfoDetailsDemoBean> initInputListData() {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "屠宰批次";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "屠宰数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.maxLength = 6;
        bean.inputType = 1;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "重量1";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.integerLength = 4;
        bean.doubleLength = 2;
        bean.isDouble = true;
        bean.required = true;
        bean.inputType = 2;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);
        return list;
    }

    public List<InfoDetailsDemoBean> getWeights(int slaughterCount, boolean enable) {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();
        for (int i = 0; i < slaughterCount; i++) {
            bean = new InfoDetailsDemoBean();
            bean.itemType = 1;
            bean.key = "重量" + (i + 2);
            bean.hintText = ResourcesUtils.getString(R.string.input_normal);
            bean.integerLength = 4;
            bean.doubleLength = 2;
            bean.isDouble = true;
            bean.inputType = 2;
            bean.checkNumber = true;
            bean.enable = enable;
            bean.value = new InfoDetailsDemoBean.ValueBean();
            list.add(bean);
        }
        return list;
    }

    private List<InfoDetailsDemoBean> initDetailsListData(SlaughterWeighingDetailsListBean s) {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "屠宰批次";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.getBatchName(), null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "屠宰数量";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.getButcherCount(), null);
        list.add(bean);

        List<String> remoteWeights = s.getWeights();
        if (remoteWeights != null && !remoteWeights.isEmpty()) {
            for (int i = 0; i < remoteWeights.size(); i++) {
                bean = new InfoDetailsDemoBean();
                bean.itemType = 1;
                bean.key = "重量"+(i+1);
                bean.integerLength = 4;
                bean.doubleLength = 2;
                bean.isDouble = true;
                bean.required = i == 0;
                bean.inputType = 2;
                bean.checkNumber = true;
                bean.value = new InfoDetailsDemoBean.ValueBean(remoteWeights.get(i), null);
                list.add(bean);
            }
        }

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "创建人";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.getOperatorName(), null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "创建时间";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.getCreateTime(), null);
        list.add(bean);

        for (InfoDetailsDemoBean i : list) {
            i.enable = false;
        }
        return list;
    }

    private ValueKeeperLiveData<Resource<String>> mGetUploadWeighingLiveData;

    public LiveData<Resource<String>> getUpload(Map<String, Object> map) {
        if (mGetUploadWeighingLiveData == null) {
            mGetUploadWeighingLiveData = new ValueKeeperLiveData<>();
        }

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postSlaughterWeighing(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mGetUploadWeighingLiveData) {
                });
        return mGetUploadWeighingLiveData;
    }

}
