package com.ggb.nirvanahappyclub.module.community.text

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean
import com.ggb.nirvanahappyclub.bean.DevelopJokesListBean

class CommunityTextViewModel(application: Application) : BaseViewModel(application) {

    private var model: CommunityTextModel = CommunityTextModel()

    var titleList = arrayListOf<String>()


    private val mGetCommunityTextLiveData: MutableLiveData<String> = ValueKeeperLiveData()



    fun getCommunityText(titleType:String) {
        mGetCommunityTextLiveData.value = titleType
    }

    fun getCommunityTextLiveData(): LiveData<Resource<List<DevelopJokesListBean>>>{
        return Transformations.switchMap(mGetCommunityTextLiveData) {
            input:String -> model.getCommunityTitleListLiveData(input)
        }
    }

}