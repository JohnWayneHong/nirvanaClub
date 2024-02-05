package com.ggb.nirvanahappyclub.module.community.text

import android.os.Bundle
import android.view.View
import cn.bingoogolapple.baseadapter.BGAAdapterViewAdapter
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout

import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.DevelopJokesBean
import com.ggb.nirvanahappyclub.bean.DevelopJokesListBean
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityTextBinding
import com.ggb.nirvanahappyclub.databinding.ItemCommunityTextBinding
import com.ggb.nirvanahappyclub.databinding.ItemFullSpanBinding
import com.ggb.nirvanahappyclub.databinding.ItemSimpleBinding
import com.ggb.nirvanahappyclub.module.subscribe.FullSpanModel
import com.ggb.nirvanahappyclub.module.subscribe.LeastAnimationStateChangedHandler
import com.ggb.nirvanahappyclub.module.subscribe.SimpleModel
import com.ggb.nirvanahappyclub.utils.EncryptionUtils
import com.ggb.nirvanahappyclub.utils.RegularUtil

class CommunityTextFragment : BaseFragment<CommunityTextViewModel, FragmentCommunityTextBinding>(),OnItemSingleClickListener,
    BGANinePhotoLayout.Delegate{

    override fun initView() {

        mBindingView.rvCommunityTextFragment.linear().setup {
            addType<DevelopJokesListBean>(R.layout.item_community_text)

            onBind {
                when (itemViewType) {
                    R.layout.item_community_text -> {
                        getBinding<ItemCommunityTextBinding>().data = getModel<DevelopJokesListBean>()

                        if (getBinding<ItemCommunityTextBinding>().data!!.joke.type >= 2) {
                            getBinding<ItemCommunityTextBinding>().nplItemMomentPhotos.setDelegate(this@CommunityTextFragment)
                            //处理多图数据

                            val formulaStr = getBinding<ItemCommunityTextBinding>().data!!.joke.imageUrl.split(",")
                            val decodeString = arrayListOf<String>()
                            formulaStr.forEach {
                                decodeString.add(
                                    EncryptionUtils.decrypt(
                                        "cretinzp**273846", RegularUtil.truncateHeadString(it,6)
                                    ))
                            }

                            getBinding<ItemCommunityTextBinding>().nplItemMomentPhotos.data = decodeString
                        }

                    }
                }
            }
        }


        // 该处理者可以保证骨骼动图显示最短时间(避免网络请求过快导致骨骼动画快速消失屏幕闪烁), 如果不需要可以不配置
        // 推荐在Application中全局配置, 而不是每次都配置
        mBindingView.prCommunityTextFragment.stateChangedHandler = LeastAnimationStateChangedHandler()

    }

    override fun initFragmentData() {
        mBindingView.prCommunityTextFragment.onRefresh {
            mViewModel.getCommunityText("获取段子乐纯文")

        }.autoRefresh()

    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityTextLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> {}
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    mBindingView.prCommunityTextFragment.addData(resource.data)

                }
                Resource.ERROR -> {}
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

    override fun onClickNinePhotoItem(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {

    }

    override fun onClickExpand(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {

    }

}