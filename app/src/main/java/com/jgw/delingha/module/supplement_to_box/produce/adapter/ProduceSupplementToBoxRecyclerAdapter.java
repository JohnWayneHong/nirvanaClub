package com.jgw.delingha.module.supplement_to_box.produce.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.databinding.ItemPdaScanBinding;

public class ProduceSupplementToBoxRecyclerAdapter extends CustomRecyclerAdapter<CodeBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_pda_scan, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            CodeBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            if (bean.codeStatus == CodeBean.STATUS_CODE_VERIFYING) {
                ((ContentType1ViewHolder) holder).mBindingView.tvChecking.setVisibility(View.VISIBLE);
                ((ContentType1ViewHolder) holder).mBindingView.ivCheckOk.setVisibility(View.GONE);
            } else if (bean.codeStatus == CodeBean.STATUS_CODE_SUCCESS) {
                ((ContentType1ViewHolder) holder).mBindingView.tvChecking.setVisibility(View.GONE);
                ((ContentType1ViewHolder) holder).mBindingView.ivCheckOk.setVisibility(View.VISIBLE);
            } else if (bean.codeStatus == CodeBean.STATUS_CODE_FAIL) {
                ((ContentType1ViewHolder) holder).mBindingView.tvChecking.setVisibility(View.VISIBLE);
                ((ContentType1ViewHolder) holder).mBindingView.ivCheckOk.setVisibility(View.GONE);
            }
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemPdaScanBinding> {

        ContentType1ViewHolder(ItemPdaScanBinding binding) {
            super(binding);
        }
    }
}
