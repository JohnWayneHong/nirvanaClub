package com.ggb.nirvanahappyclub.module.index

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.IndexTagBean

class IndexViewModel(application: Application) : BaseViewModel(application) {

    private var mModel: IndexModel = IndexModel()

    private val mGetIndexTagLiveData: MutableLiveData<String> = ValueKeeperLiveData()


    fun searchIndexTag() {
        mGetIndexTagLiveData.value = "null"
    }

    fun getIndexTagLiveData(): LiveData<Resource<List<IndexTagBean>>> {
        return Transformations.switchMap(mGetIndexTagLiveData, mModel::getIndexTagLiveData)
    }


}