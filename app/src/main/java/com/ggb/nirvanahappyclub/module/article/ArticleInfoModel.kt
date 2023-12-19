package com.ggb.nirvanahappyclub.module.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean
import com.ggb.nirvanahappyclub.network.HttpUtils
import com.ggb.nirvanahappyclub.network.api.ApiService
import com.ggb.nirvanahappyclub.utils.ConstantUtil
import io.reactivex.disposables.Disposable

class ArticleInfoModel {

    private val indexTagLiveData: MutableLiveData<Resource<List<IndexArticleInfoBean>>> = ValueKeeperLiveData()

    fun getArticleByTagLiveData(tagId:String,current : Int): LiveData<Resource<List<IndexArticleInfoBean>>> {

        val map = HashMap<String, Any>()
        map["tagId"] = tagId
        map["page"] = current-1
        map["size"] = CustomRecyclerAdapter.ITEM_PAGE_SIZE

        HttpUtils.getGatewayApi(ApiService::class.java)
            .searchArticleByTag(map)
            .compose(HttpUtils.applyMainSchedulers())
            .subscribe(object : CustomObserver<List<IndexArticleInfoBean>>() {

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    indexTagLiveData.value = Resource(Resource.LOADING, null, "")
                }

                override fun onNext(s: List<IndexArticleInfoBean>) {
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