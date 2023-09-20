package com.jgw.delingha.custom_module.delingha.daily_management.weight.list;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FeedingListBean;
import com.jgw.delingha.bean.WeightListBean;
import com.jgw.delingha.databinding.ItemFeedingListBinding;
import com.jgw.delingha.databinding.ItemWeightListBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;


/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class WeightListRecyclerAdapter extends CustomRecyclerAdapter<SelectItemSupport> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_weight_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            SelectItemSupport selectItemSupport = mList.get(position);
            WeightListBean listBean = null;
            if (selectItemSupport instanceof WeightListBean) {
                listBean = (WeightListBean) selectItemSupport;
            }
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemWeightListBinding> {

        ContentType1ViewHolder(ItemWeightListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvWeightListDetails)
                    .addView(binding.tvWeightListDelete)
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
