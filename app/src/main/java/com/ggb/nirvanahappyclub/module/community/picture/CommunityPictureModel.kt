package com.ggb.nirvanahappyclub.module.community.picture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.DevelopJokesListBean
import com.ggb.nirvanahappyclub.network.HttpUtils
import com.ggb.nirvanahappyclub.network.JokerAndroidHttpUtils
import com.ggb.nirvanahappyclub.network.api.ApiService
import io.reactivex.disposables.Disposable

class CommunityPictureModel {

    private val getCommunityPictureLiveData: MutableLiveData<Resource<List<DevelopJokesListBean>>> = ValueKeeperLiveData()

    fun getCommunityPictureListLiveData(tagId:String): LiveData<Resource<List<DevelopJokesListBean>>> {

//        val map = HashMap<String, Any>()
//        map["tagId"] = tagId
//        map["page"] = current-1
//        map["size"] = CustomRecyclerAdapter.ITEM_PAGE_SIZE
//
        HttpUtils.getJokerAndroidGatewayApi(ApiService::class.java)
            .communityPictureJokerAndroid
            .compose(JokerAndroidHttpUtils.applyMainSchedulers())
            .subscribe(object : CustomObserver<List<DevelopJokesListBean>>() {

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    getCommunityPictureLiveData.value = Resource(Resource.LOADING, null, "")
                }

                override fun onNext(s: List<DevelopJokesListBean>) {
                    getCommunityPictureLiveData.value = Resource(Resource.SUCCESS, s, "")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    getCommunityPictureLiveData.value = Resource(Resource.ERROR, null, e.message)
                }
            })
        return getCommunityPictureLiveData
    }
}