package com.jgw.delingha.custom_module.delingha.slaughter.in.details;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.custom_module.delingha.image.ImageUploadModel;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 肉猪屠宰进场Model
 */
public class SlaughterInDetailsModel {

    private final ImageUploadModel uploadModel;

    public SlaughterInDetailsModel() {
        uploadModel = new ImageUploadModel();
    }

    private ValueKeeperLiveData<Resource<List<InfoDetailsDemoBean>>> mListLiveData;

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getListData(SlaughterInDetailsBean data) {
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
                .subscribe(new CustomObserver<List<InfoDetailsDemoBean>>(mListLiveData) {});
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
        bean.key = "离场批次";
        bean.hintText = ResourcesUtils.getString(R.string.breed_in_batch_scan_tips);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场日期";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(FormatUtils.formatDate(new Date(), FormatUtils.DAY_TIME_PATTERN), null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "进场数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_required);
        bean.required = true;
        bean.maxLength = 6;
        bean.inputType = 1;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "进场重量";
        bean.hintText = ResourcesUtils.getString(R.string.input_required);
        bean.required = true;
        bean.integerLength = 6;
        bean.doubleLength = 2;
        bean.isDouble = true;
        bean.inputType = 2;
        bean.unit = "kg";
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "动检合格证编号";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.maxLength = 50;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 4;
        bean.key = "动检合格证图片";
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 3;
        bean.key = "备注";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.maxLength = 500;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);


        bean = new InfoDetailsDemoBean();
        bean.itemType = 4;
        bean.key = "图片";
        list.add(bean);

        return list;
    }

    private List<InfoDetailsDemoBean> initDetailsListData(SlaughterInDetailsBean s) {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "屠宰批次";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inBatch, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "离场批次";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.breedLeaveBatch,null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场日期";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inTime, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "进场数量";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inCount, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "进场重量";
        bean.required = true;
        bean.unit = "kg";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inWeight, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 4;
        bean.key = "动检合格证图片";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.certificateImageUrl, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 3;
        bean.key = "备注";
        bean.maxLength = 500;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.remarks, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 4;
        bean.key = "图片";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.operatorPic, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "创建人";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.operatorName, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "创建时间";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.createTime, null);
        list.add(bean);

        for (InfoDetailsDemoBean i : list) {
            i.enable = false;
        }
        return list;
    }

    private ValueKeeperLiveData<Resource<BreedInAssociateBean>> mGetBatchByCodeLiveData;


    public LiveData<Resource<BreedInAssociateBean>> getBatchByCode(String mCode) {
        if (mGetBatchByCodeLiveData == null) {
            mGetBatchByCodeLiveData = new ValueKeeperLiveData<>();
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("identification", mCode);

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBatchByCode(map)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<BreedInAssociateBean>(mGetBatchByCodeLiveData) {});
        return mGetBatchByCodeLiveData;
    }

    private ValueKeeperLiveData<Resource<String>> mNewPigInLiveData;

    public LiveData<Resource<String>> postPigIn(Map<String, Object> map) {
        if (mNewPigInLiveData == null) {
            mNewPigInLiveData = new ValueKeeperLiveData<>();
        }
        String operatorPic = (String) map.get("operatorPic");
        Observable<String> uploadObservable;
        if (TextUtils.isEmpty(operatorPic)) {
            uploadObservable = getCertificateImage(map);
        } else {
            uploadObservable = uploadModel.getImagesStr(operatorPic)
                    .flatMap((Function<String, ObservableSource<String>>) s -> {
                        map.put("operatorPic", s);
                        return getCertificateImage(map);
                    });
        }
        uploadObservable.subscribe(new CustomObserver<String>(mNewPigInLiveData) {});
        return mNewPigInLiveData;
    }

    private Observable<String> getCertificateImage(Map<String, Object> map) {

        String operatorPic = (String) map.get("certificateImageUrl");
        Observable<String> uploadObservable;
        if (TextUtils.isEmpty(operatorPic)) {
            uploadObservable = getUploadObservable(map);
        } else {
            uploadObservable = uploadModel.getImagesStr(operatorPic)
                    .flatMap((Function<String, ObservableSource<String>>) s -> {
                        map.put("certificateImageUrl", s);
                        return getUploadObservable(map);
                    });
        }
        return uploadObservable;
    }

    private Observable<String> getUploadObservable(Map<String, Object> map) {
        Observable<String> uploadObservable;

        uploadObservable = HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postSlaughterIn(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
        return uploadObservable;
    }
}
