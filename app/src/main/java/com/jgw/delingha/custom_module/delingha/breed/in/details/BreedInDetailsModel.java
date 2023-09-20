package com.jgw.delingha.custom_module.delingha.breed.in.details;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.BreedInDetailsBean;
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
 * @Author CJM
 * @Date 2023/6/14 09:35
 * @Description 养殖进场 关联耳号 || 详情 Model
 */
public class BreedInDetailsModel {

    private final ImageUploadModel uploadModel;

    public BreedInDetailsModel() {
        uploadModel = new ImageUploadModel();
    }

    private ValueKeeperLiveData<Resource<List<InfoDetailsDemoBean>>> mListLiveData;

    private ValueKeeperLiveData<Resource<List<BreedInAssociateBean>>> mBreedInAssociateListLiveData;
    private ValueKeeperLiveData<String> mTotalListLiveData;

    private ValueKeeperLiveData<Resource<String>> mAddEarCodeAssociationLiveData;


    public LiveData<Resource<List<InfoDetailsDemoBean>>> getListData(BreedInDetailsBean data) {
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
        bean.key = "进场日期";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(FormatUtils.formatDate(new Date(),FormatUtils.DAY_TIME_PATTERN),null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场类型";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean("本场采购","774722cd9a616bc819db8efb9a886bfa");
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场品种";
        bean.hintText = ResourcesUtils.getString(R.string.select_required);
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
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
        bean.isDouble=true;
        bean.inputType = 2;
        bean.unit = "kg";
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "进场日龄";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.inputType = 1;
        bean.maxLength = 4;
        bean.unit = "日";
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "供应商";
        bean.hintText = ResourcesUtils.getString(R.string.select_normal);
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "收购人";
        bean.hintText = ResourcesUtils.getString(R.string.select_normal);
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "接收人";
        bean.hintText = ResourcesUtils.getString(R.string.select_normal);
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "收购数量";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.inputType = 1;
        bean.maxLength = 6;
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "收购重量";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.integerLength = 6;
        bean.doubleLength = 2;
        bean.inputType = 2;
        bean.isDouble=true;
        bean.unit = "kg";
        bean.checkNumber = true;
        bean.value = new InfoDetailsDemoBean.ValueBean();
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "收购单价";
        bean.hintText = ResourcesUtils.getString(R.string.input_normal);
        bean.integerLength = 4;
        bean.doubleLength = 2;
        bean.inputType = 2;
        bean.isDouble=true;
        bean.unit = "元/kg";
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

    private List<InfoDetailsDemoBean> initDetailsListData(BreedInDetailsBean s) {
        InfoDetailsDemoBean bean;
        ArrayList<InfoDetailsDemoBean> list = new ArrayList<>();

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "养殖批次";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.breedInBatch, s.breedInRecId);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场日期";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inDate, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场类型";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inTypeValue, s.inType);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "进场品种";
        bean.required = true;
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inVariety, null);
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
        bean.itemType = 5;
        bean.key = "进场日龄";
        bean.unit = "日";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.inAge, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "供应商";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.customerName, s.customerId);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "收购人";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.acquirerName, s.acquirerId);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 2;
        bean.key = "接收人";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.recipientName, s.recipientId);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 1;
        bean.key = "收购数量";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.acquisitionsCount, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "收购重量";
        bean.unit = "kg";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.acquisitionsWeight, null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 5;
        bean.key = "收购单价";
        bean.unit = "元/kg";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.acquisitionsPrice, null);
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

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "创建人";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.createrName,null);
        list.add(bean);

        bean = new InfoDetailsDemoBean();
        bean.itemType = 0;
        bean.key = "创建时间";
        bean.value = new InfoDetailsDemoBean.ValueBean(s.createTime,null);
        list.add(bean);

        for (InfoDetailsDemoBean i:list){
            i.enable=false;
        }
        return list;
    }


    private ValueKeeperLiveData<Resource<String>> mNewPigInLiveData;

    public LiveData<Resource<String>> postPigIn(Map<String, Object> map) {
        if (mNewPigInLiveData == null) {
            mNewPigInLiveData = new ValueKeeperLiveData<>();
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
        uploadObservable.subscribe(new CustomObserver<String>(mNewPigInLiveData) {});
        return mNewPigInLiveData;
    }

    private static Observable<String> getUploadObservable(Map<String, Object> map) {
        return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postBreedIn(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
    }

    public LiveData<Resource<List<BreedInAssociateBean>>> getEarCodeByBatchList(String batch, int mPage) {
        if (mBreedInAssociateListLiveData == null) {
            mBreedInAssociateListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();

        map.put("current", mPage);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        map.put("breedInBatch", batch);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getEarCodeByBatchList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    mTotalListLiveData.postValue(String.valueOf(JsonUtils.parseObject(s).getJsonObject("pagination").getInt("total")));
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(BreedInAssociateBean.class);
                })
                .subscribe(new CustomObserver<List<BreedInAssociateBean>>(mBreedInAssociateListLiveData) {});

        return mBreedInAssociateListLiveData;
    }
    public LiveData<String> getEarCodeByBatchTotal() {
        if (mTotalListLiveData == null) {
            mTotalListLiveData = new ValueKeeperLiveData<>();
        }
        return mTotalListLiveData;
    }

    public LiveData<Resource<String>> postAddEarCodeAssociation(String earCode,String breedOutRecId) {
        if (mAddEarCodeAssociationLiveData == null) {
            mAddEarCodeAssociationLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();

        list.add(earCode);

        map.put("batch", breedOutRecId);
        map.put("identityNumList", list);

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postAddEarCodeAssociation(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mAddEarCodeAssociationLiveData) {});
        return mAddEarCodeAssociationLiveData;
    }

    public LiveData<Resource<String>> postRemoveEarCodeAssociation(List<String> earList, String batchIn) {
        if (mAddEarCodeAssociationLiveData == null) {
            mAddEarCodeAssociationLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("batch", batchIn);
        map.put("identityNumList", earList);

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postRemoveEarCodeAssociation(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mAddEarCodeAssociationLiveData) {});
        return mAddEarCodeAssociationLiveData;
    }

}
