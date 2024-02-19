package com.ggb.nirvanahappyclub.module.login

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.ggb.common_library.base.ui.BaseActivity
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.ToastUtils
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.common.MyApplication
import com.ggb.nirvanahappyclub.databinding.ActivityArticleBinding
import com.ggb.nirvanahappyclub.databinding.ActivityLoginBinding
import com.ggb.nirvanahappyclub.utils.RegularUtil
import com.gyf.immersionbar.ImmersionBar
import com.tamsiree.rxkit.view.RxToast

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>(), OnItemSingleClickListener {

    private var beforeFragment = ""

    /**
     * 登录方式，默认是手机验证码登录
     * 新版本尚未实现，暂时默认账户登录
     */
    private var loginType = LoginType.PWD
    private lateinit var codeSpannableString: SpannableString
    private lateinit var pwdSpannableString: SpannableString

    enum class LoginType {
        CODE, PWD
    }

    private var isSelectPrivacy = false
    private var isGetCode = false



    override fun initView() {
        initEvent()
        initVerifyView()
        initMustReadSpannableString()
        setMustReadText()
    }


    override fun initData() {
        beforeFragment = intent.getStringExtra("beforeFragment").toString()
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(mBindingView.titlePublicView.publicTitle).init()

    }


    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getLoginByPwdLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> showLoadingDialog()
                Resource.SUCCESS -> {
                    dismissLoadingDialog()

                }
                Resource.ERROR -> dismissLoadingDialog()
            }
        }
    }

    private fun initEvent() {

    }

    private fun initVerifyView() {
        mBindingView.userLoginVerifyBar.apply {
            draggable = mBindingView.userLoginVerifyDraggable
            actualDraggable = mBindingView.userLoginVerifyActualDraggable
        }
    }

    /**
     * 判断行为验证是否通过
     */
    private fun checkVerify():Boolean{
        mBindingView.userLoginVerifyBar.apply {
            return isVerifyPass()
        }
    }

    override fun initListener() {
        super.initListener()
        ClickUtils.register(this)
            .addView(mBindingView.userLoginBtn)
            .addView(mBindingView.tvUserLoginChangeType)
            .addView(mBindingView.ivUserLoginQq)
            .addView(mBindingView.ivUserLoginWx)
            .addView(mBindingView.ivUserLoginWb)
            .addView(mBindingView.llUserLoginMustRead)
            .addView(mBindingView.userLoginGetCode)
            .addView(mBindingView.userLoginProblem)
            .addView(mBindingView.userLoginRegister)
            .addView(mBindingView.titlePublicView.llPublicBack)
            .addOnClickListener()
            .submit()

    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.ll_public_back) {
            onBackPressed()
        }else if (id == R.id.tv_user_login_change_type) {
            RxToast.info("牛蛙呐系统升级中，暂时无法使用手机登录，请谅解~")

//            loginType = if (loginType == LoginType.CODE) LoginType.PWD else LoginType.CODE
//            when(loginType){
//                LoginType.CODE->{
//                    rl_login_account.visibility = View.GONE
//                    rl_user_login_pwd.visibility = View.GONE
//                    rl_login_mobile.visibility = View.VISIBLE
//                    rl_login_mobile_code.visibility = View.VISIBLE
//                    tv_user_login_change_type.text = resources.getString(R.string.me_user_login_title_pwd_string)
//                }
//                LoginType.PWD->{
//                    rl_login_account.visibility = View.VISIBLE
//                    rl_user_login_pwd.visibility = View.VISIBLE
//                    rl_login_mobile.visibility = View.GONE
//                    rl_login_mobile_code.visibility = View.GONE
//                    tv_user_login_change_type.text = resources.getString(R.string.me_user_login_title_code_string)
//                }
//            }
            setMustReadText()
        }else if (id == R.id.iv_user_login_qq) {
            RxToast.warning("系统维护升级，暂时无法使用QQ登录")
            return

//            if (!isSelectPrivacy){
//                ToastUtils.showToast(resources.getString(R.string.user_login_mustread_popup_string))
//                return
//            }
//            if (!App.mTencent.isSessionValid) {
//                when (App.mTencent.login(this, "all",iu)) {
//                    0 -> "正常登录".showToast()
//                    1 -> "正在跳转QQ登录".showToast()
//                    -1 -> {
//                        "异常".showToast()
//                        App.mTencent.logout(App.context)
//                    }
//                    2 -> "使用H5登陆或显示下载页面".showToast()
//                    else -> "出错".showToast()
//                }
//            }
        }else if (id == R.id.iv_user_login_wx) {
            RxToast.warning("Sorry, - -! 微信登录暂未开发")
            return
        }else if (id == R.id.iv_user_login_wb) {
            RxToast.warning("Sorry, - -! 微博登录暂未开发")
            return
        }else if (id == R.id.tv_user_login_must_read) {
            //隐私政策
        }else if (id == R.id.ll_user_login_must_read) {
            mBindingView.userLoginMustReadCheckbox.isChecked = !mBindingView.userLoginMustReadCheckbox.isChecked
            isSelectPrivacy = !isSelectPrivacy
        }else if (id == R.id.user_login_get_code) {
            if (!checkVerify()){
                ToastUtils.showToast(resources.getString(R.string.verify_error))
                return
            }
            if (!isGetCode){
                if (!isSelectPrivacy){
                    ToastUtils.showToast(resources.getString(R.string.user_login_mustread_popup_string))
                    return
                }
                if (mBindingView.etUserLoginMobile.text.isEmpty()){
                    ToastUtils.showToast(resources.getString(R.string.me_user_login_account_code_hint_string))
                    return
                }
                if(!RegularUtil.isChinaPhoneLegal(mBindingView.etUserLoginMobile.text.toString())){
                    ToastUtils.showToast(resources.getString(R.string.mobile_error))
                    return
                }
                //老版本发送已失效
//                present.sendCode(et_user_login_mobile.text.toString())
            }
        }else if (id == R.id.user_login_btn) {
            if (checkVerify()&&isSelectPrivacy){
                when(loginType){
                    LoginType.CODE -> {
                        if (mBindingView.etUserLoginMobile.text.isEmpty()){
                            ToastUtils.showToast(resources.getString(R.string.me_user_login_account_code_hint_string))
                            return
                        }
                        if (mBindingView.userLoginCode.text.isEmpty()){
                            ToastUtils.showToast(resources.getString(R.string.me_user_login_code_hint_string))
                            return
                        }
                        if (!RegularUtil.isChinaPhoneLegal(mBindingView.etUserLoginMobile.text.toString())){
                            ToastUtils.showToast(resources.getString(R.string.mobile_error))
                            return
                        }
                        //后续加接口
//                        present.login(et_user_login_mobile.text.toString(),user_login_code.text.toString())
                    }
                    LoginType.PWD -> {
                        if (mBindingView.etUserLoginAccount.text.isEmpty()){
                            ToastUtils.showToast(resources.getString(R.string.me_user_login_account_pwd_hint_string))
                            return
                        }
                        if (mBindingView.userLoginPwd.text.isEmpty()){
                            ToastUtils.showToast(resources.getString(R.string.me_user_login_pwd_hint_string))
                            return
                        }
                        if (!RegularUtil.isEmailLegal(mBindingView.etUserLoginAccount.text.toString())&&!RegularUtil.isChinaPhoneLegal(mBindingView.etUserLoginAccount.text.toString())){
                            ToastUtils.showToast(resources.getString(R.string.account_error))
                            return
                        }
                        //前往 账号密码登录
                        mViewModel.loginByPwd(mBindingView.etUserLoginAccount.text.toString(),mBindingView.userLoginPwd.text.toString())
                    }
                }
            }else{
                ToastUtils.showToast(resources.getString(R.string.verify_error_2))
                return
            }
        }else if (id == R.id.user_login_problem) {
            RxToast.success("我没有问题")
        }else if (id == R.id.user_login_register) {
            //前往注册页面
        }
    }

    /**
     * 用户须知打勾的文字设置
     */
    private fun setMustReadText(){
        mBindingView.tvUserLoginMustRead.apply {
            text = when(loginType){
                LoginType.CODE -> codeSpannableString
                LoginType.PWD -> pwdSpannableString
            }
        }
    }

    /**
     * 初始化用户须知打勾的 SpannableString
     */
    private fun initMustReadSpannableString() {
        pwdSpannableString = SpannableString("我已阅读并同意《用户协议》和《隐私政策》").apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.gray_text_color)), 0, 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 7, 13, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.primary)), 7, 13, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.gray_text_color)), 13, 14, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 14, 20, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.primary)), 14, 20, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        codeSpannableString = SpannableString("我已阅读并同意《用户协议》和《隐私政策》，未注册手机号登录时将自动注册").apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.gray_text_color)), 0, 7, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 7, 13, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.primary)), 7, 13, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.gray_text_color)), 13, 14, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 14, 20, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.primary)), 14, 20, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(MyApplication.context, R.color.gray_text_color)), 20, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    companion object {
        fun start(context: Context, beforeFragment: String) {
            if (isActivityNotFinished(context)) {
                val intent = Intent(context, LoginActivity::class.java)
                intent.putExtra("beforeFragment", beforeFragment)
                context.startActivity(intent)
            }
        }
    }
}