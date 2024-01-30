package com.ggb.nirvanahappyclub.module.subscribe

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.ItemFullSpanBinding
import com.ggb.nirvanahappyclub.databinding.ItemSimpleBinding
import com.ggb.nirvanahappyclub.databinding.TestRoundImageViewBinding

class SubscribeFragment : BaseFragment<SubscribeViewModel, TestRoundImageViewBinding>(),OnItemSingleClickListener{

    //构造方法可补充
    init {

    }


    override fun initView() {
        setHasOptionsMenu(true)
        mBindingView.rvSubscribeFragment.linear().setup {
            addType<SimpleModel>(R.layout.item_simple)
            addType<FullSpanModel>(R.layout.item_full_span)

            onBind {
                when (itemViewType) {
                    R.layout.item_simple -> {
                        getBinding<ItemSimpleBinding>().data = getModel<SimpleModel>()
                    }
                    R.layout.item_full_span -> {
                        getBinding<ItemFullSpanBinding>().data = getModel<FullSpanModel>()
                    }
                }
            }
        }



        // 该处理者可以保证骨骼动图显示最短时间(避免网络请求过快导致骨骼动画快速消失屏幕闪烁), 如果不需要可以不配置
        // 推荐在Application中全局配置, 而不是每次都配置
        mBindingView.prSubscribeFragment.stateChangedHandler = LeastAnimationStateChangedHandler()
        mBindingView.prSubscribeFragment.onRefresh {
            val runnable = { // 模拟网络请求, 创建假的数据集
                val data = getData()
                addData(data) {
                    index < 3 // 判断是否有更多页
                }
            }
            postDelayed(runnable, 500)

        }.showLoading()
    }

    private fun getData(): MutableList<Any> {
        return mutableListOf<Any>().apply {
            for (i in 0..9) {
                when (i) {
                    1, 2, 5 -> add(FullSpanModel())
                    else -> add(SimpleModel())
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_skeleton, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_loading -> mBindingView.prSubscribeFragment.showLoading()  // 加载中
            R.id.menu_content -> mBindingView.prSubscribeFragment.showContent() // 加载成功
            R.id.menu_error -> mBindingView.prSubscribeFragment.showError(force = true) // 强制加载错误
            R.id.menu_empty -> mBindingView.prSubscribeFragment.showEmpty() // 空数据
        }
        return super.onOptionsItemSelected(item)
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