package com.ggb.nirvanahappyclub.module.community.android

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.listener.OnLoadMoreListener
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.CommunityAndroidBean
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityAndroidBinding
import com.ggb.nirvanahappyclub.module.community.CommunityWebContentActivity
import com.ggb.nirvanahappyclub.module.community.android.adapter.CommunityAndroidAdapter

class CommunityAndroidFragment : BaseFragment<CommunityAndroidViewModel, FragmentCommunityAndroidBinding>(),OnItemSingleClickListener{

    private var mAdapter : CommunityAndroidAdapter?=null

    override fun initView() {

    }

    override fun initFragmentData() {
        mAdapter = CommunityAndroidAdapter()
        mBindingView.rvCommunityAndroid.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        mBindingView.rvCommunityAndroid.adapter = mAdapter
        mViewModel.setDataList(mAdapter?.dataList)
        mViewModel.getCommunityAndroid(0)
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityAndroidListLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> {
                    showLoadingDialog()
                }
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    mBindingView.swipeRefreshLayout.finishRefresh()
                    loadList(resource.data)
                    LogUtils.xswShowLog(resource.data.size.toString())
                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addOnClickListener()
            .submit()
        mAdapter?.setOnItemClickListener(this)
        mBindingView.rvCommunityAndroid.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onScrollToLastItem() {
                mViewModel.onLoadMore()
            }
        })
        mBindingView.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.refreshList()
        }
    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id

    }

    override fun onItemClick(view: View, position: Int) {
        val articleInfoBean = mAdapter!!.dataList[position]
        val id = view.id
        if (id == R.id.cv_community_content) {
            CommunityWebContentActivity.start(activity,articleInfoBean.id.toString(),articleInfoBean.title,articleInfoBean.link)
        }
    }

    private fun loadList(data: List<CommunityAndroidBean.CommunityAndroidListBean>) {
        if (mViewModel.getCurrentPage() == 0) {
            mAdapter?.notifyRemoveListItem()
        }
        mAdapter?.notifyAddListItem(data)
    }

    companion object {
        fun newInstance(): CommunityAndroidFragment {
            val fragment = CommunityAndroidFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}