package com.jgw.delingha.module.logistics_statistics.statistical_dimension;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.databinding.ItemStatisticalDimensionBinding;

/**
 * author : Cxz
 * data : 2019/11/20
 * description : 用户类别Item Adapter
 */
public class StatisticalDimensionListRecyclerAdapter extends CustomRecyclerAdapter<StatisticsParamsBean.StatisticalDimension> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_statistical_dimension, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            StatisticsParamsBean.StatisticalDimension bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemStatisticalDimensionBinding> {

        ContentType1ViewHolder(ItemStatisticalDimensionBinding binding) {
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
