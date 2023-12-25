package com.ggb.nirvanahappyclub.module.community.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean

class CommunityNavigationModel {

    private val getCommunityTitleLiveData: MutableLiveData<Resource<List<CommunityTitleBean>>> = ValueKeeperLiveData()

    fun getCommunityTitleListLiveData(tagId:String): LiveData<Resource<List<CommunityTitleBean>>> {

        val selectItemSupports: ArrayList<CommunityTitleBean> = ArrayList<CommunityTitleBean>()
        var p = CommunityTitleBean()
        p.setTitle("Android")
        p.setId(1)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("广场")
        p.setId(2)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("体系")
        p.setId(3)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("导航")
        p.setId(4)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("日报")
        p.setId(5)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("段子乐文")
        p.setId(6)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("段子乐图")
        p.setId(7)
        selectItemSupports.add(p)

        p = CommunityTitleBean()
        p.setTitle("推荐的人")
        p.setId(8)
        selectItemSupports.add(p)

        getCommunityTitleLiveData.value = Resource(Resource.SUCCESS, selectItemSupports, "")
        return getCommunityTitleLiveData

//        val map = HashMap<String, Any>()
//        map["tagId"] = tagId
//        map["page"] = current-1
//        map["size"] = CustomRecyclerAdapter.ITEM_PAGE_SIZE
//
//        HttpUtils.getGatewayApi(ApiService::class.java)
//            .searchArticleByTag(map)
//            .compose(HttpUtils.applyMainSchedulers())
//            .subscribe(object : CustomObserver<List<CommunityTitleBean>>() {
//
//                override fun onSubscribe(d: Disposable) {
//                    super.onSubscribe(d)
//                    getCommunityTitleLiveData.value = Resource(Resource.LOADING, null, "")
//                }
//
//                override fun onNext(s: List<CommunityTitleBean>) {
//                    getCommunityTitleLiveData.value = Resource(Resource.SUCCESS, s, "")
//                }
//
//                override fun onError(e: Throwable) {
//                    super.onError(e)
//                    getCommunityTitleLiveData.value = Resource(Resource.ERROR, null, e.message)
//                }
//            })
//        return getCommunityTitleLiveData
    }
}