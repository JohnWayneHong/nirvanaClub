package com.jgw.delingha.module.scan_back.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemCommonScanBackBinding;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

public class CommonScanBackAdapter extends CustomRecyclerAdapter<BaseCodeEntity> {

    @Override
    public ContentViewHolder<? extends ViewDataBinding> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_common_scan_back, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            BaseCodeEntity entity = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(entity);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemCommonScanBackBinding> {

        ContentType1ViewHolder(ItemCommonScanBackBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.rlCommonScanBackDelete)
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
