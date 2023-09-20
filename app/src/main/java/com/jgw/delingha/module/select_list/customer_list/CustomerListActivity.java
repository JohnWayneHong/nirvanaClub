package com.jgw.delingha.module.select_list.customer_list;

import android.app.Activity;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.databinding.ActivityCommonSelectListBinding;
import com.jgw.delingha.module.select_list.common.BaseSelectItemListActivity;

public class CustomerListActivity extends BaseSelectItemListActivity<CustomerListViewModel, ActivityCommonSelectListBinding> implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EXTRA_NAME = "customer_id";

    //全部下级
    public static final int TYPE_ALL = 0;
    //直接下属客户
    public static final int TYPE_UNDERLING = 1;
    //总部全部 客户直接下属客户
    public static final int TYPE_ADMIN_ALL = 2;

    @Override
    protected void initData() {
        setTitle(ResourcesUtils.getString(R.string.customer_select_title));
        int customerType = getIntent().getIntExtra("customerType", -1);
        mViewModel.setCustomerType(customerType);
        super.initData();
    }

    @Override
    public String getExtraName() {
        return EXTRA_NAME;
    }

    public static void start(int requestCode, Activity context, int customerType) {
        if (customerType == -1) {
            ToastUtils.showToast("类型异常");
            return;
        }
        if (isActivityNotFinished(context)) {
            Intent intent = new Intent(context, CustomerListActivity.class);
            intent.putExtra("customerType", customerType);
            context.startActivityForResult(intent, requestCode);
        }
    }

}
