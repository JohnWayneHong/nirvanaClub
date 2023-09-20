package com.jgw.delingha.module.query.code_status.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.R;
import com.jgw.delingha.bean.BaseCodeStatusQueryBean;
import com.jgw.delingha.bean.CodeStatusQueryDetailsLabelBean;
import com.jgw.delingha.bean.CodeStatusQueryExchangeGoodsBean;
import com.jgw.delingha.bean.CodeStatusQueryExchangeWarehouseBean;
import com.jgw.delingha.bean.CodeStatusQueryInfoItemBean;
import com.jgw.delingha.bean.CodeStatusQueryStockInBean;
import com.jgw.delingha.bean.CodeStatusQueryStockOutBean;
import com.jgw.delingha.bean.CodeStatusQueryStockReturnBean;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : J-T
 * @date : 2022/7/21 11:19
 * description : 扫码状态查询Model
 */
public class CodeStatusQueryDetailModel {
    public static final String STOCK_IN_LIST = "stock_in_list";
    public static final String STOCK_OUT_LIST = "stock_out_list";
    public static final String CHANGE_WAREHOUSE_LIST = "exchange_warehouse_list";
    public static final String CHANGE_GOODS_LIST = "exchange_goods_list";
    public static final String STOCK_RETURN_LIST = "stock_return_list";

    private final Map<String, List<CodeStatusQueryInfoItemBean>> map = new HashMap<>(5);

    private ValueKeeperLiveData<Resource<List<CodeStatusQueryInfoItemBean>>> getInfoLiveData;
    private ValueKeeperLiveData<Resource<List<CodeStatusQueryDetailsLabelBean>>> initLabelLiveData;

    public MutableLiveData<Resource<List<CodeStatusQueryDetailsLabelBean>>> getLabelList() {
        initLabelLiveData = new ValueKeeperLiveData<>();
        Observable.just("whatever")
                .map(s -> initLabel()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<CodeStatusQueryDetailsLabelBean>>() {
                    @Override
                    public void onNext(List<CodeStatusQueryDetailsLabelBean> list) {
                        initLabelLiveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }
                });
        return initLabelLiveData;
    }

    private List<CodeStatusQueryDetailsLabelBean> initLabel() {
        CodeStatusQueryDetailsLabelBean bean;
        ArrayList<CodeStatusQueryDetailsLabelBean> list = new ArrayList<>();

        bean = new CodeStatusQueryDetailsLabelBean(R.drawable.icon_status_query_stock_in_blue,
                R.drawable.icon_status_query_stock_in_grey, "入库信息");
        list.add(bean);

        bean = new CodeStatusQueryDetailsLabelBean(R.drawable.icon_status_query_stock_out_blue,
                R.drawable.icon_status_query_stock_out_grey, "出库信息");
        list.add(bean);

        bean = new CodeStatusQueryDetailsLabelBean(R.drawable.icon_status_query_exchange_warehouse_blue,
                R.drawable.icon_status_query_exchange_warehouse_grey, "调仓信息");
        list.add(bean);

        bean = new CodeStatusQueryDetailsLabelBean(R.drawable.icon_status_query_exchange_goods_blue,
                R.drawable.icon_status_query_exchange_goods_grey, "调货信息");
        list.add(bean);

        bean = new CodeStatusQueryDetailsLabelBean(R.drawable.icon_status_query_stock_return_blue,
                R.drawable.icon_status_query_stock_return_grey, "退货信息");
        list.add(bean);
        return list;
    }


    public LiveData<Resource<List<CodeStatusQueryInfoItemBean>>> getInfo(String code, String key) {
        if (getInfoLiveData == null) {
            getInfoLiveData = new ValueKeeperLiveData<>();
        }

        List<CodeStatusQueryInfoItemBean> list = map.get(key);
        if (list != null) {
            getInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
            return getInfoLiveData;
        }
        Observable<HttpResult<String>> observableByKey = getObservableByKey(code, key);
        observableByKey.compose(HttpUtils.applyMainSchedulers())
                .map(s -> convertDataBean(s, key))
                .subscribe(new CustomObserver<BaseCodeStatusQueryBean>() {
                    @Override
                    public void onNext(BaseCodeStatusQueryBean bean) {
                        List<CodeStatusQueryInfoItemBean> infoList = bean.getInfoList();
                        map.put(key, infoList);
                        getInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, infoList, ""));
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        getInfoLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        getInfoLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return getInfoLiveData;
    }

    private Observable<HttpResult<String>> getObservableByKey(String code, String key) {
        ApiLogisticsService api = HttpUtils.getGatewayApi(ApiLogisticsService.class);
        Observable<HttpResult<String>> httpResultObservable;
        switch (key) {
            case STOCK_IN_LIST:
                httpResultObservable = api.queryRecentIn(code);
                break;
            case STOCK_OUT_LIST:
                httpResultObservable = api.queryRecentOut(code);
                break;
            case CHANGE_WAREHOUSE_LIST:
                httpResultObservable = api.queryRecentRelocate(code);
                break;
            case CHANGE_GOODS_LIST:
                httpResultObservable = api.queryRecentTransfer(code);
                break;
            case STOCK_RETURN_LIST:
                httpResultObservable = api.queryRecentReturn(code);
                break;
            default:
                httpResultObservable = api.queryRecentIn(code);
        }
        return httpResultObservable;
    }

    private BaseCodeStatusQueryBean convertDataBean(String sourceString, String key) {
        BaseCodeStatusQueryBean bean = null;
        switch (key) {
            case STOCK_IN_LIST:
                bean = JsonUtils.parseObject(sourceString, CodeStatusQueryStockInBean.class);
                break;
            case STOCK_OUT_LIST:
                bean = JsonUtils.parseObject(sourceString, CodeStatusQueryStockOutBean.class);
                break;
            case CHANGE_WAREHOUSE_LIST:
                bean = JsonUtils.parseObject(sourceString, CodeStatusQueryExchangeWarehouseBean.class);
                break;
            case CHANGE_GOODS_LIST:
                bean = JsonUtils.parseObject(sourceString, CodeStatusQueryExchangeGoodsBean.class);
                break;
            case STOCK_RETURN_LIST:
                bean = JsonUtils.parseObject(sourceString, CodeStatusQueryStockReturnBean.class);
                break;
        }
        return bean;
    }
}
