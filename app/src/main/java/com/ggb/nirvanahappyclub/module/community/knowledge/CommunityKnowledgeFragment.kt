package com.ggb.nirvanahappyclub.module.community.knowledge

import android.os.Bundle
import android.view.View
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityKnowledgeBinding

class CommunityKnowledgeFragment : BaseFragment<CommunityKnowledgeViewModel, FragmentCommunityKnowledgeBinding>(),OnItemSingleClickListener{

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
        fun newInstance(): CommunityKnowledgeFragment {
            val fragment = CommunityKnowledgeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}