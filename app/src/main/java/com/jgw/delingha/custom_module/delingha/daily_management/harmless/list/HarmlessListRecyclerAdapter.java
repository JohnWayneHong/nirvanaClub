package com.jgw.delingha.custom_module.delingha.daily_management.harmless.list;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.HarmlessListBean;
import com.jgw.delingha.databinding.ItemHarmlessListBinding;
import com.jgw.delingha.databinding.ItemHealthCareListBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;


/**
 * @author : hwj
 * @date : 2023/8/4
 * description : 无害化记录 列表 Adapter
 */
public class HarmlessListRecyclerAdapter extends CustomRecyclerAdapter<SelectItemSupport> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_harmless_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            SelectItemSupport selectItemSupport = mList.get(position);
            HarmlessListBean listBean = null;
            if (selectItemSupport instanceof HarmlessListBean) {
                listBean = (HarmlessListBean) selectItemSupport;
            }
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemHarmlessListBinding> {

        ContentType1ViewHolder(ItemHarmlessListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvHarmlessListDetails)
                    .addView(binding.tvHarmlessListDelete)
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
