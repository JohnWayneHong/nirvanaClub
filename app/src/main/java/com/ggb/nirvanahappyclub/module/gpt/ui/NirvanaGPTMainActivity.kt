package com.ggb.nirvanahappyclub.module.gpt.ui

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import cn.hutool.json.JSONObject
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.utils.LogUtils
import com.ggb.common_library.utils.ToastUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.MainMenuBean
import com.ggb.nirvanahappyclub.databinding.ActivityNirvanaGptMainBinding
import com.ggb.nirvanahappyclub.module.gpt.ChatApiClient
import com.ggb.nirvanahappyclub.module.gpt.ChatManager
import com.ggb.nirvanahappyclub.module.gpt.ChatManager.ChatMessage
import com.ggb.nirvanahappyclub.module.gpt.ChatManager.ChatMessage.ChatRole
import com.ggb.nirvanahappyclub.module.gpt.ChatManager.Conversation
import com.ggb.nirvanahappyclub.module.gpt.ChatManager.MessageList
import com.ggb.nirvanahappyclub.module.gpt.GlobalDataHolder
import com.ggb.nirvanahappyclub.module.gpt.GlobalUtils
import com.ggb.nirvanahappyclub.module.gpt.GlobalUtils.base64ToBitmap
import com.ggb.nirvanahappyclub.module.gpt.GlobalUtils.dpToPx
import com.ggb.nirvanahappyclub.module.gpt.GlobalUtils.resizeBitmap
import com.ggb.nirvanahappyclub.module.gpt.MarkdownRenderer
import com.ggb.nirvanahappyclub.module.gpt.PromptTabData
import com.ggb.nirvanahappyclub.module.gpt.viewmodel.NirvanaGPTMainViewModel
import com.gyf.immersionbar.ImmersionBar
import io.noties.prism4j.annotations.PrismBundle
import java.io.ByteArrayOutputStream

@PrismBundle(includeAll = true)
class NirvanaGPTMainActivity : BaseActivity<NirvanaGPTMainViewModel, ActivityNirvanaGptMainBinding>() {

    private var chatManager: ChatManager? = null

    private var chatApiClient: ChatApiClient? = null

    private var chatApiBuffer = ""

    private var markdownRenderer: MarkdownRenderer? = null

    private var selectedImageBitmap: Bitmap? = null

//    private val multiChatList: MessageList? = null // 指向currentConversation.messages
    private val multiChatList: MessageList = MessageList() // 指向currentConversation.messages

    private var selectedTab = 0
    private var ttsSentenceEndIndex = 0
    private val currentConversation: Conversation? = null // 当前会话信息


    private var currentTemplateParams: JSONObject? = null // 当前模板参数

    private val multiChat = false

    private var handler: Handler? = null


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



        handler = Handler() // 初始化Handler
        GlobalDataHolder.init(this) // 初始化全局共享数据
        markdownRenderer = MarkdownRenderer(this) // 初始化Markdown渲染器

        initGPTClient()
        switchToTemplate(0)
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
            .addView(mBindingView.btSend)
            .submit()
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onClick(view: View) {
        super.onClick(view)
        when (view) {
            mBindingView.btSend -> {
                if (chatApiClient!!.isStreaming) {
                    chatApiClient!!.stop()
                }
                //TODO 网页抓取和语音暂停
//                else if (webScraper.isLoading()) {
//                    webScraper.stopLoading()
//                    if (tvGptReply != null) tvGptReply.setText(R.string.text_cancel_web)
//                    btSend.setImageResource(R.drawable.send_btn)
//                }
                else {
//                    tts.stop()
                    sendQuestion(null)
                    mBindingView.etUserInput.setText("")
                }
            }

        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun initGPTClient() {
        chatManager = ChatManager(this) // 初始化聊天记录管理器
        ChatMessage.setContext(this) // 设置聊天消息的上下文（用于读写文件）

        // 初始化GPT客户端
        chatApiClient = ChatApiClient(this,
            GlobalDataHolder.getGptApiHost(),
            GlobalDataHolder.getGptApiKey(),
            GlobalDataHolder.getGptModel(),
            object : ChatApiClient.OnReceiveListener {
                private var lastRenderTime: Long = 0
                override fun onMsgReceive(message: String) { // 收到GPT回复（增量）
                    chatApiBuffer += message
                    handler!!.post {
                        if (System.currentTimeMillis() - lastRenderTime > 100) { // 限制最高渲染频率10Hz
                            val isBottom: Boolean = (mBindingView.svChatList.getChildAt(0).getBottom()
                                    <= mBindingView.svChatList.getHeight() + mBindingView.svChatList.getScrollY()) // 判断消息布局是否在底部
                            markdownRenderer!!.render(mBindingView.tvChatNotice, chatApiBuffer) // 渲染Markdown
                            if (isBottom) {
                                scrollChatAreaToBottom() // 渲染前在底部则渲染后滚动到底部
                            }
                            lastRenderTime = System.currentTimeMillis()
                        }
                        //TODO 忽略语音处理
                    }
                }

                override fun onFinished(completed: Boolean) { // GPT回复完成
                    // GPT回复完成
                    handler!!.post {
                        var referenceStr = "\n\n" + getString(R.string.text_ref_web_prefix)

                        var referenceCount = 0
                        if (completed) { // 如果是完整回复则添加参考网页
                            var questionIndex: Int = multiChatList!!.size - 1
                            while (questionIndex >= 0 && multiChatList[questionIndex].role !== ChatRole.USER) { // 找到上一个提问消息
                                questionIndex--
                            }
                            for (i in questionIndex + 1 until multiChatList.size) { // 依次检查函数调用，并获取网页URL
                                if (multiChatList[i].role === ChatRole.FUNCTION && multiChatList[i - 1].role === ChatRole.ASSISTANT && multiChatList[i - 1].functionName != null
                                ) {
                                    val funcName = multiChatList[i - 1].functionName
                                    val funcArgs = multiChatList[i - 1].contentText
                                    if (funcName == "get_html_text") {
                                        val url = JSONObject(funcArgs).getStr("url")
                                        referenceStr += String.format(
                                            "[[%s]](%s) ",
                                            ++referenceCount,
                                            url
                                        )
                                    }
                                }
                            }
                        }
                        try {
                            markdownRenderer!!.render(mBindingView.tvChatNotice, chatApiBuffer) // 渲染Markdown
                            //TODO 暂时语音处理
//                            val ttsText: String = mBindingView.tvChatNotice.getText().toString()
//                            if (currentTemplateParams!!.getBool("speak", ttsEnabled) && ttsText.length > ttsSentenceEndIndex) { // 如果TTS开启则朗读剩余文本
//                                val id = UUID.randomUUID().toString()
//                                tts.speak(
//                                    ttsText.substring(ttsSentenceEndIndex),
//                                    TextToSpeech.QUEUE_ADD,
//                                    null,
//                                    id
//                                )
//                                ttsLastId = id
//                            }
                            if (referenceCount > 0) chatApiBuffer += referenceStr // 添加参考网页
                            multiChatList!!.add(
                                ChatMessage(ChatRole.ASSISTANT).setText(chatApiBuffer)
                            ) // 保存回复内容到聊天数据列表
                            (mBindingView.tvChatNotice.getParent() as LinearLayout).tag =
                                multiChatList[multiChatList.size - 1] // 绑定该聊天数据到布局
                            markdownRenderer!!.render(mBindingView.tvChatNotice, chatApiBuffer) // 再次渲染Markdown添加参考网页
                            mBindingView.btSend.setImageResource(R.drawable.send_btn)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(message: String?) {
                    handler!!.post {
                        val errText = String.format(
                            getString(R.string.text_gpt_error_prefix) + "%s",
                            message
                        )
                        if (mBindingView.tvChatNotice != null) {
                            mBindingView.tvChatNotice.setText(errText)
                        } else {
                            ToastUtils.showToast(errText)
                        }
                        mBindingView.btSend.setImageResource(R.drawable.send_btn)
                    }
                }

                override fun onFunctionCall(name: String, arg: String?) { // 收到函数调用请求

                }
            })
    }


    // 切换到指定的模板
    private fun switchToTemplate(tabIndex: Int) {
        selectedTab = tabIndex
        if (GlobalDataHolder.getSelectedTab() != -1) {
            GlobalDataHolder.saveSelectedTab(selectedTab)
        }
        currentTemplateParams = GlobalDataHolder.getTabDataList()[selectedTab].parseParams()
        LogUtils.xswShowLog("MainActivity switch template: params=$currentTemplateParams")
        chatApiClient!!.setModel(currentTemplateParams!!.getStr("model", GlobalDataHolder.getGptModel()))
        setNetworkEnabled(currentTemplateParams!!.getBool("network", GlobalDataHolder.getEnableInternetAccess()))
        //TODO 模板操作 暂停
//        updateTabListView()
//        updateTemplateParamsView()
//        updateImageButtonVisible()
    }

    // 设置是否允许GPT联网
    private fun setNetworkEnabled(enabled: Boolean) {
        if (enabled) {
            chatApiClient!!.addFunction(
                "get_html_text",
                "get all innerText and links of a web page",
                "{url: {type: string, description: html url}}",
                arrayOf("url")
            )
        } else {
            chatApiClient!!.removeFunction("get_html_text")
        }
    }


    // 添加一条聊天记录到聊天列表布局
    private fun addChatView(role: ChatRole, content: String, imageBase64: String?): LinearLayout {
        val iconParams = MarginLayoutParams(dpToPx(this,30), dpToPx(this,30)) // 头像布局参数
        iconParams.setMargins(dpToPx(this,4), dpToPx(this,12), dpToPx(this,4), dpToPx(this,12))
        val contentParams = MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ) // 内容布局参数
        contentParams.setMargins(dpToPx(this,4), dpToPx(this,15), dpToPx(this,4), dpToPx(this,15))
        val popupIconParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(dpToPx(this,30), dpToPx(this,30)) // 弹出的操作按钮布局参数
        popupIconParams.setMargins(dpToPx(this,5), dpToPx(this,5), dpToPx(this,5), dpToPx(this,5))
        val llOuter = LinearLayout(this) // 包围整条聊天记录的最外层布局
        llOuter.orientation = LinearLayout.HORIZONTAL
        if (role === ChatRole.ASSISTANT) // 不同角色使用不同背景颜色
            llOuter.setBackgroundColor(Color.parseColor("#0A000000"))
        val ivIcon = ImageView(this) // 设置头像
        if (role === ChatRole.USER) ivIcon.setImageResource(R.drawable.chat_user_icon) else ivIcon.setImageResource(
            R.drawable.chat_gpt_icon
        )
        ivIcon.layoutParams = iconParams
        val tvContent = TextView(this) // 设置内容
        var spannableString: SpannableString? = null
        if (role === ChatRole.USER) {
            if (imageBase64 != null) { // 如有图片则在末尾添加ImageSpan
                spannableString = SpannableString("$content\n ")
                var bitmap: Bitmap? = base64ToBitmap(imageBase64)
                val maxSize: Int = dpToPx(this,120)
                bitmap = resizeBitmap(bitmap, maxSize, maxSize)
                val imageSpan = ImageSpan(this, bitmap!!)
                spannableString.setSpan(
                    imageSpan,
                    content.length + 1,
                    content.length + 2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString = SpannableString(content)
            }
            tvContent.text = spannableString
        } else if (role === ChatRole.ASSISTANT) {
            markdownRenderer!!.render(tvContent, content)
        }
        tvContent.textSize = 16f
        tvContent.setTextColor(Color.BLACK)
        tvContent.layoutParams = contentParams
        tvContent.setTextIsSelectable(true)
        tvContent.movementMethod = LinkMovementMethod.getInstance()
        val llPopup = LinearLayout(this) // 弹出按钮列表布局
        llPopup.layoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        val popupBackground = PaintDrawable(Color.TRANSPARENT)
        llPopup.background = popupBackground
        llPopup.orientation = LinearLayout.HORIZONTAL
        val popupWindow = PopupWindow(
            llPopup,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ) // 弹出窗口
        popupWindow.isOutsideTouchable = true
        ivIcon.tag = popupWindow // 将弹出窗口绑定到头像上
        val cvDelete = CardView(this) // 删除单条对话按钮
        cvDelete.foreground = getDrawable(R.drawable.clear_btn)
        cvDelete.setOnClickListener { view: View? ->
            popupWindow.dismiss()
            val chat = llOuter.tag as ChatMessage // 获取布局上绑定的聊天记录数据
            if (chat != null) {
                var index: Int = multiChatList!!.indexOf(chat)
                multiChatList.remove(chat)
                while (--index > 0 && (multiChatList.get(index).role === ChatRole.FUNCTION
                            || multiChatList.get(index).functionName != null && multiChatList.get(
                        index
                    ).functionName.equals(
                        "get_html_text"
                    ))
                ) // 将上方联网数据也删除
                    multiChatList.removeAt(index)
            }
            if (tvContent === mBindingView.tvChatNotice) { // 删除的是GPT正在回复的消息框，停止回复和TTS
                if (chatApiClient!!.isStreaming) chatApiClient!!.stop()
                //TODO 语音暂停
//                tts.stop()
            }
            mBindingView.llChatList.removeView(llOuter)
            if (mBindingView.llChatList.getChildCount() == 0) // 如果删除后聊天列表为空，则添加占位TextView
                clearChatListView()
        }
        llPopup.addView(cvDelete)
        val cvDelBelow = CardView(this) // 删除下方所有对话按钮
        cvDelBelow.foreground = getDrawable(R.drawable.del_below_btn)
        cvDelBelow.setOnClickListener { view: View? ->
            popupWindow.dismiss()
            val index: Int = mBindingView.llChatList.indexOfChild(llOuter)
            while (mBindingView.llChatList.getChildCount() > index && mBindingView.llChatList.getChildAt(0) is LinearLayout) { // 模拟点击各条记录的删除按钮
                val pw =
                    (mBindingView.llChatList.getChildAt(mBindingView.llChatList.getChildCount() - 1) as LinearLayout).getChildAt(
                        0
                    )
                        .tag as PopupWindow
                (pw.contentView as LinearLayout).getChildAt(0).performClick()
            }
        }
        llPopup.addView(cvDelBelow)
        if (role === ChatRole.USER) { // USER角色才有的按钮
            val cvEdit = CardView(this) // 编辑按钮
            cvEdit.foreground = getDrawable(R.drawable.edit_btn)
            cvEdit.setOnClickListener { view: View? ->
                popupWindow.dismiss()
                val chat = llOuter.tag as ChatMessage // 获取布局上绑定的聊天记录数据
                var text = chat.contentText
                if (chat.contentImageBase64 != null) { // 若含有图片则设置为选中的图片
                    if (text.endsWith("\n ")) text = text.substring(0, text.length - 2)
                    selectedImageBitmap = base64ToBitmap(chat.contentImageBase64)
                    mBindingView.btImage.setImageResource(R.drawable.image_enabled)
                } else {
                    selectedImageBitmap = null
                    mBindingView.btImage.setImageResource(R.drawable.image)
                }
                mBindingView.etUserInput.setText(text) // 添加文本内容到输入框
                cvDelBelow.performClick() // 删除下方所有对话
            }
            llPopup.addView(cvEdit)
            val cvRetry = CardView(this) // 重试按钮
            cvRetry.foreground = getDrawable(R.drawable.retry_btn)
            cvRetry.setOnClickListener { view: View? ->
                popupWindow.dismiss()
                val chat = llOuter.tag as ChatMessage // 获取布局上绑定的聊天记录数据
                var text = chat.contentText
                if (chat.contentImageBase64 != null) { // 若含有图片则设置为选中的图片
                    if (text.endsWith("\n ")) text = text.substring(0, text.length - 2)
                    selectedImageBitmap = base64ToBitmap(chat.contentImageBase64)
                } else {
                    selectedImageBitmap = null
                }
                cvDelBelow.performClick() // 删除下方所有对话
                sendQuestion(text) // 重新发送问题
            }
            llPopup.addView(cvRetry)
        }
        val cvCopy = CardView(this) // 复制按钮
        cvCopy.foreground = getDrawable(R.drawable.copy_btn)
        cvCopy.setOnClickListener { view: View? ->  // 复制文本内容到剪贴板
            popupWindow.dismiss()
            val clipboard =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("chat", tvContent.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.toast_clipboard, Toast.LENGTH_SHORT).show()
        }
        llPopup.addView(cvCopy)
        for (i in 0 until llPopup.childCount) { // 设置弹出按钮的样式
            val cvBtn = llPopup.getChildAt(i) as CardView
            cvBtn.layoutParams = popupIconParams
            cvBtn.setCardBackgroundColor(Color.WHITE)
            cvBtn.radius = GlobalUtils.dpToPx(this,5).toFloat()
        }
        ivIcon.setOnClickListener { view: View? ->  // 点击头像时弹出操作按钮
            popupWindow.showAsDropDown(view, GlobalUtils.dpToPx(this,30), -GlobalUtils.dpToPx(this,35))
        }
        llOuter.addView(ivIcon)
        llOuter.addView(tvContent)
        mBindingView.llChatList.addView(llOuter)
        return llOuter
    }

    // 发送一个提问，input为null时则从输入框获取
    private fun sendQuestion(input: String?) {
        val isMultiChat: Boolean = currentTemplateParams!!.getBool("chat", multiChat)
        if (!isMultiChat) { // 若为单次对话模式则新建一个聊天
//            (findViewById<View>(R.id.cv_new_chat) as CardView).performClick()
        }



        // 处理提问文本内容
        val userInput = input ?: mBindingView.etUserInput.getText().toString()
        if (multiChatList!!.size == 0 && input == null) { // 由用户输入触发的第一次对话需要添加模板内容
            val tabData: PromptTabData = GlobalDataHolder.getTabDataList()[selectedTab]
            var template: String = tabData.getFormattedPrompt(getTemplateParamsFromView())
            if (currentTemplateParams!!.getBool("system", false)) {
                multiChatList.add(ChatMessage(ChatRole.SYSTEM).setText(template))
                multiChatList.add(ChatMessage(ChatRole.USER).setText(userInput))
            } else {
                if (!template.contains("%input%") && !template.contains("\${input}")) template += "\${input}"
                val question =
                    template.replace("%input%", userInput).replace("\${input}", userInput)
                multiChatList.add(ChatMessage(ChatRole.USER).setText(question))
            }
            currentConversation?.title = java.lang.String.format(
                "%s%s%s",
                tabData.getTitle(),
                if (!tabData.getTitle().isEmpty() && !userInput.isEmpty()) " | " else "",
                userInput.substring(0, Math.min(100, userInput.length)).replace("\n".toRegex(), " ")
            ) // 保存对话标题
        } else {
            multiChatList.add(ChatMessage(ChatRole.USER).setText(userInput))
        }
        if (selectedImageBitmap != null) { // 若有选中的图片则添加到聊天记录数据中
            val baos = ByteArrayOutputStream()
            selectedImageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val bytes = baos.toByteArray()
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            multiChatList[multiChatList.size - 1].setImage(base64)
        }
        if (mBindingView.llChatList.childCount > 0 && mBindingView.llChatList.getChildAt(0) is TextView) { // 若有占位TextView则删除
            mBindingView.llChatList.removeViewAt(0)
        }
        if (isMultiChat && mBindingView.llChatList.childCount > 0) { // 连续对话模式下，将第一条提问改写为添加模板后的内容
            val llFirst = mBindingView.llChatList.getChildAt(0) as LinearLayout
            val tvFirst = llFirst.getChildAt(1) as TextView
            val firstChat = llFirst.tag as ChatMessage
            if (firstChat.role === ChatRole.USER) {
                if (firstChat.contentImageBase64 != null && tvFirst.text.toString()
                        .endsWith("\n ")
                ) { // 若有附加图片则也要一并添加
                    val oldText = tvFirst.text as SpannableString
                    val imgSpan = oldText.getSpans(
                        oldText.length - 1, oldText.length,
                        ImageSpan::class.java
                    )[0]
                    val newText = SpannableString(firstChat.contentText + "\n")

                    newText.setSpan(
                        imgSpan,
                        newText.length - 1,
                        newText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvFirst.text = newText
                } else {
                    tvFirst.text = firstChat.contentText
                }
            }
        }
        if (GlobalDataHolder.getOnlyLatestWebResult()) { // 若设置为仅保留最新网页数据，删除之前的所有网页数据
            var i = 0
            while (i < multiChatList.size) {
                val chatItem = multiChatList[i]
                if (chatItem.role === ChatRole.FUNCTION) {
                    multiChatList.removeAt(i)
                    i--
                    if (i > 0 && multiChatList[i].role === ChatRole.ASSISTANT) { // 也要删除调用Function的Assistant记录
                        multiChatList.removeAt(i)
                        i--
                    }
                }
                i++
            }
        }

        // 添加对话布局
        val llInput = addChatView(
            ChatRole.USER,
            if (isMultiChat) multiChatList[multiChatList.size - 1].contentText else userInput,
            multiChatList[multiChatList.size - 1].contentImageBase64
        )




        val llReply = addChatView(ChatRole.ASSISTANT, getString(R.string.text_waiting_reply), null)
        llInput.tag = multiChatList[multiChatList.size - 1] // 将对话数据绑定到布局上

        //TODO 有问题 暂定测试
        mBindingView.tvChatNotice.text = (llReply.getChildAt(1) as TextView).text
        var tvChatNotice = mBindingView.tvChatNotice
        tvChatNotice = llReply.getChildAt(1) as TextView


        scrollChatAreaToBottom()
        chatApiBuffer = ""
        ttsSentenceEndIndex = 0
        chatApiClient!!.sendPromptList(multiChatList)
        //        markdownRenderer.render(tvGptReply, etUserInput.getText().toString());
        mBindingView.btImage.setImageResource(R.drawable.image)
        selectedImageBitmap = null
        mBindingView.btSend.setImageResource(R.drawable.cancel_btn)
    }

    // 滚动聊天列表到底部
    private fun scrollChatAreaToBottom() {
        mBindingView.svChatList.post(Runnable {
            val delta: Int = (mBindingView.svChatList.getChildAt(0).getBottom()
                    - (mBindingView.svChatList.getHeight() + mBindingView.svChatList.getScrollY()))
            if (delta != 0) mBindingView.svChatList.smoothScrollBy(0, delta)
        })
    }


    // 清空聊天界面
    private fun clearChatListView() {
        if (chatApiClient!!.isStreaming) {
            chatApiClient!!.stop()
        }
        mBindingView.llChatList.removeAllViews()
        //TODO 语音暂停
//        tts.stop()
        val tv = mBindingView.tvChatNotice // 清空列表后添加一个占位TextView
        tv.setTextColor(Color.parseColor("#000000"))
        tv.textSize = 16f
        tv.setPadding(dpToPx(this,10), dpToPx(this,10), dpToPx(this,10), dpToPx(this,10))
        tv.setText(R.string.default_greeting)

        mBindingView.llChatList.addView(tv)
    }

    // 从界面上获取模板参数
    private fun getTemplateParamsFromView(): JSONObject? {
        val params = JSONObject()
//        val llParams = findViewById<LinearLayout>(R.id.ll_template_params)
//        for (i in 0 until llParams.childCount) {
//            val llOuter = llParams.getChildAt(i) as LinearLayout
//            val inputKey = llOuter.tag as String
//            if (llOuter.getChildAt(1) is EditText) {
//                val et = llOuter.getChildAt(1) as EditText
//                params.putOpt(inputKey, et.text.toString())
//            } else if (llOuter.getChildAt(1) is Spinner) {
//                val sp = llOuter.getChildAt(1) as Spinner
//                params.putOpt(inputKey, sp.selectedItem)
//            }
//        }
        return params
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