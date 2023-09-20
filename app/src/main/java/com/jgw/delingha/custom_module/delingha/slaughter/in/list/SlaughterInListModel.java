package com.jgw.delingha.custom_module.delingha.slaughter.in.list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.SlaughterInDetailsBean;
import com.jgw.delingha.bean.SlaughterInListBean;
import com.jgw.delingha.custom_module.delingha.image.ImageUploadModel;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class SlaughterInListModel {

    private final ImageUploadModel uploadModel;

    public SlaughterInListModel() {
        uploadModel = new ImageUploadModel();
    }

    private ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> mOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<SlaughterInDetailsBean>> mSlaughterInDetailsLiveData;
    private ValueKeeperLiveData<Resource<String>> mDeleteSlaughterInLiveData;
    private ValueKeeperLiveData<Resource<String>> mGetUpdateImageLiveData;

    public LiveData<Resource<List<? extends SelectItemSupport>>> getSlaughterInList(String search, int page) {
        if (mOrderStockOutListLiveData == null) {
            mOrderStockOutListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getSlaughterInList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> {
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(SlaughterInListBean.class);
                })
                .subscribe(new CustomObserver<List<? extends SelectItemSupport>>(mOrderStockOutListLiveData) {});
        return mOrderStockOutListLiveData;
    }

    public LiveData<Resource<SlaughterInDetailsBean>> getSlaughterInDetails(String breedInRecId) {
        if (mSlaughterInDetailsLiveData == null) {
            mSlaughterInDetailsLiveData = new ValueKeeperLiveData<>();
        }
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getSlaughterInDetails(breedInRecId)
                .compose(HttpUtils.applyIOSchedulers())
                .subscribe(new CustomObserver<SlaughterInDetailsBean>(mSlaughterInDetailsLiveData) {});
        return mSlaughterInDetailsLiveData;
    }

    public LiveData<Resource<String>> deleteSlaughterIn(String inFactoryId) {
        if (mDeleteSlaughterInLiveData == null) {
            mDeleteSlaughterInLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("inFactoryId",inFactoryId);
        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .deleteSlaughterIn(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mDeleteSlaughterInLiveData) {});
        return mDeleteSlaughterInLiveData;
    }

    public LiveData<Resource<String>> getUpdateImage(String inFactoryId, String tempImageUrl, String certificateImageUrl) {
        if (mGetUpdateImageLiveData == null) {
            mGetUpdateImageLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("inFactoryId",inFactoryId);
        map.put("certificateImageUrl",tempImageUrl);

        Observable<String> uploadObservable;
        uploadObservable = uploadModel.getImagesStr(tempImageUrl)
                .flatMap((Function<String, ObservableSource<String>>) s -> {
                    if (TextUtils.isEmpty(certificateImageUrl)) {
                        map.put("certificateImageUrl", s);
                    }else {
                        String[] split = certificateImageUrl.split(",");
                        ArrayList<String> strings = new ArrayList<>(Arrays.asList(split));

                        MMKVUtils.save(ConstantUtil.SAVA_TEMP_IMAGE,s);

                        strings.add(s);
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < strings.size(); j++) {
                            sb.append(strings.get(j));
                            if (j != strings.size() - 1) {
                                sb.append(",");
                            }
                        }
                        map.put("certificateImageUrl", sb.toString());
                    }

                    return getUploadObservable(map);
                });
        uploadObservable.subscribe(new CustomObserver<String>(mGetUpdateImageLiveData) {});
        return mGetUpdateImageLiveData;
    }

    private static Observable<String> getUploadObservable(Map<String, Object> map) {
        return HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .getUpdateImageSlaughterIn(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers());
    }

}
