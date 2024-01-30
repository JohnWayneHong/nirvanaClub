package com.ggb.nirvanahappyclub.module.mine.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.IndexTagBean
import com.ggb.nirvanahappyclub.databinding.ItemIndexTagBinding
import com.ggb.nirvanahappyclub.databinding.ItemMeBannerBinding

class MeBannerAdapter: CustomRecyclerAdapter<Int>() {

    override fun onCreateCustomViewHolder(parent: ViewGroup, viewType: Int): ContentType1ViewHolder? {
        return if (viewType == ITEM_TYPE_CONTENT1) {
            ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_me_banner, parent, false
                )
            )
        } else null
    }

    override fun onBindCustomViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentType1ViewHolder) {
            val newPosition = position % mList.size

            holder.mBindingView.data = mList[newPosition]
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    //重写此方法，因为BaseQuickAdapter里绘制view时会调用此方法判断，position减去getHeaderLayoutCount小于data.size()时才会调用调用cover方法绘制我们自定义的view
    override fun getItemViewType(position: Int): Int {
        var count = headerCount + mList.size
        //刚开始进入包含该类的activity时,count为0。就会出现0%0的情况，这会抛出异常，所以我们要在下面做一下判断
        if (count <= 0) {
            count = 1
        }
        val newPosition = position % count
        return super.getItemViewType(newPosition)
    }


    inner class ContentType1ViewHolder(binding: ItemMeBannerBinding) : ContentViewHolder<ItemMeBannerBinding>(binding) {
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