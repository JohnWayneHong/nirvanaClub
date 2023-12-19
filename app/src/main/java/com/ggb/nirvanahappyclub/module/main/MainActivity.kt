package com.ggb.nirvanahappyclub.module.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.utils.CommonDialogUtil
import com.ggb.common_library.utils.FormatUtils
import com.ggb.common_library.utils.MMKVUtils
import com.ggb.common_library.utils.ToastUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.widget.commonDialog.CommonDialog
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.MainMenuBean
import com.ggb.nirvanahappyclub.databinding.ActivityMainBinding
import com.ggb.nirvanahappyclub.module.community.CommunityFragment
import com.ggb.nirvanahappyclub.module.index.IndexFragment
import com.ggb.nirvanahappyclub.module.message.MessageFragment
import com.ggb.nirvanahappyclub.module.mine.MineFragment
import com.ggb.nirvanahappyclub.module.subscribe.SubscribeFragment
import com.ggb.nirvanahappyclub.utils.ConstantUtil
import java.util.Date

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    private var lastSelectTab = -1
    private var tags: ArrayList<String> = ArrayList()
    private var fragments: ArrayList<Fragment> = ArrayList()

    private var mIndexFragment: IndexFragment = IndexFragment()
    private var mCommunityFragment: CommunityFragment = CommunityFragment()
    private var mSubscribeFragment: SubscribeFragment = SubscribeFragment()
    private var mMessageFragment: MessageFragment = MessageFragment()
    private var mMineFragment: MineFragment = MineFragment()

    private var exitTime: Long = 0

    override fun getSaveInstanceState(savedInstanceState: Bundle) {
        lastSelectTab = savedInstanceState.getInt("LastSelectTab")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("LastSelectTab", lastSelectTab)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mMainActivityContext = this
        val wm = this.getSystemService(WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels // 屏幕宽度（像素）
        val height = dm.heightPixels // 屏幕高度（像素）
        val density = dm.density // 屏幕密度（0.75 / 1.0 / 1.5）
        val densityDpi = dm.densityDpi // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        val screenWidth = (width / density).toInt() // 屏幕宽度(dp)
        val screenHeight = (height / density).toInt() // 屏幕高度(dp)
        Log.d("h_bl", "屏幕宽度（像素）：$width")
        Log.d("h_bl", "屏幕高度（像素）：$height")
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：$density")
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：$densityDpi")
        Log.d("h_bl", "屏幕宽度（dp）：$screenWidth")
        Log.d("h_bl", "屏幕高度（dp）：$screenHeight")
        super.onCreate(savedInstanceState)
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        val index = intent.getIntExtra("index", -1)
//        if (index != -1) {
//            jumpToFragment(index)
//        }
//        val exit = intent.getBooleanExtra("exit", false)
//        if (exit) {
//            finish()
//        }
//    }

    override fun initView() {}
    override fun initData() {
        mBindingView.data = MainMenuBean()
        val intent = intent
        val exit = intent.getBooleanExtra("exit", false)
        if (exit) {
            finish()
        }
        tags.add("mIndexFragment")
        tags.add("mCommunityFragment")
        tags.add("mSubscribeFragment")
        tags.add("mMessageFragment")
        tags.add("mMineFragment")

        fragments.add(mIndexFragment)
        fragments.add(mCommunityFragment)
        fragments.add(mSubscribeFragment)
        fragments.add(mMessageFragment)
        fragments.add(mMineFragment)


        val index = intent.getIntExtra("index", -1)
        if (index != -1) {
            jumpToFragment(index)
        } else {
            jumpToFragment(0)
        }
    }
    override fun initLiveData() {
        super.initLiveData()
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addOnClickListener()
            .addView(mBindingView.llToolsHome,mBindingView.llToolsCommunity,mBindingView.llToolsSubscribe,mBindingView.llToolsMessage,mBindingView.llToolsMine)
            .submit()
    }

    override fun onResume() {
        super.onResume()
//        CheckUpdateUtils.getVersionType(false, this)
    }

    private fun checkNeedChangePassword() {
        val i = MMKVUtils.getInt(ConstantUtil.NEED_CHANGE_PASSWORD, 0)
        val lastCheckDay = MMKVUtils.getString(ConstantUtil.TODAY_CHECK_NEED_CHANGE_PASSWORD)
        val today = FormatUtils.formatDate(Date(), FormatUtils.DAY_TIME_PATTERN)
        if (i != 1 || TextUtils.equals(lastCheckDay, today)) {
            return
        }
        CommonDialogUtil.showConfirmDialog(this, "提示", getString(R.string.strong_password_remind),
            "确定", object : CommonDialog.OnButtonClickListener {
                override fun onRightClick() {
                    MMKVUtils.save(ConstantUtil.TODAY_CHECK_NEED_CHANGE_PASSWORD, today)
                }
            })
    }

    //根据点击位置跳转不同Fragment
    private fun jumpToFragment(index: Int) {
        mBindingView.data?.setIndex(index)
        if (lastSelectTab != -1 && lastSelectTab != index) {
            val baseFragment = fragments[lastSelectTab]
            fm.beginTransaction().hide(baseFragment).commitAllowingStateLoss()
        }
        val tag = tags[index]
        if (!fragments[index].isAdded && null == fm.findFragmentByTag(tag)) {
            fm.beginTransaction().add(R.id.fl_main_content, fragments[index], tag)
                .addToBackStack(null).commitAllowingStateLoss()
        }
        fm.beginTransaction().show(fragments[index]).commitAllowingStateLoss()
        lastSelectTab = index
        MMKVUtils.save("index", index)

    }
    override fun onClick(view: View) {
        super.onClick(view)
        when (view.id) {
            R.id.ll_tools_home -> {
                jumpToFragment(0)
            }
            R.id.ll_tools_community -> {
                jumpToFragment(1)
            }
            R.id.ll_tools_subscribe -> {
                jumpToFragment(2)
            }
            R.id.ll_tools_message -> {
                jumpToFragment(3)
            }
            R.id.ll_tools_mine -> {
                jumpToFragment(4)
            }
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showToast("再按一次退出")
            exitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    companion object {
        var mMainActivityContext: MainActivity? = null

        /**
         * 打开或者回到首页方法
         *
         * @param context
         * @param index   0首页  1我的
         */
        fun start(context: Context, index: Int) {
            if (isActivityNotFinished(context)) {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("index", index)
                context.startActivity(intent)
            }
        }

        fun start(context: Context, index: Int, clazz: Class<out Activity?>?) {
            if (isActivityNotFinished(context)) {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("index", index)
                intent.putExtra("isJump", 1)
                intent.putExtra("clazz", clazz)
                context.startActivity(intent)
            }
        }
    }
}