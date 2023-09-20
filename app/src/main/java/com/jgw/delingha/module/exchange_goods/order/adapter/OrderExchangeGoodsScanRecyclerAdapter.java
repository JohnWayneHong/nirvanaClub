package com.jgw.delingha.module.exchange_goods.order.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsCodeBean;
import com.jgw.delingha.databinding.ItemOrderExchangeGoodsScanBinding;


public class OrderExchangeGoodsScanRecyclerAdapter extends CustomRecyclerAdapter<OrderExchangeGoodsCodeBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_exchange_goods_scan, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            OrderExchangeGoodsCodeBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderExchangeGoodsScanBinding> {

        public ContentType1ViewHolder(ItemOrderExchangeGoodsScanBinding binding) {
            super(binding);
        }
    }
}
