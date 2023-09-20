package com.jgw.delingha.module.select_list.organization_list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.OrganizationBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiService;

import java.util.HashMap;
import java.util.List;

/**
 * author : Cxz
 * data : 2019/11/20
 * description : 物流公司列表Model
 */
public class OrganizationListModel {
    public LiveData<Resource<List<OrganizationBean.ListBean>>> getOrganizationList(String searchStr, int currentPage) {
        ValueKeeperLiveData<Resource<List<OrganizationBean.ListBean>>> liveData = new ValueKeeperLiveData<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        map.put("current", currentPage);
        //是否测试组织 0非测试组织  1测试组织
//        map.put("orgUsageType", 0);
        if (!TextUtils.isEmpty(searchStr)) {
            map.put("search", searchStr);
        }
        HttpUtils.getGatewayApi(ApiService.class)
                .getOrganizationListWithoutToken(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrganizationBean>() {
                    @Override
                    public void onNext(OrganizationBean organizationBean) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, organizationBean.list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return liveData;
    }
}
