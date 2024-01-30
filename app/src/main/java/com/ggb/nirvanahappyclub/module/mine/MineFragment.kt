package com.ggb.nirvanahappyclub.module.mine

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.ToastUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.FragmentMeBinding
import com.ggb.nirvanahappyclub.module.mine.adapter.MeBannerAdapter
import com.ggb.nirvanahappyclub.module.mine.adapter.MeFunctionAdapter
import com.gyf.immersionbar.ImmersionBar
import java.util.Timer
import java.util.TimerTask

class MineFragment : BaseFragment<MineViewModel, FragmentMeBinding>(),OnItemSingleClickListener{

    private var aAdapter : MeFunctionAdapter?=null
    private var bAdapter : MeBannerAdapter?=null

    private var bannerHeight: Int = 0
    // 从 999999 开始，刚好可以被 3 整除，也就是从第一张开始，左右都可以滑动
    private var curCarouselPosition = 999999
    private var carouselTask: TimerTask? = null
    private val carouselTimer = Timer()
    private val carouselHandler = Handler(Looper.myLooper()!!) {
        LogUtils.xswShowLog(curCarouselPosition.toString())
        mBindingView.vpMeCarousel.setCurrentItem(++curCarouselPosition, true)
        true
    }

    //构造方法可补充
    init {

    }


    override fun initView() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).navigationBarColor(R.color.white).titleBar(mBindingView.meToolbar).init()

        aAdapter = MeFunctionAdapter()
        mBindingView.meUserOptionsRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        mBindingView.meUserOptionsRv.adapter = aAdapter

        bAdapter = MeBannerAdapter()
        mBindingView.vpMeCarousel.adapter = bAdapter

        // 计算出横幅高度，当滑动距离超过横幅后，正好让标题栏TitleBar的透明度变为1
        bannerHeight = mBindingView.meBanner.layoutParams.height - mBindingView.meToolbar.height - ImmersionBar.getStatusBarHeight(this)
    }

    override fun initFragmentData() {

        setCarousel()
        initFunctionList()
        mBindingView.meScrollView.apply {
            setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
                // scrollY 为总滑动距离
                val alpha = scrollY.toFloat() / bannerHeight
                mBindingView.meToolbar.alpha = if (alpha > 1) 1f else alpha
            })
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getMeFunctionListLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()
                    aAdapter?.notifyRefreshList(resource.data)
                }
                Resource.ERROR -> dismissLoadingDialog()
            }

        }
    }

    override fun initListener() {
        super.initListener()

        aAdapter?.setOnItemClickListener(this)
    }

    private fun initFunctionList() {
        mViewModel.searchMeFunctionLiveData()
    }

    override fun onItemClick(view: View, position: Int) {
        super.onItemClick(view, position)

        val id = view.id
        if (id == R.id.ll_me_option) {
            ToastUtils.showToast(aAdapter?.dataList?.get(position)?.title)
        }
    }

    //轮播图
    @SuppressLint("ClickableViewAccessibility")
    private fun setCarousel(){
        mBindingView.vpMeCarousel.apply {
            val imgIds = mutableListOf(R.mipmap.carousel1, R.mipmap.carousel2, R.mipmap.carousel3)
            bAdapter?.notifyRefreshList(imgIds)
//            bAdapter?.notifyAddListItem(imgIds)

            // 从 999999 开始，刚好可以被 3 整除，也就是从第一张开始，并且左右都可以滑动
            setCurrentItem(999999, false)

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                // 切换页面过程中，动态移动轮播图下方小白点的位置，调用 CarouselDots 的 setSelectedDotX 即可
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    mBindingView.meCarouselDots.setSelectedDotX(position % imgIds.size, positionOffset)
                }

                // 当切换到新的下标位置时，及时更新 curCarouselPosition 的值
                // Handler 根据 curCarouselPosition 的值进行自动滚动
                override fun onPageSelected(position: Int) {
                    curCarouselPosition = position
                }
            })
            /*
             * 轮播图手动滑动时，让计时器停止，等到手移开后重新计时
             * 注意不要返回 true，返回 true 就消费了事件，导致 vp 没办法正常滑动
             */
            getChildAt(0).setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        stopCarousel()
                        false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        stopCarousel()
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        startCarousel()
                        false
                    }
                    else -> false
                }
            }
            // 轮播图下面的小点的设置
            mBindingView.meCarouselDots.dotCount = 3
        }
    }

    /**
     * 开始定时任务，每 3s 切换一次轮播图
     */
    private fun startCarousel() {
        carouselTask = newCarouselTask()
        carouselTimer.schedule(carouselTask, 3000L, 3000L)
    }

    private fun stopCarousel() {
        carouselTask?.cancel()
        carouselTimer.purge()
    }

    /**
     * 定时发送任务给 Handler，让 Handler 修改 vp
     */
    private fun newCarouselTask() = object : TimerTask() {
        override fun run() {
            carouselHandler.sendEmptyMessage(0)
        }
    }

    override fun onResume() {
        super.onResume()
        // 启动轮播图
        startCarousel()
    }

    override fun onPause() {
        super.onPause()
        // 停止轮播图
        stopCarousel()
    }
}