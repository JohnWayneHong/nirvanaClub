package com.ggb.nirvanahappyclub.module.article

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ggb.common_library.base.viewmodel.BaseViewModel
import com.ggb.common_library.http.Resource
import com.ggb.common_library.livedata.ValueKeeperLiveData
import com.ggb.nirvanahappyclub.bean.ArticleContentBean
import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean
import com.ggb.nirvanahappyclub.bean.IndexTagBean

class ArticleInfoViewModel(application: Application) : BaseViewModel(application) {

    private var mModel: ArticleInfoModel = ArticleInfoModel()

    private val mGetArticleByTagLiveData: MutableLiveData<Int> = ValueKeeperLiveData()
    private val mGetArticleContentByIdLiveData: MutableLiveData<String> = ValueKeeperLiveData()

    var tagId = ""

    private var mPage = 1


    fun searchArticleByIndexTag() {
        mGetArticleByTagLiveData.value = mPage
    }

    fun getArticleByTagLiveData(): LiveData<Resource<List<IndexArticleInfoBean>>> {
        return Transformations.switchMap(mGetArticleByTagLiveData) { input: Int ->
            mModel.getArticleByTagLiveData(tagId, input)
        }
    }

    fun getArticleContentById(articleId:String) {
        mGetArticleContentByIdLiveData.value = articleId
    }

    fun getArticleContentByIdLiveData(): LiveData<Resource<ArticleContentBean>> {
        return Transformations.switchMap(mGetArticleContentByIdLiveData) { input: String ->
            mModel.getArticleContentByIdLiveData(input )
        }
    }

}