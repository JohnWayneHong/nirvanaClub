package com.ggb.nirvanahappyclub.module.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.http.Resource
import com.ggb.common_library.http.rxjava.CustomObserver
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.ArticleContentBean
import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean
import com.ggb.nirvanahappyclub.bean.SimpleUserInfo
import com.ggb.nirvanahappyclub.network.HttpUtils
import com.ggb.nirvanahappyclub.network.api.ApiService
import io.reactivex.disposables.Disposable

class LoginModel {

    private val userLoginByPwdLiveData: MutableLiveData<Resource<String>> = ValueKeeperLiveData()

    fun getUserLoginByPwdLiveData(map:HashMap<String,Any>): LiveData<Resource<String>> {

        HttpUtils.getGatewayApi(ApiService::class.java)
            .login(map)
            .compose(HttpUtils.applyMainSchedulers())
            .subscribe(object : CustomObserver<String>() {

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    userLoginByPwdLiveData.value = Resource(Resource.LOADING, null, "")
                }

                override fun onNext(s: String) {
                    userLoginByPwdLiveData.value = Resource(Resource.SUCCESS, s, "")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    userLoginByPwdLiveData.value = Resource(Resource.ERROR, null, e.message)
                }
            })
        return userLoginByPwdLiveData
    }

    private val userInfoLiveData: MutableLiveData<Resource<SimpleUserInfo>> = ValueKeeperLiveData()

    fun getUserInfoLiveData(xx:String): LiveData<Resource<SimpleUserInfo>> {

        HttpUtils.getGatewayApi(ApiService::class.java)
            .userInfo
            .compose(HttpUtils.applyMainSchedulers())
            .subscribe(object : CustomObserver<SimpleUserInfo>() {

                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    userInfoLiveData.value = Resource(Resource.LOADING, null, "")
                }

                override fun onNext(info: SimpleUserInfo) {
                    userInfoLiveData.value = Resource(Resource.SUCCESS, info, "")
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    userInfoLiveData.value = Resource(Resource.ERROR, null, e.message)
                }
            })
        return userInfoLiveData
    }

}