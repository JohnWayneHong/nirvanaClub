package com.jgw.delingha.custom_module.delingha.breed.task.list;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.utils.click_utils.ClickUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BreedTaskListBean;
import com.jgw.delingha.databinding.ItemBreedTaskListBinding;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;


/**
 * @author : hwj
 * @date : 2023-8-8 10:03:40
 * description : 养殖任务 列表 Adapter
 */
public class BreedTaskListRecyclerAdapter extends CustomRecyclerAdapter<SelectItemSupport> {

    @Override
    public ContentViewHolder onCreateCustomViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CONTENT1) {
            return new ContentType1ViewHolder(DataBindingUtil.inflate(mLayoutInflater
                    , R.layout.item_breed_task_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindCustomViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentType1ViewHolder) {
            SelectItemSupport selectItemSupport = mList.get(position);
            BreedTaskListBean listBean=null;
            if (selectItemSupport instanceof BreedTaskListBean){
                listBean= (BreedTaskListBean) selectItemSupport;
            }
            ((ContentType1ViewHolder) holder).mBindingView.setData(listBean);
        }
    }

    private class ContentType1ViewHolder extends ContentViewHolder<ItemBreedTaskListBinding> {

        ContentType1ViewHolder(ItemBreedTaskListBinding binding) {
            super(binding);
            ClickUtils.register(this)
                    .addItemClickListener()
                    .addView(binding.tvBreedTaskListComplete)
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
