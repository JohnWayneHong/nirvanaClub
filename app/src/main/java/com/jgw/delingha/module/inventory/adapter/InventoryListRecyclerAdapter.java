package com.jgw.delingha.module.inventory.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InventoryListBean;
import com.jgw.delingha.databinding.ItemInventoryListBinding;


/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class InventoryListRecyclerAdapter extends CustomRecyclerAdapter<InventoryListBean.ListBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_inventory_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder){
            InventoryListBean.ListBean listBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemInventoryListBinding> {

        ContentType1ViewHolder(ItemInventoryListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvInventoryListExecute)
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
