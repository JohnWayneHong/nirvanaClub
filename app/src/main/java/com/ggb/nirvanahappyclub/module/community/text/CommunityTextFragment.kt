package com.ggb.nirvanahappyclub.module.community.text

import android.os.Bundle
import android.view.View
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityTextBinding

class CommunityTextFragment : BaseFragment<CommunityTextViewModel, FragmentCommunityTextBinding>(),OnItemSingleClickListener{

    override fun initView() {

    }

    override fun initFragmentData() {

    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityTitleLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()

                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    override fun initListener() {
        super.initListener()

    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }

    companion object {
        fun newInstance(): CommunityTextFragment {
            val fragment = CommunityTextFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}