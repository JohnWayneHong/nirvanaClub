package com.jgw.delingha.custom_module.delingha.breed.ear_reset;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemBreedInScanBackBinding;

/**
 * 2023-8-4 09:43:49
 * 养殖进场 耳号重置 adapter
 */

public class EarResetAssociateAdapter extends CustomRecyclerAdapter<String> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_breed_in_scan_back, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ((ContentType1ViewHolder) holder).mBindingView.tvScanBackEarNumber.setText(mList.get(position));
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemBreedInScanBackBinding> {
        ContentType1ViewHolder(ItemBreedInScanBackBinding binding) {
            super(binding);
        }
    }
}
