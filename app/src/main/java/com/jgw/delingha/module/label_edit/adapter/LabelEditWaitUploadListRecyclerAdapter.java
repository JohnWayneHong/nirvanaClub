package com.jgw.delingha.module.label_edit.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.LabelEditWaitUploadListBean;
import com.jgw.delingha.databinding.ItemLabelEditWaitUploadListBinding;

public class LabelEditWaitUploadListRecyclerAdapter extends CustomRecyclerAdapter<LabelEditWaitUploadListBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_label_edit_wait_upload_list
                    , parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            LabelEditWaitUploadListBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            ((ContentType1ViewHolder) holder).mBindingView.ivLabelEditWaitUploadListSelect.setSelected(bean.selected);
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemLabelEditWaitUploadListBinding> {

        ContentType1ViewHolder(ItemLabelEditWaitUploadListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.rlLabelEditWaitUploadListSelect)
                    .addView(binding.llLabelEditWaitUploadListItem)
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
