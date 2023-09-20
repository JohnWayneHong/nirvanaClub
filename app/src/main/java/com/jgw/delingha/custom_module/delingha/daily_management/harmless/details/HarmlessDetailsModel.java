package com.jgw.delingha.custom_module.delingha.daily_management.harmless.details;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.bean.FeedingListBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 无害化记录 详情 Model
 */
public class HarmlessDetailsModel {

    private final ImageUploadModel uploadModel;

    public HarmlessDetailsModel() {
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
        bean.key = "养殖批次";
        bean.hintText = ResourcesUtils.getString(R.string.breed_in_batch_scan_tips);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "处理日期";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(FormatUtils.formatDate(new Date(), FormatUtils.DAY_TIME_PATTERN), null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "处理对象";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "处理数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_required);
        bean.inputType = 1;
        bean.maxLength = 6;
        bean.required = true;
        bean.checkNumber = true;
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

    private ValueKeeperLiveData<Resource<BreedInListBean>> mGetBreedInByPigstyLiveData;
    public LiveData<Resource<BreedInListBean>> getBreedInDataByPigstyId(String selectByPigstyId) {
        if (mGetBreedInByPigstyLiveData == null) {
            mGetBreedInByPigstyLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();

        map.put("current", 1);
        map.put("pageSize", 9999);
        map.put("fenceId", selectByPigstyId);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBreedInBatchByFenceList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(BreedInListBean.class);
                })
                .filter(breedInListBeans -> breedInListBeans.size()==1)
                .map(breedInListBeans -> breedInListBeans.get(0))
                .subscribe(new CustomObserver<BreedInListBean>(mGetBreedInByPigstyLiveData) {});
        return mGetBreedInByPigstyLiveData;
    }

    private ValueKeeperLiveData<Resource<String>> mHarmlessLiveData;

    public LiveData<Resource<String>> postUpload(Map<String, Object> map) {
        if (mHarmlessLiveData == null) {
            mHarmlessLiveData = new ValueKeeperLiveData<>();
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
        uploadObservable.subscribe(new CustomObserver<String>(mHarmlessLiveData) {
        });
        return mHarmlessLiveData;
    }

    private Observable<String> getUploadObservable(Map<String, Object> map) {
        return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postHarmless(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
    }
}
