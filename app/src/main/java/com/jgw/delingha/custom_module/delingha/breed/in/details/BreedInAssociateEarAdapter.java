package com.jgw.delingha.custom_module.delingha.breed.in.details;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedInAssociateBean;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.databinding.ItemAssociateEarNumberBinding;

/**
 * 养殖进场，离场 的关联耳号的adapter
 */

public class BreedInAssociateEarAdapter extends CustomRecyclerAdapter<BreedInAssociateBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_associate_ear_number, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            BreedInAssociateBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemAssociateEarNumberBinding> {

        ContentType1ViewHolder(ItemAssociateEarNumberBinding binding) {
            super(binding);
        }
    }
}
