package com.jgw.delingha.module.tools_table.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.ToolsOptionsBean;
import com.jgw.delingha.bean.ToolsTableHeaderBean;
import com.jgw.delingha.databinding.HeaderToolsTableBinding;
import com.jgw.delingha.databinding.ItemToolsTableBinding;
import com.jgw.delingha.view.marqueeview.MarqueeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongShaoWu
 * on 2019/9/12
 */
public class ToolsTableRecyclerAdapter extends CustomRecyclerAdapter<ToolsOptionsBean> {


    private ToolsTableHeaderBean mHeaderData;

    public ToolsTableRecyclerAdapter() {
    }

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new MyHeaderViewHolder(DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.header_tools_table, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.item_tools_table, parent, false));
        }

        return null;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_CONTENT1;
        }
    }

    @Override
    public int getHeaderCount() {
        return 1;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            ToolsOptionsBean bean = mList.get(position - 1);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            List<ToolsOptionsBean.MobileFunsBean> newList = new ArrayList<>(bean.mobileFuns);
            ((ContentType1ViewHolder) holder).mItemAdapter.notifyRefreshList(newList);
        } else if (holder instanceof MyHeaderViewHolder) {
            ((MyHeaderViewHolder) holder).mBindingView.setData(mHeaderData);
            if (mHeaderData == null || mHeaderData.getSystemRenewalReminderVisible() == View.GONE) {
                return;
            }
            List<CharSequence> list = mHeaderData.getSystemRenewalReminderText();

            //noinspection unchecked
            MarqueeView<CharSequence> marqueeView = ((MyHeaderViewHolder) holder).mBindingView.mvToolsTableRenewalReminder;
            List<CharSequence> messages = marqueeView.getMessages();
            if (list.size() != messages.size()) {
                marqueeView.startWithList(list);
            } else if (!list.isEmpty()) {
                boolean isChange = false;
                for (int i = 0; i < list.size(); i++) {
                    if (!TextUtils.equals(list.get(i), messages.get(i))) {
                        isChange = true;
                        break;
                    }
                }
                if (isChange) {
                    marqueeView.startWithList(list);
                }
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof MyHeaderViewHolder) {
            ((MyHeaderViewHolder) holder).mBindingView.mvToolsTableRenewalReminder.startFlipping();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof MyHeaderViewHolder) {
            ((MyHeaderViewHolder) holder).mBindingView.mvToolsTableRenewalReminder.stopFlipping();
        }
    }

    public void setHeaderData(ToolsTableHeaderBean toolsTableHeaderData) {
        mHeaderData = toolsTableHeaderData;
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemToolsTableBinding> {

        private final ToolsTableItemRecyclerAdapter mItemAdapter;

        public ContentType1ViewHolder(ItemToolsTableBinding binding) {
            super(binding);
            binding.rvToolsTableItem.setGridVerticalLayoutManager(3, mContext, false);
            binding.rvToolsTableItem.setFocusable(false);
            mItemAdapter = new ToolsTableItemRecyclerAdapter();
            mItemAdapter.setOnItemClickListener(this);
            binding.rvToolsTableItem.setAdapter(mItemAdapter);
        }

        @Override
        public void onItemClick(View view, int position) {
            super.onItemClick(view, position);
            if (mListener != null) {
                mListener.onItemClick(view, getAdapterPosition(), position);
            }
        }
    }

    private class MyHeaderViewHolder extends CustomHeaderViewHolder<HeaderToolsTableBinding> {

        public MyHeaderViewHolder(HeaderToolsTableBinding binding) {
            super(binding);
        }
    }

}
