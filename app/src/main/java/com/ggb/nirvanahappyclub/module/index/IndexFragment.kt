package com.ggb.nirvanahappyclub.module.index

import android.view.View
import androidx.fragment.app.Fragment
import com.ggb.common_library.base.ui.BaseFragment
import com.ggb.common_library.utils.click_utils.listener.OnItemSingleClickListener
import com.ggb.nirvanahappyclub.bean.IndexTagBean
import com.ggb.nirvanahappyclub.databinding.FragmentIndexBinding
import com.ggb.nirvanahappyclub.module.index.adapter.IndexTagAdapter
import com.ggb.nirvanahappyclub.module.index.dialog.DownloadProgressDialog

class IndexFragment : BaseFragment<IndexViewModel, FragmentIndexBinding>(),OnItemSingleClickListener{

    private var mIndex = 0

    private var tAdapter : IndexTagAdapter?=null

    private var tagList = arrayListOf<IndexTagBean>()

    private var fragments: ArrayList<Fragment> = ArrayList()

    private var processDialog: DownloadProgressDialog? = null

    private var patchPath = ""

    //构造方法可补充
    init {

    }


    override fun initView() {

    }

    override fun initFragmentData() {

    }

    override fun initLiveData() {
        super.initLiveData()

    }

    override fun initListener() {
        super.initListener()

    }

    override fun onItemClick(view: View?, groupPosition: Int, subPosition: Int) {
        super.onItemClick(view, groupPosition, subPosition)

    }
}