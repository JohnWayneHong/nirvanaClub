package com.ggb.nirvanahappyclub.module.community.android

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityBinding
import com.ggb.nirvanahappyclub.module.community.CommunityViewModel
import java.util.ArrayList

class CommunityAndroidFragment : BaseFragment<CommunityAndroidViewModel, FragmentCommunityAndroidBinding>(),OnItemSingleClickListener{

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
        fun newInstance(): CommunityAndroidFragment {
            val fragment = CommunityAndroidFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}