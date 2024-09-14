package com.ggb.nirvanahappyclub.module.gpt.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.MainMenuBean
import com.ggb.nirvanahappyclub.databinding.ActivityNirvanaGptMainBinding
import com.ggb.nirvanahappyclub.module.gpt.viewmodel.NirvanaGPTMainViewModel
import com.gyf.immersionbar.ImmersionBar
import java.io.FileNotFoundException

class NirvanaGPTMainActivity : BaseActivity<NirvanaGPTMainViewModel, ActivityNirvanaGptMainBinding>() {



    override fun getSaveInstanceState(savedInstanceState: Bundle) {

    }

    override fun onSaveInstanceState(outState: Bundle) {

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

        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(true).titleBar(mBindingView.rlGptTitle).navigationBarColor(R.color.white).init()



        // 处理启动Intent
        val activityIntent = intent
        if (activityIntent != null) {
            val action = activityIntent.action
            if (Intent.ACTION_PROCESS_TEXT == action) { // 全局上下文菜单
                val text = activityIntent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                if (text != null) {
                    mBindingView.etUserInput.setText(text)
                }
            } else if (Intent.ACTION_SEND == action) { // 分享图片
                val type = activityIntent.type
                if (type != null && type.startsWith("image/")) {
//                    val imageUri =
//                        activityIntent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM) // 获取图片Uri
//                    if (imageUri != null) {
//                        try {
//                            // 获取图片Bitmap并缩放
//                            val bitmap =
//                                BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri)) as Bitmap
//                            selectedImageBitmap = bitmap
//                            if (GlobalDataHolder.getLimitVisionSize()) {
//                                if (bitmap.width < bitmap.height) selectedImageBitmap =
//                                    resizeBitmap(bitmap, 512, 2048) else selectedImageBitmap =
//                                    resizeBitmap(bitmap, 2048, 512)
//                            } else {
//                                selectedImageBitmap = resizeBitmap(bitmap, 2048, 2048)
//                            }
//                            btImage.setImageResource(R.drawable.image_enabled)
//                            if (!GlobalUtils.checkVisionSupport(GlobalDataHolder.getGptModel())) Toast.makeText(
//                                this,
//                                R.string.toast_use_vision_model,
//                                Toast.LENGTH_LONG
//                            ).show()
//                        } catch (e: FileNotFoundException) {
//                            e.printStackTrace()
//                        }
//                    }
                } else if (type == "text/plain") { // 分享文本
                    val text = activityIntent.getStringExtra(Intent.EXTRA_TEXT)
                    if (text != null) {
                        mBindingView.etUserInput.setText(text)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val index = intent.getIntExtra("index", -1)
        LogUtils.xswShowLog("走到这个--》onNewIntent")
    }

    override fun initView() {}
    override fun initData() {
        mBindingView.data = MainMenuBean()
        val intent = intent
        val exit = intent.getBooleanExtra("exit", false)
        if (exit) {
            finish()
        }

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

    override fun onResume() {
        super.onResume()

    }



    override fun onClick(view: View) {
        super.onClick(view)
        when (view.id) {
            R.id.ll_tools_home -> {

            }

        }
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        var mMainActivityContext: NirvanaGPTMainActivity? = null

        /**
         * 打开或者回到首页方法
         *
         * @param context
         * @param index   0首页  1我的
         */
        fun start(context: Context, index: Int) {
            if (isActivityNotFinished(context)) {
                val intent = Intent(context, NirvanaGPTMainActivity::class.java)
                intent.putExtra("index", index)
                context.startActivity(intent)
            }
        }

        fun start(context: Context, index: Int, clazz: Class<out Activity?>?) {
            if (isActivityNotFinished(context)) {
                val intent = Intent(context, NirvanaGPTMainActivity::class.java)
                intent.putExtra("index", index)
                intent.putExtra("isJump", 1)
                intent.putExtra("clazz", clazz)
                context.startActivity(intent)
            }
        }
    }
}