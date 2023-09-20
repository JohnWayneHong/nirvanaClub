package com.jgw.delingha.custom_module.delingha.daily_management.exchange_pigsty.list;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ExchangePigstyListBean;
import com.jgw.delingha.bean.WeightListBean;
import com.jgw.delingha.databinding.ItemExchangePigstyListBinding;
import com.jgw.delingha.databinding.ItemWeightListBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;


/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class ExchangePigstyListRecyclerAdapter extends CustomRecyclerAdapter<SelectItemSupport> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_exchange_pigsty_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            SelectItemSupport selectItemSupport = mList.get(position);
            ExchangePigstyListBean listBean = null;
            if (selectItemSupport instanceof ExchangePigstyListBean) {
                listBean = (ExchangePigstyListBean) selectItemSupport;
            }
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemExchangePigstyListBinding> {

        ContentType1ViewHolder(ItemExchangePigstyListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvExchangePigstyListDetails)
                    .addView(binding.tvExchangePigstyListDelete)
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
