package com.ggb.nirvanahappyclub.module.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.MeFunctionBean
import com.ggb.nirvanahappyclub.network.HttpUtils
import com.ggb.nirvanahappyclub.network.api.ApiService
import io.reactivex.disposables.Disposable

class MineModel {
    
    private val meFunctionLiveData: MutableLiveData<Resource<List<MeFunctionBean>>> = ValueKeeperLiveData()

    fun getMeFunctionListLiveData(xx:String): LiveData<Resource<List<MeFunctionBean>>> {

        val optionList = arrayListOf(
            MeFunctionBean("能康码", "展示健康信息"),
            MeFunctionBean("我的钱包", "首充优惠"),
            MeFunctionBean("牛蛙呐活动", "日更挑战"),
            MeFunctionBean("每日任务" ),
            MeFunctionBean("我的专题" ),
            MeFunctionBean("浏览历史" ),
            MeFunctionBean("设置" ),
            MeFunctionBean("清除缓存" ),
            MeFunctionBean("帮助与反馈" ),
            MeFunctionBean("了解更多" ),
            MeFunctionBean("开发者模式" )

        )

        meFunctionLiveData.value = Resource(Resource.SUCCESS, optionList, "")

        return meFunctionLiveData

//        HttpUtils.getGatewayApi(ApiService::class.java)
//            .searchIndexTag()
//            .compose(HttpUtils.applyMainSchedulers())
//            .subscribe(object : CustomObserver<List<MeFunctionBean>>() {
//
//                override fun onSubscribe(d: Disposable) {
//                    super.onSubscribe(d)
//                    meFunctionLiveData.value = Resource(Resource.LOADING, null, "")
//                }
//
//                override fun onNext(s: List<MeFunctionBean>) {
//                    meFunctionLiveData.value = Resource(Resource.SUCCESS, s, "")
//                }
//
//                override fun onError(e: Throwable) {
//                    super.onError(e)
//                    meFunctionLiveData.value = Resource(Resource.ERROR, null, e.message)
//                }
//            })
//        return meFunctionLiveData
    }
}