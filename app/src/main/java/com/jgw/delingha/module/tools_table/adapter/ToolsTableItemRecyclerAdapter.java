package com.jgw.delingha.module.tools_table.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.databinding.ItemToolsTableItemBinding;

/**
 * Created by XiongShaoWu
 * on 2019/9/12
 */
public class ToolsTableItemRecyclerAdapter extends CustomRecyclerAdapter<ToolsOptionsBean.MobileFunsBean> {


    public ToolsTableItemRecyclerAdapter() {
        super();
    }

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.item_tools_table_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ((ContentType1ViewHolder) holder).mBindingView.setData(mList.get(position));
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemToolsTableItemBinding> {

        public ContentType1ViewHolder(ItemToolsTableItemBinding binding) {
            super(binding);

            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.getRoot())
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
