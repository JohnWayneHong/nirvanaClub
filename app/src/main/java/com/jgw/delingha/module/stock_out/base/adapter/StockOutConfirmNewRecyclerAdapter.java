package com.jgw.delingha.module.stock_out.base.adapter;


import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemNewStockOutConfirmBinding;
import com.jgw.delingha.sql.entity.CodeEntity;

public class StockOutConfirmNewRecyclerAdapter extends CustomRecyclerAdapter<CodeEntity> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_new_stock_out_confirm, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            CodeEntity bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            if (!TextUtils.isEmpty(bean.getThirdNumberUnitName())) {
                ((ContentType1ViewHolder) holder).mBindingView.tvStockOutConfirmItemBigBox.setVisibility(View.VISIBLE);
            } else {
                ((ContentType1ViewHolder) holder).mBindingView.tvStockOutConfirmItemBigBox.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(bean.getSecondNumberUnitName())) {
                ((ContentType1ViewHolder) holder).mBindingView.tvStockOutConfirmItemBox.setVisibility(View.VISIBLE);
            } else {
                ((ContentType1ViewHolder) holder).mBindingView.tvStockOutConfirmItemBox.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(bean.getFirstNumberUnitName())) {
                ((ContentType1ViewHolder) holder).mBindingView.tvStockOutConfirmItemNumber.setVisibility(View.VISIBLE);
            } else {
                ((ContentType1ViewHolder) holder).mBindingView.tvStockOutConfirmItemNumber.setVisibility(View.GONE);
            }
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemNewStockOutConfirmBinding> {

        ContentType1ViewHolder(ItemNewStockOutConfirmBinding binding) {
            super(binding);
        }
    }
}
