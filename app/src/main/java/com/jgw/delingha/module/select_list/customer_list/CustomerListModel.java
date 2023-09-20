package com.jgw.delingha.module.select_list.customer_list;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.ToastUtils;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.operator.CustomerOperator;
import com.jgw.delingha.utils.ConstantUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Cxz
 * data : 2019/11/20
 * description : 顾客列表Model
 */
public class CustomerListModel {

    private final String mCurrentCustomId = MMKVUtils.getString(ConstantUtil.CURRENT_CUSTOMER_ID);

    public LiveData<Resource<List<? extends SelectItemSupport>>> getCustomerList(String searchStr, int customerType, int currentPage) {
        ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> liveData = new ValueKeeperLiveData<>();

        Observable.just("whatever")
                .map(s -> {
                    if (TextUtils.equals(mCurrentCustomId, "null")) {
                        ToastUtils.showToast("获取信息失败,请更新数据");
                        throw new CustomHttpApiException(500, "获取信息失败,请更新数据");
                    }
                    List<CustomerEntity> customerEntities;
                    if (customerType == CustomerListActivity.TYPE_ALL) {
                        customerEntities = requestCustomerList(searchStr, null, currentPage);
                    } else if (customerType == CustomerListActivity.TYPE_ADMIN_ALL) {
                        //总部时获取全部 客户获取一级
                        String customId = TextUtils.isEmpty(mCurrentCustomId) ? null : mCurrentCustomId;
                        customerEntities = requestCustomerList(searchStr, customId, currentPage);
                    } else {
                        //获取非全部客户时如果是总部 就查询一级
                        String customId = TextUtils.isEmpty(mCurrentCustomId) ? MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID) : mCurrentCustomId;
                        customerEntities = requestCustomerList(searchStr, customId, currentPage);
                    }
                    return customerEntities;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<CustomerEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<CustomerEntity> customerEntities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, customerEntities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    private List<CustomerEntity> requestCustomerList(String searchStr, String currentCustomId, int page) {
        CustomerOperator customerOperator = new CustomerOperator();
        return customerOperator.queryCustomerListByUpperAndSearch(searchStr, currentCustomId, page, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
    }
}
