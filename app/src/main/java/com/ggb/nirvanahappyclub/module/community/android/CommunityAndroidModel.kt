package com.ggb.nirvanahappyclub.module.community.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.common_library.utils.json.JsonUtils
import com.ggb.nirvanahappyclub.bean.CommunityAndroidBean
import com.ggb.nirvanahappyclub.common.AppConfig
import com.ggb.nirvanahappyclub.network.HttpUtils
import com.ggb.nirvanahappyclub.network.WanAndroidHttpUtils
import com.ggb.nirvanahappyclub.network.api.ApiService
import io.reactivex.disposables.Disposable

class CommunityAndroidModel {

    private val getCommunityTitleLiveData: MutableLiveData<Resource<List<CommunityAndroidBean.CommunityAndroidListBean>>> = ValueKeeperLiveData()

    fun getCommunityAndroidList(pager: Int): LiveData<Resource<List<CommunityAndroidBean.CommunityAndroidListBean>>> {

        HttpUtils.getWanAndroidGatewayApi(ApiService::class.java)
            .getCommunityAndroid(pager,AppConfig.ITEM_PAGE_SIZE)
            .compose(WanAndroidHttpUtils.applyMainSchedulers())
            .map {
                JsonUtils.parseObject(it)?.getJsonArray("datas")?.toJavaList(CommunityAndroidBean.CommunityAndroidListBean::class.java)
            }
            .subscribe(object : CustomObserver<List<CommunityAndroidBean.CommunityAndroidListBean>>() {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    getCommunityTitleLiveData.value = Resource(Resource.LOADING, null, "")
                }

                override fun onNext(dataList: List<CommunityAndroidBean.CommunityAndroidListBean>) {
                    getCommunityTitleLiveData.value = Resource(Resource.SUCCESS, dataList, "")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    getCommunityTitleLiveData.value = Resource(Resource.ERROR, null, e.message)
                }
            })
        return getCommunityTitleLiveData
    }
}