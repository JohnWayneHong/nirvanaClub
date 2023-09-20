package com.jgw.delingha.sql.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ItemCommonScanInputBinding;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

public class CodeEntityRecyclerAdapter<T extends BaseCodeEntity> extends CustomRecyclerAdapter<T> {
    @Override
    public ContentViewHolder<? extends ViewDataBinding> onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_common_scan_input, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass() == ContentType1ViewHolder.class) {
            BaseCodeEntity bean = getContentItemData(position);
            //noinspection unchecked
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }

    }

    public void notifyRefreshItemStatus(String code, int status) {
        notifyRefreshItemStatus(code, status, 0);
    }

    public void notifyRefreshItemStatus(String code, int status, int number) {
        int index = -1;
        for (int i = 0; i < mList.size(); i++) {
            BaseCodeEntity e = mList.get(i);
            if (TextUtils.equals(e.getCode(), code)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return;
        }
        BaseCodeEntity baseCodeEntity = mList.get(index);
        baseCodeEntity.setCodeStatus(status);
        baseCodeEntity.setSingleNumber(number);
        notifyItemChanged(getHeaderCount() + index);
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemCommonScanInputBinding> {

        ContentType1ViewHolder(ItemCommonScanInputBinding binding) {
            super(binding);
        }
    }
}
