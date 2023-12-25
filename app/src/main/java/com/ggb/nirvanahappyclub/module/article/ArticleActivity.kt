package com.ggb.nirvanahappyclub.module.article

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.widget.NestedScrollView
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.ActivityArticleBinding

class ArticleActivity : BaseActivity<ArticleInfoViewModel, ActivityArticleBinding>(), OnItemSingleClickListener {

    private var articleId = ""
    private var authorId = ""


    override fun initView() {

        initEvent()
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

    private fun initEvent() {
        //滑动布局监听
        mBindingView.nsvArticleScroll.apply {
            setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                // scrollY 为总滑动距离
                if (scrollY != 0 && mBindingView.ivArticleColAvatar.visibility == View.INVISIBLE) {
                    mBindingView.ivArticleColAvatar.visibility = View.VISIBLE
                    mBindingView.tvArticleColAuth.visibility = View.VISIBLE
                    mBindingView.btnArticleColSubscribe.visibility = View.VISIBLE
                } else if (scrollY == 0 && mBindingView.ivArticleColAvatar.visibility == View.VISIBLE) {
                    mBindingView.ivArticleColAvatar.visibility = View.INVISIBLE
                    mBindingView.tvArticleColAuth.visibility = View.INVISIBLE
                    mBindingView.btnArticleColSubscribe.visibility = View.INVISIBLE
                }
            })
        }
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addView(mBindingView.ivArticleColBack)
            .addOnClickListener()
            .submit()

    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.iv_article_col_back) {
            onBackPressed()
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