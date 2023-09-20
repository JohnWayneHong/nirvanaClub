package com.jgw.delingha.module.fail_log.adapter;

import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_EXCHANGE_GOODS;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_EXCHANGE_WAREHOUSE;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_LABEL_EDIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_PACKAGING_ASSOCIATION;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_RELATE_TO_NFC;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_GROUP_SPLIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_IN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_OUT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_RETURN;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_SINGLE_SPLIT;
import static com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel.TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FailLogBean;
import com.jgw.delingha.databinding.ItemFailLogBinding;
import com.jgw.delingha.databinding.ItemFailLogExchangeGoodsBinding;
import com.jgw.delingha.databinding.ItemFailLogExchangeWarehouseBinding;
import com.jgw.delingha.databinding.ItemFailLogPackagingAssociationBinding;
import com.jgw.delingha.databinding.ItemFailLogRelateToNfcBinding;
import com.jgw.delingha.databinding.ItemFailLogStockOutBinding;
import com.jgw.delingha.databinding.ItemFailLogStockReturnBinding;

public class FailLogListRecyclerAdapter extends CustomRecyclerAdapter<FailLogBean.ListBean> {

    private int mTaskType;

    public FailLogListRecyclerAdapter(int t) {
        mTaskType = t;
    }

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT2) {
            return new ContentType2ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log_packaging_association, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT3) {
            return new ContentType3ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log_exchange_warehouse, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT4) {
            return new ContentType4ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log_exchange_goods, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT5) {
            return new ContentType5ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log_stock_out, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT6) {
            return new ContentType6ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log_stock_return, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT7) {
            return new ContentType7ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_fail_log_relate_to_nfc, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EmptyViewHolder) {
            return;
        }
        FailLogBean.ListBean bean = mList.get(position);
        if (holder instanceof ContentType1ViewHolder) {
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
            switch (mTaskType) {
                case TYPE_TASK_STOCK_IN:
                case TYPE_TASK_STOCK_OUT:
                case TYPE_TASK_STOCK_RETURN:
                    break;
                case TYPE_TASK_STOCK_GROUP_SPLIT:
                case TYPE_TASK_STOCK_SINGLE_SPLIT:
                case TYPE_TASK_STOCK_SUPPLEMENT_TO_BOX:
                    ((ContentType1ViewHolder) holder).mBindingView.llProduct.setVisibility(View.GONE);
                    ((ContentType1ViewHolder) holder).mBindingView.llWarehouse.setVisibility(View.GONE);
                    break;
                case TYPE_TASK_LABEL_EDIT:
                    ((ContentType1ViewHolder) holder).mBindingView.llWarehouse.setVisibility(View.GONE);
                    break;
            }
        } else if (holder instanceof ContentType2ViewHolder) {
            ((ContentType2ViewHolder) holder).mBindingView.setData(bean);
        } else if (holder instanceof ContentType3ViewHolder) {
            ((ContentType3ViewHolder) holder).mBindingView.setData(bean);
        } else if (holder instanceof ContentType4ViewHolder) {
            ((ContentType4ViewHolder) holder).mBindingView.setData(bean);
        } else if (holder instanceof ContentType5ViewHolder) {
            ((ContentType5ViewHolder) holder).mBindingView.setData(bean);
        } else if (holder instanceof ContentType6ViewHolder) {
            ((ContentType6ViewHolder) holder).mBindingView.setData(bean);
        } else if (holder instanceof ContentType7ViewHolder) {
            ((ContentType7ViewHolder) holder).mBindingView.setData(bean);
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch (mTaskType) {
            case TYPE_TASK_PACKAGING_ASSOCIATION:
                return ITEM_TYPE_CONTENT2;
            case TYPE_TASK_EXCHANGE_WAREHOUSE:
                return ITEM_TYPE_CONTENT3;
            case TYPE_TASK_EXCHANGE_GOODS:
                return ITEM_TYPE_CONTENT4;
            case TYPE_TASK_STOCK_OUT:
                return ITEM_TYPE_CONTENT5;
            case TYPE_TASK_STOCK_RETURN:
                return ITEM_TYPE_CONTENT6;
            case TYPE_TASK_RELATE_TO_NFC:
                return ITEM_TYPE_CONTENT7;
            default:
                return ITEM_TYPE_CONTENT1;
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemFailLogBinding> {

        ContentType1ViewHolder(ItemFailLogBinding binding) {
            super(binding);
        }
    }

    private class ContentType2ViewHolder extends ContentViewHolder<ItemFailLogPackagingAssociationBinding> {

        ContentType2ViewHolder(ItemFailLogPackagingAssociationBinding binding) {
            super(binding);
        }
    }

    private class ContentType3ViewHolder extends ContentViewHolder<ItemFailLogExchangeWarehouseBinding> {

        ContentType3ViewHolder(ItemFailLogExchangeWarehouseBinding binding) {
            super(binding);
        }
    }

    private class ContentType4ViewHolder extends ContentViewHolder<ItemFailLogExchangeGoodsBinding> {
        ContentType4ViewHolder(ItemFailLogExchangeGoodsBinding binding) {
            super(binding);
        }
    }

    private class ContentType5ViewHolder extends ContentViewHolder<ItemFailLogStockOutBinding> {
        ContentType5ViewHolder(ItemFailLogStockOutBinding binding) {
            super(binding);
        }
    }

    private class ContentType6ViewHolder extends ContentViewHolder<ItemFailLogStockReturnBinding> {
        ContentType6ViewHolder(ItemFailLogStockReturnBinding binding) {
            super(binding);
        }
    }

    private class ContentType7ViewHolder extends ContentViewHolder<ItemFailLogRelateToNfcBinding> {
        ContentType7ViewHolder(ItemFailLogRelateToNfcBinding binding) {
            super(binding);
        }
    }
}
