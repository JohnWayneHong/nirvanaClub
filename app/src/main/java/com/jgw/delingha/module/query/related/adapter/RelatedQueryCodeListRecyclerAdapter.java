package com.jgw.delingha.module.query.related.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeRelationInfoResultBean;
import com.jgw.delingha.databinding.ItemRelatedQueryLowerCodeBinding;

public class RelatedQueryCodeListRecyclerAdapter extends CustomRecyclerAdapter<CodeRelationInfoResultBean.SonCodeVoListBean> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_related_query_lower_code, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            CodeRelationInfoResultBean.SonCodeVoListBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemRelatedQueryLowerCodeBinding> {

        ContentType1ViewHolder(ItemRelatedQueryLowerCodeBinding binding) {
            super(binding);
        }
    }
}
