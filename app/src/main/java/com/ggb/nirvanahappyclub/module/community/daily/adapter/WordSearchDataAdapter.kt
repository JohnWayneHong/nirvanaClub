package com.ggb.nirvanahappyclub.module.community.daily.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ggb.common_library.base.adapter.CustomRecyclerAdapter
import com.ggb.common_library.utils.click_utils.ClickUtils
import com.ggb.nirvanahappyclub.R
import com.ggb.nirvanahappyclub.databinding.ItemWordSearchDataBinding
import com.ggb.nirvanahappyclub.databinding.ItemWordSearchDataPhraseBinding
import com.ggb.nirvanahappyclub.databinding.ItemWordSearchDataTranslationBinding
import com.ggb.nirvanahappyclub.sql.entity.BasicWordEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordPhraseEntity
import com.ggb.nirvanahappyclub.sql.entity.BasicWordTranslationEntity


class WordSearchDataAdapter: CustomRecyclerAdapter<Any>() {



    override fun onCreateCustomViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder<out ViewDataBinding> {
        return when (viewType) {
            ITEM_TYPE_CONTENT1 -> ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_word_search_data, parent, false
                )
            )
            ITEM_TYPE_CONTENT2 -> ContentType2ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_word_search_data_translation, parent, false
                )
            )
            ITEM_TYPE_CONTENT3 -> ContentType3ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_word_search_data_phrase, parent, false
                )
            )
            else -> ContentType1ViewHolder(
                DataBindingUtil.inflate(
                    mLayoutInflater, R.layout.item_word_search_data, parent, false
                )
            )
        }
    }


    override fun onBindCustomViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mList[position]
        if (holder is ContentType1ViewHolder) {
            val wordEntity: BasicWordEntity = item as BasicWordEntity

            holder.mBindingView.data = wordEntity
        }else if (holder is ContentType2ViewHolder) {
            val wordEntity: BasicWordTranslationEntity = item as BasicWordTranslationEntity

            holder.mBindingView.data = wordEntity
        }else if (holder is ContentType3ViewHolder) {
            val wordEntity: BasicWordPhraseEntity = item as BasicWordPhraseEntity

            holder.mBindingView.data = wordEntity
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mList[position] is  BasicWordEntity) {
            ITEM_TYPE_CONTENT1
        }else if (mList[position] is BasicWordTranslationEntity) {
            ITEM_TYPE_CONTENT2
        }else if (mList[position] is BasicWordPhraseEntity) {
            ITEM_TYPE_CONTENT3
        }else {
            throw  IllegalArgumentException("Invalid item type at position " + position)
        }

    }

    inner class ContentType1ViewHolder(binding: ItemWordSearchDataBinding) : ContentViewHolder<ItemWordSearchDataBinding>(binding) {
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

    inner class ContentType2ViewHolder(binding: ItemWordSearchDataTranslationBinding) : ContentViewHolder<ItemWordSearchDataTranslationBinding>(binding) {
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

    inner class ContentType3ViewHolder(binding: ItemWordSearchDataPhraseBinding) : ContentViewHolder<ItemWordSearchDataPhraseBinding>(binding) {
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