package com.jgw.delingha.module.logistics_statistics.statistics_list;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsResultBean;
import com.jgw.delingha.databinding.ItemStatisticalExchangeGoodsBinding;
import com.jgw.delingha.databinding.ItemStatisticalExchangeWarehouseBinding;
import com.jgw.delingha.databinding.ItemStatisticalStockInBinding;
import com.jgw.delingha.databinding.ItemStatisticalStockOutBinding;
import com.jgw.delingha.databinding.ItemStatisticalStockReturnBinding;

/**
 * author : Cxz
 * data : 2019/11/20
 * description : 用户类别Item Adapter
 */
public class StatisticalListRecyclerAdapter extends CustomRecyclerAdapter<StatisticsResultBean> {

    private int mType;

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_statistical_stock_in, parent, false));
        }else if (viewType == ITEM_TYPE_CONTENT2) {
            return new ContentType2ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_statistical_stock_out, parent, false));
        }else if (viewType == ITEM_TYPE_CONTENT3) {
            return new ContentType3ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_statistical_stock_return, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT4) {
            return new ContentType4ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_statistical_exchange_warehouse, parent, false));
        }else if (viewType == ITEM_TYPE_CONTENT5) {
            return new ContentType5ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_statistical_exchange_goods, parent, false));
        }return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            StatisticsResultBean bean = mList.get(position);
            ((ContentType1ViewHolder) holder).mBindingView.setData(bean);
        }else if (holder instanceof ContentType2ViewHolder){
            StatisticsResultBean bean = mList.get(position);
            ((ContentType2ViewHolder) holder).mBindingView.setData(bean);
        }else if (holder instanceof ContentType3ViewHolder){
            StatisticsResultBean bean = mList.get(position);
            ((ContentType3ViewHolder) holder).mBindingView.setData(bean);
        }else if (holder instanceof ContentType4ViewHolder){
            StatisticsResultBean bean = mList.get(position);
            ((ContentType4ViewHolder) holder).mBindingView.setData(bean);
        }else if (holder instanceof ContentType5ViewHolder){
            StatisticsResultBean bean = mList.get(position);
            ((ContentType5ViewHolder) holder).mBindingView.setData(bean);
        }
    }

    public void setType(int type) {
        mType = type;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public int getItemViewType(int position) {

        switch (mType) {
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_IN:
                return ITEM_TYPE_CONTENT1;
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_OUT:
                return ITEM_TYPE_CONTENT2;
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_RETURN:
                return ITEM_TYPE_CONTENT3;
            case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE:
                return ITEM_TYPE_CONTENT4;
            case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_GOODS:
                return ITEM_TYPE_CONTENT5;
            default:
                return ITEM_TYPE_CONTENT1;
        }
    }

        

    private class ContentType1ViewHolder extends ContentViewHolder<ItemStatisticalStockInBinding> {

        ContentType1ViewHolder(ItemStatisticalStockInBinding binding) {
            super(binding);
        }
    }
    private class ContentType2ViewHolder extends ContentViewHolder<ItemStatisticalStockOutBinding> {
        ContentType2ViewHolder(ItemStatisticalStockOutBinding binding) {
            super(binding);
        }
    }
    private class ContentType3ViewHolder extends ContentViewHolder<ItemStatisticalStockReturnBinding> {
        ContentType3ViewHolder(ItemStatisticalStockReturnBinding binding) {
            super(binding);
        }
    }
    private class ContentType4ViewHolder extends ContentViewHolder<ItemStatisticalExchangeWarehouseBinding> {
        ContentType4ViewHolder(ItemStatisticalExchangeWarehouseBinding binding) {
            super(binding);
        }
    }
    private class ContentType5ViewHolder extends ContentViewHolder<ItemStatisticalExchangeGoodsBinding> {
        ContentType5ViewHolder(ItemStatisticalExchangeGoodsBinding binding) {
            super(binding);
        }
    }
}
