package com.jgw.delingha.custom_module.delingha.order_stock_out.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.databinding.HeaderOrderStockOutDetailsBinding;
import com.jgw.delingha.databinding.ItemOrderStockOutDetailsBinding;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockOutDetailsRecyclerAdapter extends CustomRecyclerAdapter<OrderStockOutDetailsBean.ListBean> {

    private OrderStockOutListBean.ListBean mHeaderBean;

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new MyHeaderViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.header_order_stock_out_details, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_stock_out_details, parent, false));
        }return null;
    }

    public void setHeaderData(OrderStockOutListBean.ListBean headerData) {
        mHeaderBean = headerData;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHeaderViewHolder) {
            ((MyHeaderViewHolder) holder).mBindingView.setData(mHeaderBean);
        } else if (holder instanceof ContentType1ViewHolder) {
            OrderStockOutDetailsBean.ListBean listBean = mList.get(position - 1);
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        }

        return ITEM_TYPE_CONTENT1;
    }

    
    @Override
    public int getHeaderCount() {
        return 1;
    }

    private class MyHeaderViewHolder extends ContentViewHolder<HeaderOrderStockOutDetailsBinding> {

        MyHeaderViewHolder(HeaderOrderStockOutDetailsBinding binding) {
            super(binding);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderStockOutDetailsBinding> {

        ContentType1ViewHolder(ItemOrderStockOutDetailsBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvOrderStockOutDetailsExecute)
                    .addView(binding.tvOrderStockOutDetailsInput)
                    .addView(binding.tvOrderStockOutDetailsScanCode)
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
