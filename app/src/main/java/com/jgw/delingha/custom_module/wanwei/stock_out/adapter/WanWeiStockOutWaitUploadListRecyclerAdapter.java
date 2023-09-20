package com.jgw.delingha.custom_module.wanwei.stock_out.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StockOutFastWaitUploadListBean;
import com.jgw.delingha.databinding.ItemStockOutFastWaitUploadListBinding;

/**
 * Created by xiongshaowu
 * on 2019/9/4
 */
public class WanWeiStockOutWaitUploadListRecyclerAdapter extends CustomRecyclerAdapter<StockOutFastWaitUploadListBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_stock_out_fast_wait_upload_list, parent, false));
        } return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            StockOutFastWaitUploadListBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            ((ContentType1ViewHolder) holder).mBindingView.ivStockOutWaitUploadListSelect.setSelected(bean.selected);
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemStockOutFastWaitUploadListBinding> {

        ContentType1ViewHolder(ItemStockOutFastWaitUploadListBinding binding) {
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
