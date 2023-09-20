package com.jgw.delingha.module.query.code_status.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeStatusQueryInfoItemBean;
import com.jgw.delingha.databinding.ItemCodeStatusQueryInfoBinding;

/**
 * @author : J-T
 * @date : 2022/7/22 9:47
 * description : 扫码状态查询 码信息adapter
 */
public class CodeStatusQueryDetailsInfoAdapter extends CustomRecyclerAdapter<CodeStatusQueryInfoItemBean> {
    @Override
    public CustomRecyclerAdapter<CodeStatusQueryInfoItemBean>.ContentViewHolder<? extends ViewDataBinding> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_code_status_query_info, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            CodeStatusQueryInfoItemBean codeStatusQueryInfoItemBean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(codeStatusQueryInfoItemBean);
        }

    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemCodeStatusQueryInfoBinding> {

        ContentType1ViewHolder(ItemCodeStatusQueryInfoBinding binding) {
            super(binding);
        }
    }
}
