package com.jgw.delingha.module.inventory.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InventoryScanBean;
import com.jgw.delingha.databinding.ItemInventoryScanBinding;


public class InventoryPDAListRecyclerAdapter extends CustomRecyclerAdapter<InventoryScanBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_inventory_scan, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            InventoryScanBean data = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(data);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemInventoryScanBinding> {

        public ContentType1ViewHolder(ItemInventoryScanBinding binding) {
            super(binding);
        }
    }
}
