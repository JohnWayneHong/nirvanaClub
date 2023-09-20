package com.jgw.delingha.custom_module.delingha.breed.enter_fence.details;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.EnterFenceDetailsBean;
import com.jgw.delingha.bean.InfoDetailsDemoBean;
import com.jgw.delingha.custom_module.delingha.image.ImageUploadModel;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.ArrayList;
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
 * @Description 入栏记录 详情 || 添加 Model
 */
public class EnterFenceDetailsModel {

    private final ImageUploadModel uploadModel;

    public EnterFenceDetailsModel() {
        uploadModel = new ImageUploadModel();
    }

    private ValueKeeperLiveData<Resource<List<InfoDetailsDemoBean>>> mListLiveData;

    public LiveData<Resource<List<InfoDetailsDemoBean>>> getListData(EnterFenceDetailsBean data) {
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
        bean.itemType = 1;
        bean.key = "入栏数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_required);
        bean.required = true;
        bean.maxLength = 6;
        bean.inputType = 1;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "入栏重量";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.integerLength = 6;
        bean.doubleLength = 2;
        bean.isDouble = true;
        bean.inputType = 2;
        bean.unit = "kg";
        bean.checkNumber = true;
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

    private List<InfoDetailsDemoBean> initDetailsListData(EnterFenceDetailsBean s) {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "栏舍号";
        bean.required = true;
        bean.enable = false;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.fenceName, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "养殖批次";
        bean.required = true;
        bean.enable = false;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.breedInBatch, null);
        list.add(bean);


        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "入栏数量";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inFenceCount, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "入栏重量";
        bean.unit = "kg";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inFenceWeight, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 3;
        bean.key = "备注";
        bean.maxLength = 500;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.remark, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 4;
        bean.key = "图片";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.pic, null);
        list.add(bean);

        for (InfoDetailsDemoBean i : list) {
            i.enable = false;
        }
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
                .postEnterFence(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
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
}
