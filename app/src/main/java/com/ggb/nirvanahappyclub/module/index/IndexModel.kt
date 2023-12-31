package com.ggb.nirvanahappyclub.module.index

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.IndexTagBean
import com.ggb.nirvanahappyclub.network.HttpUtils
import com.ggb.nirvanahappyclub.network.api.ApiService
import io.reactivex.disposables.Disposable

class IndexModel {

    private val indexTagLiveData: MutableLiveData<Resource<List<IndexTagBean>>> = ValueKeeperLiveData()

    fun getIndexTagLiveData(xx:String): LiveData<Resource<List<IndexTagBean>>> {
        HttpUtils.getGatewayApi(ApiService::class.java)
            .searchIndexTag()
            .compose(HttpUtils.applyMainSchedulers())
            .subscribe(object : CustomObserver<List<IndexTagBean>>() {

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    indexTagLiveData.value = Resource(Resource.LOADING, null, "")
                }

                override fun onNext(s: List<IndexTagBean>) {
                    indexTagLiveData.value = Resource(Resource.SUCCESS, s, "")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    indexTagLiveData.value = Resource(Resource.ERROR, null, e.message)
                }
            })
        return indexTagLiveData
    }



}