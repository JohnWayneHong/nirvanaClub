package com.ggb.nirvanahappyclub.module.community.daily

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.CommunityTitleBean
import com.ggb.nirvanahappyclub.sql.entity.UserEntity

class CommunityDailyViewModel(application: Application) : BaseViewModel(application) {

    private var model: CommunityDailyModel = CommunityDailyModel()

    var titleList = arrayListOf<String>()


    private val mGetCommunityDailyDataLiveData: MutableLiveData<UserEntity> = ValueKeeperLiveData()
    private val mGetCommunityDailyDataInfoLiveData: MutableLiveData<Long> = ValueKeeperLiveData()



    fun getCommunityDailyData(userEntity: UserEntity) {
        mGetCommunityDailyDataLiveData.value = userEntity
    }

    fun getCommunityDailyDataLiveData(): LiveData<Resource<Long>>{
        return Transformations.switchMap(mGetCommunityDailyDataLiveData) {
            input:UserEntity -> model.getCommunityDailyDataLiveData(input)
        }
    }

    fun getCommunityDailyDataInfo(id: Long) {
        mGetCommunityDailyDataInfoLiveData.value = id
    }

    fun getCommunityDailyDataInfoLiveData(): LiveData<Resource<UserEntity>>{
        return Transformations.switchMap(mGetCommunityDailyDataInfoLiveData) {
                input:Long -> model.getCommunityDailyDataInfoLiveData(input)
        }
    }

}