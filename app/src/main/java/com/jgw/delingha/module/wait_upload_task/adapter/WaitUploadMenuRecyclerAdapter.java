package com.jgw.delingha.module.wait_upload_task.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.WaitUploadMenuBean;
import com.jgw.delingha.databinding.ItemPendingTaskMenuBinding;

public class WaitUploadMenuRecyclerAdapter extends CustomRecyclerAdapter<WaitUploadMenuBean> {

    @Override
    public ContentViewHolder<?> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_pending_task_menu, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            WaitUploadMenuBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemPendingTaskMenuBinding> {

        ContentType1ViewHolder(ItemPendingTaskMenuBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.getRoot())
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
