package com.jgw.delingha.module.exchange_goods.order.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.databinding.ItemOrderExchangeGoodsScanBackBinding;

public class OrderExchangeGoodsScanBackAdapter extends CustomRecyclerAdapter<OrderExchangeGoodsCodeBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_exchange_goods_scan_back, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            OrderExchangeGoodsCodeBean codeBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(codeBean);
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderExchangeGoodsScanBackBinding> {

        ContentType1ViewHolder(ItemOrderExchangeGoodsScanBackBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.rlScanBackDelete)
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
