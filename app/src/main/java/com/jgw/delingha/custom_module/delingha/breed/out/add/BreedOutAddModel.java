package com.jgw.delingha.custom_module.delingha.breed.out.add;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedInListBean;
import com.jgw.delingha.bean.BreedOutDetailsBean;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 养殖离场 新增记录 Model
 */
public class BreedOutAddModel {

    private final ImageUploadModel uploadModel;

    public BreedOutAddModel() {
        uploadModel = new ImageUploadModel();
    }
    private ValueKeeperLiveData<Resource<List<InfoDetailsDemoBean>>> mListLiveData;

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getListData(BreedOutDetailsBean data) {
        if (mListLiveData == null) {
            mListLiveData = new ValueKeeperLiveData<>();
        }
        Observable<List<InfoDetailsDemoBean>> observable;
        observable = Observable.just("whatever")
                .map(s -> initInputListData());
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<List<InfoDetailsDemoBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mListLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<InfoDetailsDemoBean> list) {
                        mListLiveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mListLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
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
        bean.key = "离场栏舍";
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
        bean.key = "离场日期";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(FormatUtils.formatDate(new Date(), FormatUtils.DAY_TIME_PATTERN), null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "离场类型";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean("屠宰", "8b16657599cc406f9c768624873f48f3");
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "离场数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_required);
        bean.required = true;
        bean.maxLength = 6;
        bean.inputType = 1;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "离场重量";
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
        bean.itemType = 5;
        bean.key = "离场单价";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.integerLength = 4;
        bean.doubleLength = 2;
        bean.required = false;
        bean.isDouble = true;
        bean.inputType = 2;
        bean.unit = "元/kg";
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "去向";
        bean.hintText = ResourcesUtils.getString(R.string.select_normal);
        bean.value = new InfoDetailsDemoBean.ValueBean();
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

    private ValueKeeperLiveData<Resource<String>> mNewPigOutLiveData;

    public LiveData<Resource<String>> postPigOut(Map<String, Object> map) {
        if (mNewPigOutLiveData == null) {
            mNewPigOutLiveData = new ValueKeeperLiveData<>();
        }
        String operatorPic = (String) map.get("pic");
        Observable<String> uploadObservable;
        if (TextUtils.isEmpty(operatorPic)) {
            uploadObservable = getUploadObservable(map);

        } else {
            uploadObservable = uploadModel.getImagesStr(operatorPic)
                    .flatMap((Function<String, ObservableSource<String>>) s -> {
                        map.put("pic", s);
                        return getUploadObservable(map);
                    });
        }
        uploadObservable.subscribe(new CustomObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                mNewPigOutLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
            }

            @Override
            public void onNext(String list) {
                mNewPigOutLiveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mNewPigOutLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
            }

        });
        return mNewPigOutLiveData;
    }

    private static Observable<String> getUploadObservable(Map<String, Object> map) {
        return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postBreedOut(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
    }

    private ValueKeeperLiveData<Resource<BreedInAssociateBean>> mGetBatchByCodeLiveData;

    public LiveData<Resource<BreedInAssociateBean>> getBatchByCode(String mCode, String pigstyId) {
        if (mGetBatchByCodeLiveData == null) {
            mGetBatchByCodeLiveData = new ValueKeeperLiveData<>();
        }

        HashMap<String, Object> map = new HashMap<>();

        map.put("identification", mCode);
        map.put("fenceId", pigstyId);

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getBatchByCodeAndFence(map)
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
}
