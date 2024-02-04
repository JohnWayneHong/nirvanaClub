package com.ggb.nirvanahappyclub.module.community

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityBinding
import com.ggb.nirvanahappyclub.module.community.android.CommunityAndroidFragment
import com.ggb.nirvanahappyclub.module.community.daily.CommunityDailyFragment
import com.ggb.nirvanahappyclub.module.community.knowledge.CommunityKnowledgeFragment
import com.ggb.nirvanahappyclub.module.community.navigation.CommunityNavigationFragment
import com.ggb.nirvanahappyclub.module.community.picture.CommunityPictureFragment
import com.ggb.nirvanahappyclub.module.community.square.CommunitySquareFragment
import com.ggb.nirvanahappyclub.module.community.text.CommunityTextFragment
import com.gyf.immersionbar.ImmersionBar
import java.util.ArrayList

class CommunityFragment : BaseFragment<CommunityViewModel, FragmentCommunityBinding>(),OnItemSingleClickListener{

    private val mFragments = ArrayList<Fragment>()
    private var mAdapter: MyPagerAdapter? = null


    override fun initView() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).titleBar(mBindingView.rxToolCommunityTitle).navigationBarColor(R.color.white).init()

    }

    override fun initFragmentData() {
        mViewModel.getCommunityTitle("后期可能Title动态获取")
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityTitleLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()

                    val tempList = arrayListOf<String>()
                    for ((index ,value ) in resource.data.withIndex()) {
                        tempList.add(value.title)
                    }
                    mViewModel.titleList.addAll(tempList)

                    mFragments.clear()
                    mFragments.add(CommunityAndroidFragment.newInstance())
                    mFragments.add(CommunitySquareFragment.newInstance())
                    mFragments.add(CommunityKnowledgeFragment.newInstance())
                    mFragments.add(CommunityNavigationFragment.newInstance())
                    mFragments.add(CommunityDailyFragment.newInstance())
//                    mFragments.add(CommunityTextFragment.newInstance())
                    mFragments.add(CommunityPictureFragment.newInstance())
//                    mFragments.add(CommunitySubscriptFragment.newInstance())
                    mAdapter = MyPagerAdapter(childFragmentManager)
                    mBindingView.vpCommunity.adapter = mAdapter
                    mBindingView.rxToolCommunityTitle.setViewPager(mBindingView.vpCommunity,tempList.toTypedArray(),context as FragmentActivity,mFragments)
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

    private inner class MyPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mViewModel.titleList[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }

}