package com.ggb.nirvanahappyclub.module.community.square

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
import com.ggb.nirvanahappyclub.databinding.FragmentCommunitySquareBinding
import com.ggb.nirvanahappyclub.module.community.CommunityWebContentActivity
import com.ggb.nirvanahappyclub.module.community.android.adapter.CommunityAndroidAdapter
import com.ggb.nirvanahappyclub.module.community.square.adapter.CommunitySquareAdapter

class CommunitySquareFragment : BaseFragment<CommunitySquareViewModel, FragmentCommunitySquareBinding>(),OnItemSingleClickListener{

    private var mAdapter : CommunitySquareAdapter?=null

    override fun initView() {

    }

    override fun initFragmentData() {
        mAdapter = CommunitySquareAdapter()
        mBindingView.rvCommunitySquare.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        mBindingView.rvCommunitySquare.adapter = mAdapter
        mViewModel.setDataList(mAdapter?.dataList)
        mViewModel.getCommunitySquare(0)
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunitySquareListLiveData().observe(this) { resource ->
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
        mBindingView.rvCommunitySquare.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onScrollToLastItem() {
                mViewModel.onLoadMore()
            }
        })
        mBindingView.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.refreshList()
        }
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
        fun newInstance(): CommunitySquareFragment {
            val fragment = CommunitySquareFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}