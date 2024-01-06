package com.ggb.nirvanahappyclub.module.community

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.HandlerCompat.postDelayed
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.ActivityWebContentBinding
import com.ggb.nirvanahappyclub.module.article.ArticleInfoViewModel
import com.ggb.nirvanahappyclub.webclient.WebClientFactory

import com.google.android.material.appbar.AppBarLayout
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.AgentWebConfig
import com.just.agentweb.NestedScrollAgentWebView
import com.just.agentweb.WebChromeClient
import com.tamsiree.rxkit.view.RxToast


class CommunityWebContentActivity : BaseActivity<ArticleInfoViewModel, ActivityWebContentBinding>(), OnItemSingleClickListener {

    private var mAgentWeb: AgentWeb? = null

    private var shareTitle: String = ""
    private var shareUrl: String = ""
    private var shareId: Int = -1

    companion object {

        fun start(context: Context?, id: String, title: String, url: String, bundle: Bundle? = null) {
            Intent(context, CommunityWebContentActivity::class.java).run {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("url", url)
                context?.startActivity(this, bundle)
            }
        }

        fun start(context: Context?, url: String) {
            start(context, "-1", "", url)
        }

    }

    override fun initView() {
//        ImmersionBar.with(this).transparentStatusBar().statusBarDarkFont(false).init()
        intent.extras?.let {
            shareId = it.getInt("id", -1)
            shareTitle = it.getString("title", "")
            shareUrl = it.getString("url", "")
        }
        mBindingView.communityToolbar.tbCommunityBar.apply{
            title = ""//getString(R.string.loading)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        mBindingView.communityToolbar.tvCommunityBarTitle.apply{
            text = getString(R.string.loading)
            visibility = View.VISIBLE
            postDelayed({
                mBindingView.communityToolbar.tvCommunityBarTitle.isSelected = true
            }, 2000)
        }


        initWebView()
    }

    override fun initData() {

    }

    /**
     * 初始化 WebView
     */
    private fun initWebView() {
        AgentWebConfig.debug()
        val webView = NestedScrollAgentWebView(this)

        val layoutParams = CoordinatorLayout.LayoutParams(-1, -1)
        layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()

        mAgentWeb = shareUrl.getAgentWeb(
            this,
            mBindingView.wcAndroidMain,
            layoutParams,
            webView,
            WebClientFactory.create(shareUrl),
            mWebChromeClient,
            R.color.main_color)


        mAgentWeb?.webCreator?.webView?.apply {
            overScrollMode = WebView.OVER_SCROLL_NEVER
            settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.loadsImagesAutomatically = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.blockNetworkImage = false

            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            mBindingView.communityToolbar.tvCommunityBarTitle.text = title
        }



//        override fun onPermissionRequest(request: PermissionRequest) {
//            request.grant(request.resources)
//        }

//        override fun onPermissionRequest(request: PermissionRequest) {
//            runOnUiThread { request.grant(request.resources) } // run
//            // MainActivity
//        } // onPermissionRequest

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_content, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                Intent().run {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(
                        R.string.share_article_url,
                        getString(R.string.app_name), shareTitle, shareUrl
                    ))
                    type = "text/plain"
                    startActivity(Intent.createChooser(this, getString(R.string.action_share)))
                }
                return true
            }
            R.id.action_like -> {
                RxToast.info(resources.getString(R.string.data_not_belong))
                return true
            }
            R.id.action_browser -> {
                Intent().run {
                    action = "android.intent.action.VIEW"
                    data = Uri.parse(shareUrl)
                    startActivity(this)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mAgentWeb?.let {
            if (!it.back()) {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (mAgentWeb?.handleKeyEvent(keyCode, event)!!) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }
}