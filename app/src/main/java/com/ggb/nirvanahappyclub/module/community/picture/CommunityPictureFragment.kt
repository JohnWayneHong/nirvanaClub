package com.ggb.nirvanahappyclub.module.community.picture

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.http.Resource
import com.ggb.common_library.utils.CommonDialogUtil
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.common_library.widget.commonDialog.CommonDialog
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.DevelopJokesListBean
import com.ggb.nirvanahappyclub.databinding.FragmentCommunityPictureBinding
import com.ggb.nirvanahappyclub.databinding.ItemCommunityTextBinding
import com.ggb.nirvanahappyclub.module.subscribe.LeastAnimationStateChangedHandler
import com.ggb.nirvanahappyclub.utils.CheckUpdateUtils
import com.ggb.nirvanahappyclub.utils.EncryptionUtils
import com.ggb.nirvanahappyclub.utils.RegularUtil
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.io.File

class CommunityPictureFragment : BaseFragment<CommunityPictureViewModel, FragmentCommunityPictureBinding>(),OnItemSingleClickListener,
    BGANinePhotoLayout.Delegate, BGAOnRVItemClickListener {

    private var mCurrentClickNpl: BGANinePhotoLayout? = null

    override fun initView() {
        mBindingView.rvCommunityPictureFragment.linear().setup {
            addType<DevelopJokesListBean>(R.layout.item_community_text)

            onBind {
                when (itemViewType) {
                    R.layout.item_community_text -> {
                        getBinding<ItemCommunityTextBinding>().data = getModel<DevelopJokesListBean>()

                        if (getBinding<ItemCommunityTextBinding>().data!!.joke.type >= 2) {
                            getBinding<ItemCommunityTextBinding>().nplItemMomentPhotos.setDelegate(this@CommunityPictureFragment)
                            //处理多图数据

                            val formulaStr = getBinding<ItemCommunityTextBinding>().data!!.joke.imageUrl.split(",")
                            val decodeString = arrayListOf<String>()
                            formulaStr.forEach {
                                decodeString.add(
                                    EncryptionUtils.decrypt(
                                        "cretinzp**273846", RegularUtil.truncateHeadString(it,6)
                                    ))
                            }

                            getBinding<ItemCommunityTextBinding>().nplItemMomentPhotos.data = decodeString
                        }

                    }
                }
            }

        }

        // 该处理者可以保证骨骼动图显示最短时间(避免网络请求过快导致骨骼动画快速消失屏幕闪烁), 如果不需要可以不配置
        // 推荐在Application中全局配置, 而不是每次都配置
        mBindingView.prCommunityPictureFragment.stateChangedHandler = LeastAnimationStateChangedHandler()
    }

    override fun initFragmentData() {
        mBindingView.prCommunityPictureFragment.onRefresh {
            mViewModel.getCommunityPicture("获取段子乐纯文")

        }.autoRefresh()

    }

    override fun initLiveData() {
        super.initLiveData()
        mViewModel.getCommunityPictureLiveData().observe(this) { resource ->
            when (resource.loadingStatus) {
                Resource.LOADING -> {}
                Resource.SUCCESS -> {
                    mBindingView.prCommunityPictureFragment.addData(resource.data)
                }
                Resource.ERROR -> {}
            }
        }
    }

    override fun initListener() {
        super.initListener()

    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }

    companion object {
        fun newInstance(): CommunityPictureFragment {
            val fragment = CommunityPictureFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /**
     * 图片预览，兼容6.0动态权限
     */
    private fun photoPreviewWrapper() {
        if (mCurrentClickNpl == null) {
            return
        }
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val downloadDir =
            File(Environment.getExternalStorageDirectory(), "BGAPhotoPickerDownload")
        val photoPreviewIntentBuilder = BGAPhotoPreviewActivity.IntentBuilder(context)
        if (true) {
            // 保存图片的目录，如果传 null，则没有保存图片功能
            photoPreviewIntentBuilder.saveImgDir(downloadDir)
        }
        if (mCurrentClickNpl!!.itemCount == 1) {
            // 预览单张图片
            photoPreviewIntentBuilder.previewPhoto(mCurrentClickNpl!!.currentClickItem)
        } else if (mCurrentClickNpl!!.itemCount > 1) {
            // 预览多张图片
            photoPreviewIntentBuilder.previewPhotos(mCurrentClickNpl!!.data)
                .currentPosition(mCurrentClickNpl!!.currentClickItemPosition) // 当前预览图片的索引
        }
        context.startActivity(photoPreviewIntentBuilder.build())
    }

    @SuppressLint("MissingPermission")
    private fun checkDownloadPermission(){

        XXPermissions.with(this)
            .permission(Permission.WRITE_EXTERNAL_STORAGE,Permission.READ_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: List<String>, all: Boolean) {
                    if (!all) {
                        return
                    }

                }

                override fun onDenied(permissions: List<String>, never: Boolean) {
                    CommonDialogUtil.showSelectDialog(context, "权限不足", "系统权限不足,无法正常使用,是否前往系统设置?", "取消", "设置",
                        object : CommonDialog.OnButtonClickListener {
                            override fun onRightClick() {
                                XXPermissions.startPermissionActivity(context, permissions)
                            }
                        })
                }
            })

    }

    override fun onClickNinePhotoItem(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
        checkDownloadPermission()
        mCurrentClickNpl = ninePhotoLayout
        photoPreviewWrapper()
    }

    override fun onClickExpand(
        ninePhotoLayout: BGANinePhotoLayout,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
        ninePhotoLayout.setIsExpand(true)
        ninePhotoLayout.flushItems()
    }

    override fun onRVItemClick(parent: ViewGroup?, itemView: View?, position: Int) {
        TODO("Not yet implemented")
    }

}