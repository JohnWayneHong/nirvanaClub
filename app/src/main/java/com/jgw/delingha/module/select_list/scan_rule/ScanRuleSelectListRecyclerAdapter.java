package com.jgw.delingha.module.select_list.scan_rule;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ScanRuleBean;
import com.jgw.delingha.databinding.ItemScanRuleSelectBinding;

public class ScanRuleSelectListRecyclerAdapter extends CustomRecyclerAdapter<ScanRuleBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_scan_rule_select, parent, false));
        } else {
            return new EmptyViewHolder(DataBindingUtil.inflate(mLayoutInflater, R.layout.item_empty, parent, false));
        }
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ScanRuleBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }

    @Override
    public int getItemCount() {
        return isEmpty() ? 1 : getAdapterItemCount();
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemScanRuleSelectBinding> {

        ContentType1ViewHolder(ItemScanRuleSelectBinding binding) {
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
