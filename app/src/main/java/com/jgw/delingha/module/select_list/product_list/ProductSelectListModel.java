package com.jgw.delingha.module.select_list.product_list;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.operator.ProductInfoOperator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class ProductSelectListModel {

    public LiveData<Resource<List<? extends SelectItemSupport>>> getProductList(@Nullable String searchStr, int current) {

        ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> liveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> {
                    ProductInfoOperator operator = new ProductInfoOperator();
                    return operator.queryWareHouseProductBySearchStr(searchStr, current, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<ProductInfoEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<ProductInfoEntity> customerEntities) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, customerEntities, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }
}
