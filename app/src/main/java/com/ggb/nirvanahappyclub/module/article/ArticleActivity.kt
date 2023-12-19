package com.ggb.nirvanahappyclub.module.article

import android.content.Context
import android.content.Intent
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.databinding.ActivityArticleBinding

class ArticleActivity : BaseActivity<ArticleInfoViewModel, ActivityArticleBinding>(), OnItemSingleClickListener {

    private var articleId = ""
    private var authorId = ""


    override fun initView() {

    }

    override fun initData() {
        articleId = intent.getStringExtra("articleId").toString()

        mViewModel.getArticleContentById(articleId)
//        mBindingView.rsbArticleLike.init(this)
    }


    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getArticleContentByIdLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    mBindingView.data = resource.data
                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    companion object {
        fun start(context: Context, articleId: String) {
            if (isActivityNotFinished(context)) {
                val intent = Intent(context, ArticleActivity::class.java)
                intent.putExtra("articleId", articleId)
                context.startActivity(intent)
            }
        }
    }
}