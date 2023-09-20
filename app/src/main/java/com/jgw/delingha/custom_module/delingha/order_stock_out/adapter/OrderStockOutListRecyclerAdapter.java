package com.jgw.delingha.custom_module.delingha.order_stock_out.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.databinding.ItemOrderStockOutListBinding;


/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockOutListRecyclerAdapter extends CustomRecyclerAdapter<OrderStockOutListBean.ListBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_order_stock_out_list, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder){
            OrderStockOutListBean.ListBean listBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemOrderStockOutListBinding> {

        ContentType1ViewHolder(ItemOrderStockOutListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvOrderStockOutExecute)
                    .addView(binding.tvOrderStockOutDetails)
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
