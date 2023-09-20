package com.jgw.delingha.module.logistics_statistics.statistical_dimension;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.StatisticsParamsBean;
import com.jgw.delingha.module.logistics_statistics.statistics_list.StatisticsListActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author : Cxz
 * data : 2019/11/20
 * description : 物流公司列表Model
 */
public class StatisticalDimensionListModel {
    public LiveData<Resource<List<StatisticsParamsBean.StatisticalDimension>>> getStatisticalDimensionList(int type, String searchStr, int currentPage) {
        ValueKeeperLiveData<Resource<List<StatisticsParamsBean.StatisticalDimension>>> liveData = new ValueKeeperLiveData<>();

        Observable.just("whatever")
                .map(s -> getStatisticalDimensionListByType(type))
                .flatMap((Function<List<StatisticsParamsBean.StatisticalDimension>, ObservableSource<StatisticsParamsBean.StatisticalDimension>>) Observable::fromIterable)
                .filter(statisticalDimension -> TextUtils.isEmpty(searchStr) || statisticalDimension.dimensionName.contains(searchStr))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<StatisticsParamsBean.StatisticalDimension>() {
                    final List<StatisticsParamsBean.StatisticalDimension> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull StatisticsParamsBean.StatisticalDimension bean) {
                        list.add(bean);
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    private List<StatisticsParamsBean.StatisticalDimension> getStatisticalDimensionListByType(int type) throws CustomHttpApiException {
        ArrayList<StatisticsParamsBean.StatisticalDimension> list = null;
        switch (type) {
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_IN:
                list = getStockInStatisticalDimensionList();
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_OUT:
                list = getStockOutStatisticalDimensionList();
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_STOCK_RETURN:
                list = getStockReturnStatisticalDimensionList();
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_WAREHOUSE:
                list = getExchangeWarehouseStatisticalDimensionList();
                break;
            case StatisticsListActivity.STATISTICAL_DIMENSION_EXCHANGE_GOODS:
                list = getExchangeGoodsStatisticalDimensionList();
                break;
        }
        if (list == null) {
            throw new CustomHttpApiException(500, "数据异常");
        }
        return list;
    }

    private ArrayList<StatisticsParamsBean.StatisticalDimension> getStockInStatisticalDimensionList() {
        ArrayList<StatisticsParamsBean.StatisticalDimension> list = new ArrayList<>();
        StatisticsParamsBean.StatisticalDimension bean;
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.product_name);
        bean.dimensionId = "product_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.batch_name);
        bean.dimensionId = "product_batch_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.warehouse_name);
        bean.dimensionId = "ware_house_id";
        list.add(bean);
        return list;
    }

    private ArrayList<StatisticsParamsBean.StatisticalDimension> getStockOutStatisticalDimensionList() {
        ArrayList<StatisticsParamsBean.StatisticalDimension> list = new ArrayList<>();
        StatisticsParamsBean.StatisticalDimension bean;
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.product_name);
        bean.dimensionId = "product_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.batch_name);
        bean.dimensionId = "product_batch_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.customer_name);
        bean.dimensionId = "receive_organization_id";
        list.add(bean);
        return list;
    }

    private ArrayList<StatisticsParamsBean.StatisticalDimension> getStockReturnStatisticalDimensionList() {
        ArrayList<StatisticsParamsBean.StatisticalDimension> list = new ArrayList<>();
        StatisticsParamsBean.StatisticalDimension bean;
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.product_name);
        bean.dimensionId = "product_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.batch_name);
        bean.dimensionId = "product_batch_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.customer_name);
        bean.dimensionId = "return_customer_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.warehouse_name);
        bean.dimensionId = "in_ware_house_id";
        list.add(bean);
        return list;
    }

    private ArrayList<StatisticsParamsBean.StatisticalDimension> getExchangeWarehouseStatisticalDimensionList() {
        ArrayList<StatisticsParamsBean.StatisticalDimension> list = new ArrayList<>();
        StatisticsParamsBean.StatisticalDimension bean;
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.product_name);
        bean.dimensionId = "product_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.warehouse_out_name);
        bean.dimensionId = "out_ware_house_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName =  ResourcesUtils.getString(R.string.warehouse_in_name);
        bean.dimensionId = "in_ware_house_id";
        list.add(bean);
        return list;
    }

    private ArrayList<StatisticsParamsBean.StatisticalDimension> getExchangeGoodsStatisticalDimensionList() {
        ArrayList<StatisticsParamsBean.StatisticalDimension> list = new ArrayList<>();
        StatisticsParamsBean.StatisticalDimension bean;
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.product_name);
        bean.dimensionId = "product_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.customer_out_name);
        bean.dimensionId = "ware_goods_out_id";
        list.add(bean);
        bean = new StatisticsParamsBean.StatisticalDimension();
        bean.dimensionName = ResourcesUtils.getString(R.string.customer_in_name);
        bean.dimensionId = "ware_goods_in_id";
        list.add(bean);
        return list;
    }
}
