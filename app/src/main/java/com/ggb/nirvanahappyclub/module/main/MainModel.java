package com.ggb.nirvanahappyclub.module.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;

import java.util.Date;
import java.util.HashMap;

public class MainModel {

//    public LiveData<Resource<String>> getSystemExpireTime() {
//        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("orgId", MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID));
//        map.put("sysId",MMKVUtils.getString(ConstantUtil.SYSTEM_ID));
//        HttpUtils.getGatewayApi(ApiService.class)
//                .getSystemExpireTime(map)
//                .compose(HttpUtils.applyIOSchedulers())
//                .map(s -> {
//                    JsonObject jsonObject = JsonUtils.parseObject(s);
//                    String value = jsonObject.getString("value");
////                        String value = "2021-12-31 00:00:00";
//                    Date date = FormatUtils.decodeDate(value, "yyyy-MM-dd HH:mm:ss");
//                    MMKVUtils.save(ConstantUtil.SYSTEM_EXPIRE_TIME,date.getTime());
//                    return "获取成功";
//                })
//                .subscribe(new CustomObserver<String>() {
//                    @Override
//                    public void onNext(@NonNull String s) {
//                        liveData.postValue(new Resource<>(Resource.SUCCESS,s,""));
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//                });
//        return liveData;
//    }
}
