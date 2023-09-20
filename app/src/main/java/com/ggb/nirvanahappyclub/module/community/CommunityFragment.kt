package com.ggb.nirvanahappyclub.module.community

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.databinding.FragmentLoginBinding

class CommunityFragment : BaseFragment<CommunityViewModel, FragmentLoginBinding>(),SwipeRefreshLayout.OnRefreshListener,OnItemSingleClickListener{

    //构造方法可补充
    init {

    }


    override fun initView() {

    }

    override fun initFragmentData() {

    }

    override fun initLiveData() {
        super.initLiveData()

    }

    override fun initListener() {
        super.initListener()

    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }

    override fun onRefresh() {

    }
}