package com.jgw.delingha.custom_module.delingha.breed.ear_reset;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.BreedEarAssociateBean;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.EnterFenceDetailsBean;
import com.jgw.delingha.bean.EnterFenceListBean;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiDeLingHaService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2023-8-4 09:42:37
 * 养殖管理 耳号重置 Model
 */
public class EarResetListModel {
    private ValueKeeperLiveData<Resource<String>> mAddEarCodeAssociationLiveData;

    public LiveData<Resource<String>> postResetEarCodeAssociation(List<String> earList) {
        if (mAddEarCodeAssociationLiveData == null) {
            mAddEarCodeAssociationLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();

        map.put("identityNum", earList);

        HttpUtils.getGatewayApi(ApiDeLingHaService.class)
                .postResetEarCodeAssociation(map)
                .compose(HttpUtils.applyResultNullableIOSchedulers())
                .subscribe(new CustomObserver<String>(mAddEarCodeAssociationLiveData) {});
        return mAddEarCodeAssociationLiveData;
    }

}
