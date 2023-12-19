package com.ggb.nirvanahappyclub.module.article

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.FragmentArticleInfoBinding
import com.ggb.nirvanahappyclub.module.article.adapter.IndexArticleInfoPagingAdapter

class ArticleInfoFragment : BaseFragment<ArticleInfoViewModel, FragmentArticleInfoBinding>(), OnItemSingleClickListener {

    private var tagId:String = ""
    private var mAdapter : IndexArticleInfoPagingAdapter?=null

    override fun initView() {

    }

    override fun initFragmentData() {
        tagId = arguments?.getString("tagId","").toString()
        mAdapter = IndexArticleInfoPagingAdapter()
        mBindingView.rcyArticleInfoRv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        mBindingView.rcyArticleInfoRv.adapter = mAdapter

        mViewModel.tagId = tagId
        mViewModel.searchArticleByIndexTag()
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addOnClickListener()
            .submit()
        mAdapter?.setOnItemClickListener(this)
    }

    override fun onClick(view: View) {
        super.onClick(view)

    }

    override fun onItemClick(view: View, position: Int) {
        val articleInfoBean = mAdapter!!.dataList[position]
        val id = view.id
        if (id == R.id.ll_article_info) {
            ArticleActivity.start(context,articleInfoBean.id)
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getArticleByTagLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    mAdapter?.notifyRefreshList(resource.data)
                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    companion object {
        fun newInstance(tagId:String): ArticleInfoFragment {
            val fragment = ArticleInfoFragment()
            val bundle = Bundle()
            bundle.putString("tagId",tagId)
            fragment.arguments = bundle
            return fragment
        }
    }
}