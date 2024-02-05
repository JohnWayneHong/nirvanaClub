package com.ggb.nirvanahappyclub.module.community.picture

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean
import com.ggb.nirvanahappyclub.bean.DevelopJokesListBean

class CommunityPictureViewModel(application: Application) : BaseViewModel(application) {

    private var model: CommunityPictureModel = CommunityPictureModel()

    var titleList = arrayListOf<String>()


    private val mGetCommunityPictureLiveData: MutableLiveData<String> = ValueKeeperLiveData()


    fun getCommunityPicture(titleType:String) {
        mGetCommunityPictureLiveData.value = titleType
    }

    fun getCommunityPictureLiveData(): LiveData<Resource<List<DevelopJokesListBean>>>{
        return Transformations.switchMap(mGetCommunityPictureLiveData) {
            input:String -> model.getCommunityPictureListLiveData(input)
        }
    }

}