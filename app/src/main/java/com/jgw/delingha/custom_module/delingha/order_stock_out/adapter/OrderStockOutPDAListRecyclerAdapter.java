package com.jgw.delingha.custom_module.delingha.order_stock_out.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.databinding.ItemOrderStockInScanBinding;


public class OrderStockOutPDAListRecyclerAdapter extends CustomRecyclerAdapter<OrderStockScanBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_stock_in_scan, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            OrderStockScanBean data = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(data);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderStockInScanBinding> {

        public ContentType1ViewHolder(ItemOrderStockInScanBinding binding) {
            super(binding);
        }
    }
}
