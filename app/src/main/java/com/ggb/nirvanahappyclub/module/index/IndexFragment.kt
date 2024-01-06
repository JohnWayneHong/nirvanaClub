package com.ggb.nirvanahappyclub.module.index

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.IndexTagBean
import com.ggb.nirvanahappyclub.databinding.FragmentIndexBinding
import com.ggb.nirvanahappyclub.module.article.ArticleInfoFragment
import com.ggb.nirvanahappyclub.module.index.adapter.IndexTagAdapter
import com.ggb.nirvanahappyclub.module.index.dialog.DownloadProgressDialog
import com.gyf.immersionbar.ImmersionBar

class IndexFragment : BaseFragment<IndexViewModel, FragmentIndexBinding>(),OnItemSingleClickListener{

    private var mIndex = 0

    private var tAdapter : IndexTagAdapter?=null

    private var tagList = arrayListOf<IndexTagBean>()

    private var fragments: ArrayList<Fragment> = ArrayList()

    private var processDialog: DownloadProgressDialog? = null

    private var patchPath = ""

    //构造方法可补充
    init {

    }


    override fun initView() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).navigationBarColor(R.color.white).titleBar(mBindingView.llIndexSearch).init()

    }

    override fun initFragmentData() {
        tAdapter = IndexTagAdapter()
        mBindingView.rvIndexTags.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        mBindingView.rvIndexTags.adapter = tAdapter


        mViewModel.searchIndexTag()
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getIndexTagLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    tagList.addAll(resource.data)
                    initTag()
                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addView(mBindingView.llIndexSearch)
            .addOnClickListener()
            .submit()
        tAdapter?.setOnItemClickListener(this)
    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.ll_index_search) {
            mViewModel.searchIndexTag()
        }
    }

    override fun onItemClick(view: View, position: Int) {
        super.onItemClick(view, position)
        tAdapter?.selectItem(position)
        articleInfoChange(position)
    }

    private fun initTag(){
        tAdapter?.notifyRefreshList(tagList)
        initTagFragment()
    }

    private fun initTagFragment(){
        fragments.clear()
        tagList.forEach {
            fragments.add(ArticleInfoFragment.newInstance(it.id))
        }

        mBindingView.vpIndexArticleVp.adapter = object : FragmentPagerAdapter(childFragmentManager){
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }
            override fun getCount(): Int = fragments.size
            override fun getItemId(position: Int): Long {
                return tagList.get(position).id.toLong()
            }

        }

        mBindingView.vpIndexArticleVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
//                if(!fragments[position].isLoadComplete){
//                    fragments[position].refreshData()
//                }
                articleInfoChange(position)
                mIndex = position
            }
        })

        mBindingView.vpIndexArticleVp.offscreenPageLimit = tagList.size
        articleInfoChange(0)
    }

    private fun articleInfoChange(position: Int) {
        mBindingView.vpIndexArticleVp.setCurrentItem(position,true)
        tAdapter?.selectItem(position)
        mBindingView.rvIndexTags.smoothScrollToPosition(position)
    }
}