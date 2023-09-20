package com.jgw.delingha.module.query.code_status.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeStatusQueryDetailsLabelBean;
import com.jgw.delingha.databinding.ItemCodeStatusQueryLabelBinding;

/**
 * @author : J-T
 * @date : 2022/7/25 13:39
 * description : 扫码状态查询标签 adapter
 */
public class CodeStatusQueryDetailsLabelAdapter extends CustomRecyclerAdapter<CodeStatusQueryDetailsLabelBean> {
    @Override
    public ContentViewHolder<? extends ViewDataBinding> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_code_status_query_label, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            CodeStatusQueryDetailsLabelBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }

    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemCodeStatusQueryLabelBinding> {

        ContentType1ViewHolder(ItemCodeStatusQueryLabelBinding binding) {
            super(binding);

            ClickUtils.register(this)
                    .addView(binding.clCodeStatusQueryLabelContainer)
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
