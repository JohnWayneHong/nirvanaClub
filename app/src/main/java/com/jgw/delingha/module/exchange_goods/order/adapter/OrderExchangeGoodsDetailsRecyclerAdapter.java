package com.jgw.delingha.module.exchange_goods.order.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderExchangeGoodsDetailsBean;
import com.jgw.delingha.databinding.HeaderOrderExchangeGoodsDetailsBinding;
import com.jgw.delingha.databinding.ItemOrderExchangeGoodsDetailsBinding;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderExchangeGoodsDetailsRecyclerAdapter extends CustomRecyclerAdapter<OrderExchangeGoodsDetailsBean.ProductsBean> {

    private OrderExchangeGoodsDetailsBean mBean;

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new MyHeaderViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.header_order_exchange_goods_details, parent, false));
        }else if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_exchange_goods_details, parent, false));
        }return null;
    }

    public void setOrderExchangeGoodsDetailsBean(OrderExchangeGoodsDetailsBean bean){
        mBean=bean;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHeaderViewHolder){
            ((MyHeaderViewHolder) holder).mBindingView.setData(mBean);
        }else  if (holder instanceof ContentType1ViewHolder){
            OrderExchangeGoodsDetailsBean.ProductsBean listBean = mList.get(position-1);
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return ITEM_TYPE_HEADER;
        }
        return ITEM_TYPE_CONTENT1;
    }

    @Override
    public int getHeaderCount() {
        return 1;
    }

    private class MyHeaderViewHolder extends ContentViewHolder<HeaderOrderExchangeGoodsDetailsBinding> {

        MyHeaderViewHolder(HeaderOrderExchangeGoodsDetailsBinding binding) {
            super(binding);
         }
    }
    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderExchangeGoodsDetailsBinding> {

        ContentType1ViewHolder(ItemOrderExchangeGoodsDetailsBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvOrderStockOutDetailsDelete)
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
