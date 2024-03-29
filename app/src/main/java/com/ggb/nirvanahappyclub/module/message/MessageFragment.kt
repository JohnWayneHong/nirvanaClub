package com.ggb.nirvanahappyclub.module.message

import android.view.View
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.FragmentMessageBinding
import com.gyf.immersionbar.ImmersionBar

class MessageFragment : BaseFragment<MessageViewModel, FragmentMessageBinding>(),OnItemSingleClickListener{

    //构造方法可补充
    init {

    }


    override fun initView() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).titleBar(mBindingView.tbMessageMain).navigationBarColor(R.color.white).init()
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

}