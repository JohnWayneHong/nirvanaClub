package com.jgw.delingha.module.stock_out.base.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StockOutWaitUploadListBean;
import com.jgw.delingha.databinding.ItemStockOutWaitUploadListBinding;

public class StockOutWaitUploadListRecyclerAdapter extends CustomRecyclerAdapter<StockOutWaitUploadListBean> {

    @Override
    public ContentViewHolder<? extends ViewDataBinding> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_stock_out_wait_upload_list
                    , parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            StockOutWaitUploadListBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            ((ContentType1ViewHolder) holder).mBindingView.ivStockOutWaitUploadListSelect.setSelected(bean.selected);
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemStockOutWaitUploadListBinding> {

        ContentType1ViewHolder(ItemStockOutWaitUploadListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.rlStockOutWaitUploadListSelect)
                    .addView(binding.llStockOutWaitUploadListItem)
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
