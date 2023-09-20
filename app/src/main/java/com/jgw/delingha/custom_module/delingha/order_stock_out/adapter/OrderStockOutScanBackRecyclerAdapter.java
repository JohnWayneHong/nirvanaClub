package com.jgw.delingha.custom_module.delingha.order_stock_out.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.databinding.ItemOrderStockOutScanBackBinding;


/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockOutScanBackRecyclerAdapter extends CustomRecyclerAdapter<OrderStockScanBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_stock_out_scan_back, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder){
            OrderStockScanBean listBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
            if (listBean.codeStatus!= CodeBean.STATUS_CODE_SUCCESS){
                ((ContentType1ViewHolder) holder).mBindingView.tvOrderStockOutScanBackUnit.setVisibility(View.GONE);
            }else {
                ((ContentType1ViewHolder) holder).mBindingView.tvOrderStockOutScanBackUnit.setVisibility(View.VISIBLE);
            }
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderStockOutScanBackBinding> {

        ContentType1ViewHolder(ItemOrderStockOutScanBackBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.ivOrderStockOutScanBackDelete)
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
