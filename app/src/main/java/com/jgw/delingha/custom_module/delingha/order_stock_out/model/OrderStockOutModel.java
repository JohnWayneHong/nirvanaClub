package com.jgw.delingha.custom_module.delingha.order_stock_out.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.bean.OrderStockOutDetailsBean;
import com.jgw.delingha.bean.OrderStockOutListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.bean.OrderStockUploadResultBean;
import com.jgw.delingha.bean.UploadResultBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.utils.CodeTypeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class OrderStockOutModel {
    public LiveData<Resource<List<OrderStockOutListBean.ListBean>>> getOrderStockOutList(String search, int page) {
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        MutableLiveData<Resource<List<OrderStockOutListBean.ListBean>>> liveData = new MutableLiveData<>();
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getDLHOrderStockOutList(map)
                .compose(HttpUtils.applyMainSchedulers())
                .subscribe(new CustomObserver<OrderStockOutListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutListBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, data.list, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<OrderStockOutDetailsBean.ListBean>>> getOrderStockOutDetails(OrderStockOutListBean.ListBean bean) {
        ValueKeeperLiveData<Resource<List<OrderStockOutDetailsBean.ListBean>>> liveData = new ValueKeeperLiveData<>();

        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getDLHOrderStockOutDetails(bean.outHouseList, bean.wareHouseOutId)
                .compose(HttpUtils.applyMainSchedulers())
                .map(orderStockOutDetailsBean -> {
                    bean.linkName=orderStockOutDetailsBean.linkName;
                    bean.linkPhoneNumber=orderStockOutDetailsBean.linkPhoneNumber;
                    bean.countyName=orderStockOutDetailsBean.customerInfoDTO.countyName;
                    bean.cityName=orderStockOutDetailsBean.customerInfoDTO.cityName;
                    bean.provinceName=orderStockOutDetailsBean.customerInfoDTO.provinceName;
                    bean.detailedAddress=orderStockOutDetailsBean.customerInfoDTO.detailedAddress;
                    bean.logisticsCompanyName=orderStockOutDetailsBean.logisticsCompanyName;
                    bean.logisticsNumber=orderStockOutDetailsBean.logisticsNumber;
                    bean.orderNo=orderStockOutDetailsBean.orderNo;
                    return orderStockOutDetailsBean;
                })
                .subscribe(new CustomObserver<OrderStockOutDetailsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutDetailsBean data) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, data.outHouseProducts, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    private ValueKeeperLiveData<Resource<OrderStockScanBean>> mCheckOrderStockInCodeLiveData;

    public LiveData<Resource<OrderStockScanBean>> singleCheckCode(OrderStockOutDetailsBean.ListBean bean, String code) {
        if (mCheckOrderStockInCodeLiveData == null) {
            mCheckOrderStockInCodeLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("outerCode", code);
        map.put("codeTypeId", CodeTypeUtils.getCodeTypeId(code));
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkDLHOrderStockOutCode(map)
                .compose(HttpUtils.applyMainSchedulers())
                .map(json -> {
                    JsonObject jb = JsonUtils.parseObject(json);
//                    if (!TextUtils.equals(jb.getString("productId"), bean.productId)) {
//                        throw new CustomHttpApiException(500, "非当前产品，请更换");
//                    }
                    OrderStockScanBean codeBean = new OrderStockScanBean();
                    codeBean.codeStatus = CodeBean.STATUS_CODE_SUCCESS;
                    codeBean.outerCodeId = jb.getString("outerCode");
                    int codeLevel = jb.getInt("codeLevel");
                    switch (codeLevel) {
                        case 1:
                            codeBean.firstLevel = 1;
                            codeBean.currentUnitName = bean.firstOutNumberUnitName;
                            break;
                        case 2:
                            codeBean.secondLevel = 1;
                            codeBean.currentUnitName = bean.secondOutNumberUnitName;
                            break;
                        case 3:
                            codeBean.thirdLevel = 1;
                            codeBean.currentUnitName = bean.thirdOutNumberUnitName;
                            break;
                    }
                    return codeBean;
                })
                .subscribe(new CustomObserver<OrderStockScanBean>() {

                    @Override
                    public void onNext(OrderStockScanBean data) {
                        data.outerCodeId = code;
                        data.codeStatus = CodeBean.STATUS_CODE_SUCCESS;
                        mCheckOrderStockInCodeLiveData.setValue(new Resource<>(Resource.SUCCESS, data, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        OrderStockScanBean scanBean = new OrderStockScanBean(code);
                        scanBean.codeStatus = CodeBean.STATUS_CODE_FAIL;
                        mCheckOrderStockInCodeLiveData.postValue(new Resource<>(Resource.ERROR, scanBean, e.getMessage()));
                    }
                });
        return mCheckOrderStockInCodeLiveData;
    }

    private ValueKeeperLiveData<Resource<Long>> mCalculationTotalLiveData;

    public LiveData<Resource<Long>> getCalculationTotal(Integer input) {
        if (mCalculationTotalLiveData == null) {
            mCalculationTotalLiveData = new ValueKeeperLiveData<>();
        }
        mCalculationTotalLiveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
        return mCalculationTotalLiveData;
    }

    public LiveData<Resource<String>> saveCheck(List<OrderStockOutDetailsBean.ListBean> input, String houseList) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList", houseList);
        ArrayList<HashMap<String, Object>> value = new ArrayList<>();
        for (OrderStockOutDetailsBean.ListBean b : input) {
            HashMap<String, Object> e = new HashMap<>();
            e.put("amount", b.getCheckAmount());
            e.put("firstNumber", b.getCheckFirstNumber());
            e.put("secondNumber", b.getCheckSecondNumber());
            e.put("thirdNumber", b.getCheckThirdNumber());
            e.put("productRecordId", b.outHouseProductId);
            value.add(e);
        }
        map.put("tallGoods", value);
        HttpUtils.getGatewayApiBigFile(ApiLogisticsService.class)
                .postDLHSave(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String uploadResultBean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, "", ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> upload(OrderStockOutDetailsBean.ListBean input, String houseList) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList",houseList);
        map.put("productRecordId",input.outHouseProductId);
        map.put("amount",input.inputAmount);
        map.put("firstNumber", input.getCheckFirstNumber());
        map.put("secondNumber", input.getCheckSecondNumber());
        map.put("thirdNumber", input.getCheckThirdNumber());
        HttpUtils.getGatewayApiBigFile(ApiLogisticsService.class)
                .postDLHOrderStockOutUploadByProduct(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String uploadResultBean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, "", ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> uploadAll(String houseList) {
        ValueKeeperLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();

        HashMap<String, Object> map = new HashMap<>();
        map.put("houseList",houseList);
        HttpUtils.getGatewayApiBigFile(ApiLogisticsService.class)
                .postDLHOrderStockOutUpload(map)
                .compose(HttpUtils.applyResultNullableMainSchedulers())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String uploadResultBean) {
                        liveData.setValue(new Resource<>(Resource.SUCCESS, "", ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }
}
