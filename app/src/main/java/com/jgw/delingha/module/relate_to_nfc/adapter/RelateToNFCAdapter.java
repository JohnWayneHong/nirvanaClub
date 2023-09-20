package com.jgw.delingha.module.relate_to_nfc.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemRelateToNfcBinding;
import com.jgw.delingha.sql.entity.RelateToNFCEntity;

/**
 * @author : J-T
 * @date : 2022/6/9 10:13
 * description : 关联NFC adapter
 */
public class RelateToNFCAdapter extends CustomRecyclerAdapter<RelateToNFCEntity> {
    @Override
    public ContentViewHolder<? extends ViewDataBinding> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_relate_to_nfc, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == ContentType1ViewHolder.class) {
            RelateToNFCEntity relateToNFCEntity = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(relateToNFCEntity);
        }

    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemRelateToNfcBinding> {

        ContentType1ViewHolder(ItemRelateToNfcBinding binding) {
            super(binding);
        }
    }
}
