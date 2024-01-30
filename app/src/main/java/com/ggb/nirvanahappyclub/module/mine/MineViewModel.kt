package com.ggb.nirvanahappyclub.module.mine

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.MeFunctionBean

class MineViewModel(application: Application) : BaseViewModel(application) {

    private var model: MineModel = MineModel()

    private val mGetFunctionListLiveData: MutableLiveData<String> = ValueKeeperLiveData()


    fun searchMeFunctionLiveData() {
        mGetFunctionListLiveData.value = "null"
    }

    fun getMeFunctionListLiveData(): LiveData<Resource<List<MeFunctionBean>>> {
        return Transformations.switchMap(mGetFunctionListLiveData, model::getMeFunctionListLiveData)
    }

}