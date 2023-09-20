package com.jgw.delingha.module.query.flow.adpter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.FlowQueryBean;
import com.jgw.delingha.databinding.ItemLogisticsFlowDetailBinding;
import com.jgw.delingha.databinding.ItemProductInfoBinding;

/**
 * author : Cxz
 * data : 2019/11/29
 * description :
 */
public class FlowQueryListAdapter extends CustomRecyclerAdapter<FlowQueryBean.FlowBean> {

    private FlowQueryBean flowQueryBean;

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new ContentTypeViewHolder(DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.item_product_info, parent, false));
        } else if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_logistics_flow_detail, parent, false));
        }
        return null;
    }
    public void setData(FlowQueryBean f){
        flowQueryBean=f;
    }

    @Override
    public int getItemViewType(int position) {
      if (0 == position) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_CONTENT1;
        }
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (flowQueryBean==null){
            return;
        }
        if (holder instanceof ContentTypeViewHolder) {
            ((ContentTypeViewHolder) holder).mBindingView.setData(flowQueryBean);
            if (TextUtils.isEmpty(flowQueryBean.productBatch)) {
                ((ContentTypeViewHolder) holder).mBindingView.llBatch.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(flowQueryBean.product == null ? "" : flowQueryBean.product.productSpecificationsName)) {
                ((ContentTypeViewHolder) holder).mBindingView.llNorm.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(flowQueryBean.packageSpecification)) {
                ((ContentTypeViewHolder) holder).mBindingView.llPackageSpec.setVisibility(View.GONE);
            }
        } else if (holder instanceof ContentType1ViewHolder) {
            ((ContentType1ViewHolder) holder).mBindingView.setData(mList.get(position - 1));
            if (mList.size() == position) {
                ((ContentType1ViewHolder) holder).mBindingView.line.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getHeaderCount() {
        return 1;
    }

    private class ContentTypeViewHolder extends ContentViewHolder<ItemProductInfoBinding> {

        ContentTypeViewHolder(ItemProductInfoBinding binding) {
            super(binding);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemLogisticsFlowDetailBinding> {

        ContentType1ViewHolder(ItemLogisticsFlowDetailBinding binding) {
            super(binding);
        }
    }

}
