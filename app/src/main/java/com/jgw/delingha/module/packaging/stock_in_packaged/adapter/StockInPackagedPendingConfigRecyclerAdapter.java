package com.jgw.delingha.module.packaging.stock_in_packaged.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ConfigurationSelectBean;
import com.jgw.delingha.databinding.ItemStockInPendingConfigBinding;


public class StockInPackagedPendingConfigRecyclerAdapter extends CustomRecyclerAdapter<ConfigurationSelectBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_stock_in_pending_config, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ConfigurationSelectBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.ivSelect.setSelected(bean.selected);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemStockInPendingConfigBinding> {

        ContentType1ViewHolder(ItemStockInPendingConfigBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.ivSelect, binding.llPendingItemRoot)
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
