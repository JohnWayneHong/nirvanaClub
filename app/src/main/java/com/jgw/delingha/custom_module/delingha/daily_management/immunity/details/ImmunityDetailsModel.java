package com.jgw.delingha.custom_module.delingha.daily_management.immunity.details;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FeedingListBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.custom_module.delingha.image.ImageUploadModel;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 免疫记录 详情 Model
 */
public class ImmunityDetailsModel {

    private final ImageUploadModel uploadModel;

    public ImmunityDetailsModel() {
        uploadModel = new ImageUploadModel();
    }

    private ValueKeeperLiveData<Resource<List<InfoDetailsDemoBean>>> mListLiveData;

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getListData(FeedingListBean data) {
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
        bean.key = "栏舍号";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "免疫对象";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "免疫日期";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(FormatUtils.formatDate(new Date(), FormatUtils.DAY_TIME_PATTERN), null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "免疫数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_required);
        bean.inputType = 1;
        bean.maxLength = 6;
        bean.required = true;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "类型";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 3;
        bean.key = "备注";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.maxLength = 300;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        return list;
    }

    private List<InfoDetailsDemoBean> initDetailsListData(FeedingListBean s) {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();
//
//        bean = new InfoDetailsDemoBean();
//        bean.itemType = 2;
//        bean.key = "屠宰批次";
//        bean.required = true;
//        bean.value = new InfoDetailsDemoBean.ValueBean(s.inBatch, null);
//        list.add(bean);
//

        for (InfoDetailsDemoBean i : list) {
            i.enable = false;
        }
        return list;
    }


    private ValueKeeperLiveData<Resource<String>> mImmunityLiveData;

    public LiveData<Resource<String>> postUpload(Map<String, Object> map) {
        if (mImmunityLiveData == null) {
            mImmunityLiveData = new ValueKeeperLiveData<>();
        }
        String operatorPic = (String) map.get("operatorPic");
        Observable<String> uploadObservable;
        if (TextUtils.isEmpty(operatorPic)) {
            uploadObservable = getUploadObservable(map);

        } else {
            uploadObservable = uploadModel.getImagesStr(operatorPic)
                    .flatMap((Function<String, ObservableSource<String>>) s -> {
                        map.put("operatorPic", s);
                        return getUploadObservable(map);
                    });
        }
        uploadObservable.subscribe(new CustomObserver<String>(mImmunityLiveData) {
        });
        return mImmunityLiveData;
    }

    private Observable<String> getUploadObservable(Map<String, Object> map) {
        return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postImmunity(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
    }
}
