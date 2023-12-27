package com.ggb.nirvanahappyclub.module.community.android

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityAndroidBean
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean

class CommunityAndroidViewModel(application: Application) : BaseViewModel(application) {

    private var model: CommunityAndroidModel = CommunityAndroidModel()

    var titleList = arrayListOf<String>()


    private val mGetCommunityTitleLiveData: MutableLiveData<Int> = ValueKeeperLiveData()



    fun getCommunityAndroid(titleType:Int) {
        mGetCommunityTitleLiveData.value = titleType
    }

    fun getCommunityAndroidListLiveData(): LiveData<Resource<List<CommunityAndroidBean.CommunityAndroidListBean>>>{
        return Transformations.switchMap(mGetCommunityTitleLiveData) {
            input:Int -> model.getCommunityAndroidList(input)
        }
    }

}