package com.ggb.nirvanahappyclub.module.mine.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.IndexTagBean
import com.ggb.nirvanahappyclub.bean.MeFunctionBean
import com.ggb.nirvanahappyclub.databinding.ItemIndexTagBinding
import com.ggb.nirvanahappyclub.databinding.ItemMeBannerBinding
import com.ggb.nirvanahappyclub.databinding.ItemMeOptionBinding

class MeFunctionAdapter: CustomRecyclerAdapter<MeFunctionBean>() {

    override fun onCreateCustomViewHolder(parent: ViewGroup, viewType: Int): ContentType1ViewHolder? {
        return if (viewType == ITEM_TYPE_CONTENT1) {
            ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_me_option, parent, false
                )
            )
        } else null
    }

    override fun onBindCustomViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentType1ViewHolder) {
            holder.mBindingView.data = mList[position]
        }
    }


    inner class ContentType1ViewHolder(binding: ItemMeOptionBinding) : ContentViewHolder<ItemMeOptionBinding>(binding) {
        init {
            ClickUtils.register(this)
                .addItemClickListener()
                .addView(binding.llMeOption)
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