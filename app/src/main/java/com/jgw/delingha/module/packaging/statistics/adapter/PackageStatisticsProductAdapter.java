package com.jgw.delingha.module.packaging.statistics.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.PackageStatisticsBean;
import com.jgw.delingha.databinding.ItemPackageStatisticsProductBinding;

public class PackageStatisticsProductAdapter extends CustomRecyclerAdapter<PackageStatisticsBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_package_statistics_product, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            PackageStatisticsBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            int max=0;
            for (PackageStatisticsBean b : mList) {
                max += b.firstCount;
            }
            ((ContentType1ViewHolder) holder).mBindingView.pbPackageStatisticsProduct.setMax(max);
            ((ContentType1ViewHolder) holder).mBindingView.pbPackageStatisticsProduct.setProgress(bean.firstCount);
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemPackageStatisticsProductBinding> {

        ContentType1ViewHolder(ItemPackageStatisticsProductBinding binding) {
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
