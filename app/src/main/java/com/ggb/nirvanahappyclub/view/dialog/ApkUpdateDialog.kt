package com.ggb.nirvanahappyclub.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ggb.common_library.utils.json.JsonUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.VersionBean
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate1Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdateBinding
import com.ggb.nirvanahappyclub.utils.AppUtils
import com.ggb.nirvanahappyclub.utils.ScreenUtils
import com.tamsiree.rxkit.view.RxToast


class ApkUpdateDialog : Dialog {

    private lateinit var info : VersionBean

    public var viewDataBinding: DialogNewApkUpdateBinding? = null
    private var viewData1Binding: DialogNewApkUpdate1Binding? = null

    private var uiStyle = 300

    constructor(context: Context) : super(context, R.style.CustomDialog)

    constructor(context: Context,style:Int ) : super(context, R.style.CustomDialog) {
        this.uiStyle = style
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(uiStyle){
            300 ->{
                val inflater = layoutInflater
                viewDataBinding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update, null, false
                )

                viewDataBinding?.root?.let { setContentView(it) }
            }
            301 ->{
                val inflater = layoutInflater
                viewData1Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_1, null, false
                )

                viewData1Binding?.root?.let { setContentView(it) }

            }
            306 ->{
                setContentView(R.layout.dialog_new_apk_update_6)
            }
            307 ->{
                setContentView(R.layout.dialog_new_apk_update_7)
            }
            308 ->{
                setContentView(R.layout.dialog_new_apk_update_8)
            }
            309 ->{
                setContentView(R.layout.dialog_new_apk_update_9)
            }
            310 ->{
                setContentView(R.layout.dialog_new_apk_update_10)
            }
        }

        initWindow()
        initEvent()
    }

    private fun initWindow(){
        val lp = window?.attributes
        lp?.width = (ScreenUtils.getScreenWidth(context)*0.9).toInt()
        lp?.height = (ScreenUtils.getScreenWidth(context)*1.3).toInt()
        window?.attributes = lp
    }

    fun showUpdate(info : VersionBean){
        show()
        this.info = info
        when(uiStyle){
            300 -> {
                viewDataBinding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewDataBinding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewDataBinding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewDataBinding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
            }
            301 -> {
                viewData1Binding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewData1Binding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewData1Binding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewData1Binding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
            }
        }

        setCanceledOnTouchOutside(info.isForce.toInt() != 1)
    }

    private fun initEvent(){
        when(uiStyle) {
            300 -> {
                viewDataBinding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewDataBinding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewDataBinding?.ivUpdateNewClose?.setOnClickListener {
                    RxToast.success(context.resources.getString(R.string.update_forbiten_1))
                    dismiss()
                }
            }
            301 -> {
                viewData1Binding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewData1Binding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewData1Binding?.ivUpdateNewClose?.setOnClickListener {
                    RxToast.success(context.resources.getString(R.string.update_forbiten_1))
                    dismiss()
                }
            }
        }

    }

    fun getDialogStyle(): Int {
        return uiStyle
    }

    fun getUpdateDialogBinding(): ViewDataBinding? {
        when(uiStyle) {
            300 -> {
                return viewDataBinding
            }
            301 -> {
                return viewData1Binding
            }
        }
        return viewDataBinding
    }

    interface OnApkDownloadConfirmListener{
        fun onConfirmDownload(info :VersionBean)
    }

    private var mOnApkDownloadConfirmListener:OnApkDownloadConfirmListener? = null

    fun setOnApkDownloadConfirmListener(mOnApkDownloadConfirmListener:OnApkDownloadConfirmListener){
        this.mOnApkDownloadConfirmListener = mOnApkDownloadConfirmListener
    }

}