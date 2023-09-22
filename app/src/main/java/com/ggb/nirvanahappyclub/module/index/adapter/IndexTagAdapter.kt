package com.ggb.nirvanahappyclub.module.index.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.IndexTagBean
import com.ggb.nirvanahappyclub.databinding.ItemIndexTagBinding

class IndexTagAdapter: CustomRecyclerAdapter<IndexTagBean>() {



    override fun onCreateCustomViewHolder(parent: ViewGroup, viewType: Int): ContentType1ViewHolder? {
        return if (viewType == ITEM_TYPE_CONTENT1) {
            ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_index_tag, parent, false
                )
            )
        } else null
    }

    override fun onBindCustomViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    inner class ContentType1ViewHolder(binding: ItemIndexTagBinding) : ContentViewHolder<ItemIndexTagBinding>(binding) {
        init {
            ClickUtils.register(this)
                .addItemClickListener()
                .addView(binding.root)
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