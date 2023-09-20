package com.jgw.delingha.module.exchange_goods.base.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ExchangeGoodsPendingConfigurationBean;
import com.jgw.delingha.databinding.ItemExchangeGoodsPendingConfigurationBinding;

public class ExchangeGoodsPendingConfigurationAdapter extends CustomRecyclerAdapter<ExchangeGoodsPendingConfigurationBean> {

    public ExchangeGoodsPendingConfigurationAdapter() {
    }

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_exchange_goods_pending_configuration, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ExchangeGoodsPendingConfigurationBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            ((ContentType1ViewHolder) holder).mBindingView.ivSelect.setSelected(bean.isSelect);
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemExchangeGoodsPendingConfigurationBinding> {

        ContentType1ViewHolder(ItemExchangeGoodsPendingConfigurationBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.ivSelect, binding.flConfiguration)
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
