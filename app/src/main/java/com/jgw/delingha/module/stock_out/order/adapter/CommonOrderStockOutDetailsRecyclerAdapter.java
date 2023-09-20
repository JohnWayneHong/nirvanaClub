package com.jgw.delingha.module.stock_out.order.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.HeaderCommonOrderStockOutDetailsBinding;
import com.jgw.delingha.databinding.ItemCommonOrderStockOutDetailsBinding;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class CommonOrderStockOutDetailsRecyclerAdapter extends CustomRecyclerAdapter<OrderStockOutProductInfoEntity> {

    private OrderStockOutEntity mHeaderBean;

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new MyHeaderViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.header_common_order_stock_out_details, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_common_order_stock_out_details, parent, false));
        }return null;
    }

    public void setHeaderData(OrderStockOutEntity headerData) {
        mHeaderBean = headerData;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHeaderViewHolder) {
            ((MyHeaderViewHolder) holder).mBindingView.setData(mHeaderBean);
        } else if (holder instanceof ContentType1ViewHolder) {
            OrderStockOutProductInfoEntity listBean = mList.get(position - 1);
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

    public OrderStockOutEntity getHeaderBean() {
        return mHeaderBean;
    }

    private class MyHeaderViewHolder extends ContentViewHolder<HeaderCommonOrderStockOutDetailsBinding> {

        MyHeaderViewHolder(HeaderCommonOrderStockOutDetailsBinding binding) {
            super(binding);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemCommonOrderStockOutDetailsBinding> {

        ContentType1ViewHolder(ItemCommonOrderStockOutDetailsBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvOrderStockOutDetailsEdit)
                    .addView(binding.tvOrderStockOutDetailsScanCode)
                    .addView(binding.tvOrderStockOutDetailsInput)
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
