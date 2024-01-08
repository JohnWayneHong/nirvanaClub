package com.ggb.nirvanahappyclub.module.community.android

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.common_library.utils.ToastUtils
import com.ggb.nirvanahappyclub.bean.CommunityAndroidBean
import com.ggb.nirvanahappyclub.common.AppConfig

class CommunityAndroidViewModel(application: Application) : BaseViewModel(application) {

    private var mPage = 0

    private var model: CommunityAndroidModel = CommunityAndroidModel()


    private var mList: List<CommunityAndroidBean.CommunityAndroidListBean> = ArrayList()

    private val mGetCommunityTitleLiveData: MutableLiveData<Int> = ValueKeeperLiveData()

    var titleList = arrayListOf<String>()

    fun getCurrentPage(): Int {
        return mPage
    }

    fun setDataList(dataList: List<CommunityAndroidBean.CommunityAndroidListBean>?) {
        if (dataList != null) {
            mList = dataList
        }
    }

    fun onLoadMore() {
        if (mList.size != AppConfig.ITEM_PAGE_SIZE * (mPage+1)) {
            ToastUtils.showToast("没有更多了")
            return
        }
        mPage++
        mGetCommunityTitleLiveData.value = mPage
    }

    fun refreshList() {
        mPage = 0
        mGetCommunityTitleLiveData.value = mPage
    }

    fun getCommunityAndroid(titleType:Int) {
        mGetCommunityTitleLiveData.value = mPage
    }

    fun getCommunityAndroidListLiveData(): LiveData<Resource<List<CommunityAndroidBean.CommunityAndroidListBean>>>{
        return Transformations.switchMap(mGetCommunityTitleLiveData) {
            input:Int -> model.getCommunityAndroidList(input)
        }
    }

}