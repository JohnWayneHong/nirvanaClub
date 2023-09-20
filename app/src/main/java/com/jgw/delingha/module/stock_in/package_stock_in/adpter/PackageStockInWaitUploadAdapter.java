package com.jgw.delingha.module.stock_in.package_stock_in.adpter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemPackageStockInPendingConfigBinding;
import com.jgw.delingha.sql.entity.PackageConfigEntity;

public class PackageStockInWaitUploadAdapter extends CustomRecyclerAdapter<PackageConfigEntity> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_package_stock_in_pending_config, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            PackageConfigEntity entity = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(entity);
            ((ContentType1ViewHolder) holder).mBindingView.ivPackagingAssociationWaitUploadSelect.setSelected(entity.isSelect());
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemPackageStockInPendingConfigBinding> {

        ContentType1ViewHolder(ItemPackageStockInPendingConfigBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.ivPackagingAssociationWaitUploadSelect, binding.getRoot())
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
