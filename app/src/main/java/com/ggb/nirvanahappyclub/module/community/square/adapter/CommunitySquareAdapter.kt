package com.ggb.nirvanahappyclub.module.community.square.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.CommunityAndroidBean.CommunityAndroidListBean

import com.ggb.nirvanahappyclub.databinding.ItemCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.ItemCommunitySquareBinding

class CommunitySquareAdapter : CustomRecyclerAdapter<CommunityAndroidListBean>() {

    override fun onCreateCustomViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder<out ViewDataBinding>? {
        return if (viewType == ITEM_TYPE_CONTENT1) {
            ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_community_square, parent, false
                )
            )
        } else null
    }

    override fun onBindCustomViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentType1ViewHolder) {
            holder.mBindingView.data = mList[position]
        }
    }

    inner class ContentType1ViewHolder(binding: ItemCommunitySquareBinding) : ContentViewHolder<ItemCommunitySquareBinding>(binding) {
        init {
            ClickUtils.register(this)
                .addItemClickListener()
                .addView(binding.root)
                .addView(binding.cvCommunityContent)
                .submit()
        }

        override fun onItemClick(view: View?, position: Int) {
            super.onItemClick(view, position)
            if (mListener != null) {
                mListener.onItemClick(view, position)
            }
        }
    }

}