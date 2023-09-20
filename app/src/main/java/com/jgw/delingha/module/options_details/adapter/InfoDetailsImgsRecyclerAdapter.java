package com.jgw.delingha.module.options_details.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ImageBean;
import com.jgw.delingha.databinding.ItemInfoDetailsImgs2Binding;
import com.jgw.delingha.databinding.ItemInfoDetailsImgsBinding;

/**
 * Created by XiongShaoWu
 * on 2019/9/12
 */
public class InfoDetailsImgsRecyclerAdapter extends CustomRecyclerAdapter<ImageBean> {


    private int mType = 0;

    private int mLimit = 5;

    public InfoDetailsImgsRecyclerAdapter() {
        super();
    }

    public InfoDetailsImgsRecyclerAdapter(int type) {
        super();
        mType = type;
    }

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_imgs, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT2) {
            return new ContentType2ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_info_details_imgs2, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ImageBean imageBean;
            if (position < getAdapterItemCount()) {
                imageBean = mList.get(position);
                ((ContentType1ViewHolder) holder).mBindingView.ivInfoDetailsImgDelete.setVisibility(View.VISIBLE);
            } else {
                imageBean = new ImageBean(R.drawable.add_img_icon);
                ((ContentType1ViewHolder) holder).mBindingView.ivInfoDetailsImgDelete.setVisibility(View.GONE);
            }
            ((ContentType1ViewHolder) holder).mBindingView.setImgBean(imageBean);
        } else if (holder instanceof ContentType2ViewHolder) {
            ImageBean imageBean;
            if (position < getAdapterItemCount()) {
                imageBean = mList.get(position);
            } else {
                imageBean = new ImageBean(R.drawable.add_img_icon);
            }
            ((ContentType2ViewHolder) holder).mBindingView.setImgBean(imageBean);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mType) {
            case 0:
                return ITEM_TYPE_CONTENT1;
            case 1:
                return ITEM_TYPE_CONTENT2;
            default:
                return ITEM_TYPE_CONTENT1;
        }
    }

    @Override
    public int getItemCount() {
        if (mType == 0) {
            if (getAdapterItemCount() < mLimit) {
                return getAdapterItemCount() + 1;
            }
            return mLimit;
        } else {
            return Math.min(getAdapterItemCount(), mLimit);
        }
    }

    public void setLimit(int limit){
        mLimit = limit;
    }

    public void setType(int type) {
        mType=type;
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemInfoDetailsImgsBinding> {
        public ContentType1ViewHolder(ItemInfoDetailsImgsBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(mBindingView.rlInfoDetailsImg)
                    .addView(mBindingView.ivInfoDetailsImgDelete)
                    .submit();
        }

        @Override
        public void onItemClick(View view, int position) {
            super.onItemClick(view, position);
            if (mListener != null) {
                mListener.onItemClick(view, position);
            }
        }
    }

    private class ContentType2ViewHolder extends ContentViewHolder<ItemInfoDetailsImgs2Binding> {
        public ContentType2ViewHolder(ItemInfoDetailsImgs2Binding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(mBindingView.rlInfoDetailsImg)
                    .submit();
        }

        @Override
        public void onItemClick(View view, int position) {
            super.onItemClick(view, position);
            if (mListener != null) {
                mListener.onItemClick(view, position);
            }
        }
    }

}
