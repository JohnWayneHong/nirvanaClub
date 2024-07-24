package com.ggb.nirvanahappyclub.module.mine.function.setting

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.bean.CommunityAndroidBean.CommunityAndroidListBean

import com.ggb.nirvanahappyclub.bean.IndexArticleInfoBean
import com.ggb.nirvanahappyclub.bean.MineSettingBean
import com.ggb.nirvanahappyclub.bean.MineSettingListBean
import com.ggb.nirvanahappyclub.databinding.ItemArticleInfoBinding
import com.ggb.nirvanahappyclub.databinding.ItemCommunityAndroidBinding
import com.ggb.nirvanahappyclub.databinding.ItemMineSettingBinding
import com.ggb.nirvanahappyclub.databinding.ItemMineSettingChildBinding
import com.ggb.nirvanahappyclub.module.community.daily.adapter.WordSearchDataAdapter
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity

class MineSettingAdapter : CustomRecyclerAdapter<Any>() {

    override fun onCreateCustomViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder<out ViewDataBinding>? {
        return when (viewType) {
            ITEM_TYPE_CONTENT1 -> ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_mine_setting, parent, false
                )
            )

            ITEM_TYPE_CONTENT2 -> ContentType2ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_mine_setting_child, parent, false
                )
            )

            else -> ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_mine_setting, parent, false
                )
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (mList[position] is MineSettingBean) {
            ITEM_TYPE_CONTENT1
        }else if (mList[position] is MineSettingListBean) {
            ITEM_TYPE_CONTENT2
        }else {
            throw  IllegalArgumentException("Invalid item type at position " + position)
        }

    }

    override fun onBindCustomViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mList[position]
        if (holder is ContentType1ViewHolder) {
            val mineSettingBean = item as MineSettingBean
            holder.mBindingView.data = mineSettingBean
        } else if (holder is ContentType2ViewHolder) {
            val mineSettingListBean = item as MineSettingListBean
            holder.mBindingView.data = mineSettingListBean
        }
    }

    inner class ContentType1ViewHolder(binding: ItemMineSettingBinding) : ContentViewHolder<ItemMineSettingBinding>(binding) {
        init {
            ClickUtils.register(this)
                .addItemClickListener()
                .addView(binding.root)
                .addView(binding.llSetting)
                .submit()
        }

        override fun onItemClick(view: View?, position: Int) {
            super.onItemClick(view, position)
            if (mListener != null) {
                mListener.onItemClick(view, position)
            }
        }
    }

    inner class ContentType2ViewHolder(binding: ItemMineSettingChildBinding) : ContentViewHolder<ItemMineSettingChildBinding>(binding) {
        init {
            ClickUtils.register(this)
                .addItemClickListener()
                .addView(binding.root)
                .addView(binding.llSettingsChildContent)
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