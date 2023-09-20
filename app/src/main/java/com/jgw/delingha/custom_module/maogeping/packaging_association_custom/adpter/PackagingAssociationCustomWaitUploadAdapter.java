package com.jgw.delingha.custom_module.maogeping.packaging_association_custom.adpter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemPackagingAssociationWaitUploadBinding;
import com.jgw.delingha.sql.entity.PackageConfigEntity;

public class PackagingAssociationCustomWaitUploadAdapter extends CustomRecyclerAdapter<PackageConfigEntity> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_packaging_association_wait_upload, parent, false));
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


    private class ContentType1ViewHolder extends ContentViewHolder<ItemPackagingAssociationWaitUploadBinding> {

        ContentType1ViewHolder(ItemPackagingAssociationWaitUploadBinding binding) {
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
