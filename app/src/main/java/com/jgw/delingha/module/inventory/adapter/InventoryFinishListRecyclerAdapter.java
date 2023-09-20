package com.jgw.delingha.module.inventory.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.InventoryCodeBean;
import com.jgw.delingha.databinding.ItemInventoryFinishBinding;


/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class InventoryFinishListRecyclerAdapter extends CustomRecyclerAdapter<InventoryCodeBean.ListBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_inventory_finish, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder){
            InventoryCodeBean.ListBean listBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemInventoryFinishBinding> {

        ContentType1ViewHolder(ItemInventoryFinishBinding binding) {
            super(binding);
        }
    }
}
