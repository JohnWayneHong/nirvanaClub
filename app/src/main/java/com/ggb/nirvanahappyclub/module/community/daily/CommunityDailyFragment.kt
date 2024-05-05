package com.ggb.nirvanahappyclub.module.community.daily

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.ToastUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.UserShowBean
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityDailyBinding
import com.ggb.nirvanahappyclub.sql.entity.UserEntity
import com.tamsiree.rxkit.view.RxToast
import kotlin.random.Random

class CommunityDailyFragment : BaseFragment<CommunityDailyViewModel, FragmentCommunityDailyBinding>(),OnItemSingleClickListener{

    private var tempId : Long = -1

    override fun initView() {
        val userShowBean = UserShowBean()

        mBindingView.data = userShowBean
    }

    override fun initFragmentData() {

        val userEntity = UserEntity(0, "119", "乖乖帮")
        mViewModel.getCommunityDailyData(userEntity)
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityDailyDataLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()

                    tempId = resource.data
                    RxToast.success("返回的数据是"+resource.data.toString())
                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }

        mViewModel.getCommunityDailyDataInfoLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    val userEntity = resource!!.data

                    mBindingView.data!!.setMobile(userEntity.phone)
                    mBindingView.data!!.setUserName(userEntity.companyId)

                    LogUtils.xswShowLog(resource.data.companyId)
                }
                Resource.ERROR -> {

                    dismissLoadingDialog()
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addOnClickListener()
            .addView(mBindingView.tvCommunityDailySearch)
            .addView(mBindingView.tvCommunityDailyInsert)
            .submit()
    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.tv_community_daily_search) {
            mViewModel.getCommunityDailyDataInfo(tempId)
        }else if (id == R.id.tv_community_daily_insert) {
            val userEntity = generateRandomUserEntity()
            mViewModel.getCommunityDailyData(userEntity)
        }
    }

    private fun generateRandomUserEntity(): UserEntity {

        val randomUserId = Random.nextInt(1000000).toString() // 生成一个 6 位数的随机字符串
        val names = listOf(
            "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hannah",
            "Isaac", "Julia", "Kevin", "Linda", "Mike", "Nancy", "Olivia", "Peter",
            "Quinn", "Rachel", "Sam", "Tracy", "Ursula", "Victor", "Wendy", "Xavier",
            "Yvonne", "Zach"
        ) // 更多可能的名字
        val randomName = names.random() // 从列表中随机选择一个名字
        val userEntity = UserEntity()
        userEntity.phone = randomUserId
        userEntity.companyId = randomName

        return userEntity
    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }

    companion object {
        fun newInstance(): CommunityDailyFragment {
            val fragment = CommunityDailyFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

}