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
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate10Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate1Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate6Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate7Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate8Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdate9Binding
import com.ggb.nirvanahappyclub.databinding.DialogNewApkUpdateBinding
import com.ggb.nirvanahappyclub.utils.AppUtils
import com.ggb.nirvanahappyclub.utils.ScreenUtils
import com.tamsiree.rxkit.view.RxToast


class ApkUpdateDialog : Dialog {

    private lateinit var info : VersionBean

    private var viewDataBinding: DialogNewApkUpdateBinding? = null
    private var viewData1Binding: DialogNewApkUpdate1Binding? = null
    private var viewData6Binding: DialogNewApkUpdate6Binding? = null
    private var viewData7Binding: DialogNewApkUpdate7Binding? = null
    private var viewData8Binding: DialogNewApkUpdate8Binding? = null
    private var viewData9Binding: DialogNewApkUpdate9Binding? = null
    private var viewData10Binding: DialogNewApkUpdate10Binding? = null

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
                viewData6Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_1, null, false
                )

                viewData6Binding?.root?.let { setContentView(it) }

            }
            306 ->{
                val inflater = layoutInflater
                viewData1Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_6, null, false
                )

                viewData1Binding?.root?.let { setContentView(it) }
            }
            307 ->{
                val inflater = layoutInflater
                viewData7Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_7, null, false
                )

                viewData7Binding?.root?.let { setContentView(it) }
            }
            308 ->{
                val inflater = layoutInflater
                viewData8Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_8, null, false
                )

                viewData8Binding?.root?.let { setContentView(it) }
            }
            309 ->{
                val inflater = layoutInflater
                viewData9Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_9, null, false
                )

                viewData9Binding?.root?.let { setContentView(it) }
            }
            310 ->{
                val inflater = layoutInflater
                viewData10Binding = DataBindingUtil.inflate(
                    inflater, R.layout.dialog_new_apk_update_10, null, false
                )

                viewData10Binding?.root?.let { setContentView(it) }
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
            306 -> {
                viewData6Binding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewData6Binding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewData6Binding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewData6Binding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
            }
            307 -> {
                viewData7Binding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewData7Binding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewData7Binding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewData7Binding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
            }
            308 -> {
                viewData8Binding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewData8Binding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewData8Binding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewData8Binding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
            }
            309 -> {
                viewData9Binding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewData9Binding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewData9Binding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewData9Binding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
            }
            310 -> {
                viewData10Binding?.tvUpdateNewVersion?.text = AppUtils.getVersionName(context)+".${info.versionCode}"
                viewData10Binding?.tvUpdateNewContent?.text = JsonUtils.parseObject(info.message)?.getString("message")
                viewData10Binding?.tvUpdateNewContent?.movementMethod = ScrollingMovementMethod.getInstance()
                viewData10Binding?.ivUpdateNewClose?.visibility = if(info.isForce.toInt() != 1) View.VISIBLE else View.GONE
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
            306 -> {
                viewData6Binding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewData6Binding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewData6Binding?.ivUpdateNewClose?.setOnClickListener {
                    RxToast.success(context.resources.getString(R.string.update_forbiten_1))
                    dismiss()
                }
            }
            307 -> {
                viewData7Binding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewData7Binding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewData7Binding?.ivUpdateNewClose?.setOnClickListener {
                    RxToast.success(context.resources.getString(R.string.update_forbiten_1))
                    dismiss()
                }
            }
            308 -> {
                viewData8Binding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewData8Binding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewData8Binding?.ivUpdateNewClose?.setOnClickListener {
                    RxToast.success(context.resources.getString(R.string.update_forbiten_1))
                    dismiss()
                }
            }
            309 -> {
                viewData9Binding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewData9Binding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewData9Binding?.ivUpdateNewClose?.setOnClickListener {
                    RxToast.success(context.resources.getString(R.string.update_forbiten_1))
                    dismiss()
                }
            }
            310 -> {
                viewData10Binding?.tvUpdateNewConfirm?.setOnClickListener {
                    mOnApkDownloadConfirmListener?.onConfirmDownload(info)
                }
                viewData10Binding?.tvUpdateNewCancel?.setOnClickListener {
                    RxToast.warning(context.resources.getString(R.string.update_forbiten))
                }
                viewData10Binding?.ivUpdateNewClose?.setOnClickListener {
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
            306 -> {
                return viewData6Binding
            }
            307 -> {
                return viewData7Binding
            }
            308 -> {
                return viewData8Binding
            }
            309 -> {
                return viewData9Binding
            }
            310 -> {
                return viewData10Binding
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