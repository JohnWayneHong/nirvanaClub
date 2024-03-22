package com.ggb.nirvanahappyclub.module.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.ArticleContentBean
import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean
import com.ggb.nirvanahappyclub.bean.SimpleUserInfo
import com.ggb.nirvanahappyclub.utils.RsaEncryptionUtils

class LoginViewModel(application: Application) : BaseViewModel(application) {

    private var mModel: LoginModel = LoginModel()

    private val mGetUserLoginByPwdLiveData: MutableLiveData<HashMap<String,Any>> = ValueKeeperLiveData()
    private val mGetUserInfoLiveData: MutableLiveData<String> = ValueKeeperLiveData()


    fun loginByPwd(account: String, password :String) {
        val map = HashMap<String, Any>()
        map["account"] = account
        map["password"] = RsaEncryptionUtils().rsaEncode(password)
        map["rememberMe"] = true
        mGetUserLoginByPwdLiveData.value = map
    }

    fun getLoginByPwdLiveData(): LiveData<Resource<String>> {
        return Transformations.switchMap(mGetUserLoginByPwdLiveData) { input: HashMap<String,Any> ->
            mModel.getUserLoginByPwdLiveData(input)
        }
    }

    fun getUserInfo() {
        mGetUserInfoLiveData.value = ""
    }

    fun getUserInfoLiveData(): LiveData<Resource<SimpleUserInfo>> {
        return Transformations.switchMap(mGetUserInfoLiveData) { input->
            mModel.getUserInfoLiveData(input)
        }
    }


}