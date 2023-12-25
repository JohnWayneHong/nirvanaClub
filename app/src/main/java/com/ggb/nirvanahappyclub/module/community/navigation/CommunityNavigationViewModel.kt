package com.ggb.nirvanahappyclub.module.community.navigation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean

class CommunityNavigationViewModel(application: Application) : BaseViewModel(application) {

    private var model: CommunityNavigationModel = CommunityNavigationModel()

    var titleList = arrayListOf<String>()


    private val mGetCommunityTitleLiveData: MutableLiveData<String> = ValueKeeperLiveData()



    fun getCommunityTitle(titleType:String) {
        mGetCommunityTitleLiveData.value = titleType
    }

    fun getCommunityTitleLiveData(): LiveData<Resource<List<CommunityTitleBean>>>{
        return Transformations.switchMap(mGetCommunityTitleLiveData) {
            input:String -> model.getCommunityTitleListLiveData(input)
        }
    }

}