package com.jgw.delingha.module.select_list.warehouse_list;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.module.select_list.common.SelectItemSupport;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.WareHouseOperator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class WareHouseSelectListModel {

    public LiveData<Resource<List<? extends SelectItemSupport>>> getWareHouseList(String searchStr, int current) {
        ValueKeeperLiveData<Resource<List<? extends SelectItemSupport>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(new WareHouseOperator())
                .map(operator -> operator.queryDataBySearchStr(searchStr, current, CustomRecyclerAdapter.ITEM_PAGE_SIZE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<WareHouseEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<WareHouseEntity> customerEntities) {
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
