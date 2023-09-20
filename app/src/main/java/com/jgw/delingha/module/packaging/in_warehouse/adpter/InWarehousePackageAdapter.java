package com.jgw.delingha.module.packaging.in_warehouse.adpter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.databinding.ItemInWarehousePackageScanBinding;
import com.jgw.delingha.sql.entity.InWarehousePackageEntity;

/**
 * author : Cxz
 * data : 2019/12/18
 * description :
 */
public class InWarehousePackageAdapter extends CustomRecyclerAdapter<InWarehousePackageEntity> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new InWarehousePackageAdapter.ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_in_warehouse_package_scan, parent, false));
        }
        return null;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InWarehousePackageAdapter.ContentType1ViewHolder) {
            InWarehousePackageEntity bean = mList.get(position);
            ((InWarehousePackageAdapter.ContentType1ViewHolder) holder).mBindingView.setData(bean);
            switch (bean.getCodeStatus()) {
                case CodeBean.STATUS_CODE_VERIFYING:
                    ((ContentType1ViewHolder) holder).mBindingView.tvChecking.setVisibility(View.VISIBLE);
                    ((ContentType1ViewHolder) holder).mBindingView.ivCheckOk.setVisibility(View.GONE);
                    break;
                case CodeBean.STATUS_CODE_SUCCESS:
                    ((ContentType1ViewHolder) holder).mBindingView.tvChecking.setVisibility(View.GONE);
                    ((ContentType1ViewHolder) holder).mBindingView.ivCheckOk.setVisibility(View.VISIBLE);
                    break;
                case CodeBean.STATUS_CODE_FAIL:
                    ((ContentType1ViewHolder) holder).mBindingView.tvChecking.setVisibility(View.VISIBLE);
                    ((ContentType1ViewHolder) holder).mBindingView.ivCheckOk.setVisibility(View.GONE);
                    break;
            }
        }
    }


    private class ContentType1ViewHolder extends ContentViewHolder<ItemInWarehousePackageScanBinding> {

        ContentType1ViewHolder(ItemInWarehousePackageScanBinding binding) {
            super(binding);
        }

    }
}
