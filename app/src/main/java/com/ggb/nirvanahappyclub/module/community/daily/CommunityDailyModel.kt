package com.ggb.nirvanahappyclub.module.community.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean
import com.ggb.nirvanahappyclub.sql.entity.UserEntity
import com.ggb.nirvanahappyclub.sql.operator.UserOperator

class CommunityDailyModel {

    private val userOperator: UserOperator = UserOperator()

    private val getCommunityDailyDataLiveData: MutableLiveData<Resource<Long>> = ValueKeeperLiveData()
    private val getCommunityDailyDataInfoLiveData: MutableLiveData<Resource<UserEntity>> = ValueKeeperLiveData()

    fun getCommunityDailyDataLiveData(userEntity: UserEntity): LiveData<Resource<Long>> {

        val userIdByInfo = userOperator.getUserIdByInfo(userEntity)

        getCommunityDailyDataLiveData.value = Resource(Resource.SUCCESS, userIdByInfo, "")
        return getCommunityDailyDataLiveData

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

    fun getCommunityDailyDataInfoLiveData(userId: Long): LiveData<Resource<UserEntity>> {


        val userInfo = userOperator.getUserIdByInfo(userId)

        getCommunityDailyDataInfoLiveData.value = Resource(Resource.SUCCESS, userInfo, "")
        return getCommunityDailyDataInfoLiveData

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