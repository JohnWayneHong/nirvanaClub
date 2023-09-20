package com.jgw.delingha.module.stock_out.order.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemCommonStockOutWaitUploadListBinding;
import com.jgw.delingha.sql.entity.BaseOrderEntity;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;

/**
 * Created by xiongshaowu
 * on 2019/9/4
 */
public class CommonStockOutWaitUploadListRecyclerAdapter extends CustomRecyclerAdapter<BaseOrderEntity> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_common_stock_out_wait_upload_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            BaseOrderEntity bean = mList.get(position);
            if (bean instanceof OrderStockOutEntity) {
                ((ContentType1ViewHolder) holder).mBindingView.setData((OrderStockOutEntity) bean);
            }
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemCommonStockOutWaitUploadListBinding> {

        ContentType1ViewHolder(ItemCommonStockOutWaitUploadListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addView(binding.llStockOutWaitUploadListItem)
                    .addView(binding.rlStockOutWaitUploadListSelect)
                    .addItemClickListener()
                    .submit();
        }

        @Override
        public void onItemClick(View view, int position) {

            if (mListener != null) {
                mListener.onItemClick(view, position);
            }
        }
    }
}
