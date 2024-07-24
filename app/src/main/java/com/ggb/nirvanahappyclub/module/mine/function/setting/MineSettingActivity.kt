package com.ggb.nirvanahappyclub.module.mine.function.setting

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.utils.MMKVUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.MineSettingBean
import com.ggb.nirvanahappyclub.bean.MineSettingListBean

import com.ggb.nirvanahappyclub.databinding.ActivityMineSettingBinding
import com.ggb.nirvanahappyclub.utils.ConstantUtil
import com.gyf.immersionbar.ImmersionBar
import per.goweii.anylayer.Layer
import per.goweii.anylayer.guide.GuideLayer


class MineSettingActivity : BaseActivity<MineSettingViewModel,ActivityMineSettingBinding>(){


    private var mAdapter : MineSettingAdapter?=null

    override fun initView() {
        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).navigationBarColor(R.color.white).titleBar(mBindingView.llMeToolbar.publicTitle).init()
        mBindingView.llMeToolbar.tvPublicTitle.text = "牛蛙呐设置"

        mAdapter = MineSettingAdapter()
        mBindingView.meSettingOptionsRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        mBindingView.meSettingOptionsRv.adapter = mAdapter

        initSettingData()
        window?.decorView?.doOnLayout {
            if (MMKVUtils.getString(ConstantUtil.USER_TOKEN).isEmpty()){
                return@doOnLayout
            }else{
                showGuideDialogIfNeeded()
            }
//            showGuideDialogIfNeeded()
        }
    }

    override fun initData() {

    }

    override fun initLiveData() {
        super.initLiveData()
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addOnClickListener()
            .submit()

    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
    }

    private fun initSettingData() {
        val settingList = ArrayList<Any>()
        settingList.add(MineSettingBean("消息推送"))
        settingList.add(MineSettingListBean("文章更新推送"))
        settingList.add(MineSettingListBean("新消息推送"))

        settingList.add(MineSettingBean("账号设置"))
        settingList.add(MineSettingListBean("编辑个人资料"))
        settingList.add(MineSettingListBean("账号与安全"))

        settingList.add(MineSettingBean("通用设置"))
        settingList.add(MineSettingListBean("默认编辑器"))
        settingList.add(MineSettingListBean("添加写文章到桌面"))
        settingList.add(MineSettingListBean("赞赏设置"))
        settingList.add(MineSettingListBean("字号设置"))
        settingList.add(MineSettingListBean("隐私设置"))
        settingList.add(MineSettingListBean("黑名单设置"))
        settingList.add(MineSettingListBean("移动网络下加载图片"))

        settingList.add(MineSettingBean("其他"))
        settingList.add(MineSettingListBean("回收站"))
        settingList.add(MineSettingListBean("清除缓存"))
        settingList.add(MineSettingListBean("版本更新"))
        settingList.add(MineSettingListBean("分享牛蛙呐","推荐"))
        settingList.add(MineSettingListBean("关于我们"))


        mAdapter?.notifyRefreshList(settingList)
        if (MMKVUtils.getString(ConstantUtil.USER_TOKEN).isNullOrEmpty()){
            mBindingView.floatingLoginOutBtn.visibility = View.GONE
        }else{
            mBindingView.floatingLoginOutBtn.visibility = View.VISIBLE
        }
    }

    private fun showGuideDialogIfNeeded(){
        if (MMKVUtils.getBoolean(ConstantUtil.MINE_SETTING_GUIDE,false)){
            return
        }else{
            window?.decorView?.post {
                showGuideBackBtnDialog {
                    MMKVUtils.save(ConstantUtil.MINE_SETTING_GUIDE,true)
//                showGuideDoubleTapDialog {
//                    showGuidePreviewImageDialog {
//                        GuideSPUtils.getInstance().setArticleGuideShown()
//                    }
//                }
                }
            }
        }
    }

    private fun showGuideBackBtnDialog(onDismiss: () -> Unit) {
        GuideLayer(this@MineSettingActivity)
            .backgroundColorInt(resources.getColor(R.color.color_dialog_background))
            .mapping(GuideLayer.Mapping().apply {
                targetView(mBindingView.floatingLoginOutBtn)
                cornerRadius(9999F)
                guideView(LayoutInflater.from(this@MineSettingActivity)
                    .inflate(R.layout.dialog_setting_guide_tip, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_tip).apply {
                            text = "退出账号请点击这里哦~"
                        }
                    })
                marginLeft(16)
                horizontalAlign(GuideLayer.Align.Horizontal.TO_LEFT)
                verticalAlign(GuideLayer.Align.Vertical.CENTER)
            })
            .mapping(GuideLayer.Mapping().apply {
                val cx = window?.decorView?.width ?: 0 / 2
                val cy = window?.decorView?.height ?: 0 / 2
                targetRect(Rect(cx, cy, cx, cy))
                guideView(LayoutInflater.from(this@MineSettingActivity)
                    .inflate(R.layout.dialog_setting_guide_btn, null, false).apply {
                        findViewById<TextView>(R.id.dialog_guide_tv_btn).apply {
                            text = "我知道了"
                        }
                    })
                marginBottom(48)
                horizontalAlign(GuideLayer.Align.Horizontal.CENTER)
                verticalAlign(GuideLayer.Align.Vertical.CENTER)
                onClick(Layer.OnClickListener { layer, _ ->
                    layer.dismiss()
                }, R.id.dialog_guide_tv_btn)
            })
            .onVisibleChangeListener(object : Layer.OnVisibleChangeListener {
                override fun onShow(layer: Layer) {
                }

                override fun onDismiss(layer: Layer) {
                    onDismiss.invoke()
                }
            })
            .show()

    }
}