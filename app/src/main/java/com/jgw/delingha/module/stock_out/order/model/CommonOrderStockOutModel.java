package com.jgw.delingha.module.stock_out.order.model;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.jgw.common_library.base.adapter.CustomRecyclerAdapter;
import com.jgw.common_library.http.CustomHttpApiException;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.FormatUtils;
import com.jgw.common_library.utils.NetUtils;
import com.jgw.common_library.utils.json.JsonObject;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.CommonOrderStockOutDetailsBean;
import com.jgw.delingha.bean.CommonOrderStockOutListBean;
import com.jgw.delingha.bean.OrderStockScanBean;
import com.jgw.delingha.module.order_task.TaskModel;
import com.jgw.delingha.module.task_list.viewmodel.TaskListViewModel;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiLogisticsService;
import com.jgw.delingha.network.result.HttpResult;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.BaseOrderEntity;
import com.jgw.delingha.sql.entity.BaseOrderScanCodeEntity;
import com.jgw.delingha.sql.entity.OrderStockOutEntity;
import com.jgw.delingha.sql.entity.OrderStockOutProductInfoEntity;
import com.jgw.delingha.sql.entity.OrderStockOutScanCodeEntity;
import com.jgw.delingha.sql.operator.OrderStockOutOperator;
import com.jgw.delingha.sql.operator.OrderStockOutProductOperator;
import com.jgw.delingha.sql.operator.OrderStockOutScanCodeOperator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.objectbox.relation.ToMany;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by XiongShaoWu
 * on 2020/7/7
 */
public class CommonOrderStockOutModel {

    private final OrderStockOutOperator mOrderStockOutOperator;
    private final OrderStockOutProductOperator mOrderProductOperator;
    private final OrderStockOutScanCodeOperator mOrderScanCodeOperator;
    private final TaskModel mTaskModel;
    private ValueKeeperLiveData<Resource<List<CommonOrderStockOutListBean>>> mOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<List<? extends BaseOrderEntity>>> mLocalOrderStockOutListLiveData;
    private ValueKeeperLiveData<Resource<OrderStockOutProductInfoEntity>> mUpdateProductInfoLiveData;
    private ValueKeeperLiveData<Resource<OrderStockOutProductInfoEntity>> mCheckScanCodeNumberLiveData;
    private ValueKeeperLiveData<Resource<OrderStockOutProductInfoEntity>> mInputNumberLiveData;
    private ValueKeeperLiveData<Resource<String>> mRefreshProductCodeNumberLiveData;

    public CommonOrderStockOutModel() {
        mTaskModel = new TaskModel();
        mOrderStockOutOperator = new OrderStockOutOperator();
        mOrderProductOperator = new OrderStockOutProductOperator();
        mOrderScanCodeOperator = new OrderStockOutScanCodeOperator();
    }

    public LiveData<Resource<List<CommonOrderStockOutListBean>>> getOrderStockOutList(String search, int page) {
        if (mOrderStockOutListLiveData == null) {
            mOrderStockOutListLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(search)) {
            map.put("search", search);
        }
        map.put("current", page);
        map.put("pageSize", CustomRecyclerAdapter.ITEM_PAGE_SIZE);
        //noinspection ConstantConditions
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getCommonOrderStockOutList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(s -> JsonUtils.parseArray(s).toJavaList(CommonOrderStockOutListBean.class))
                .subscribe(new CustomObserver<List<CommonOrderStockOutListBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mOrderStockOutListLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<CommonOrderStockOutListBean> data) {
                        mOrderStockOutListLiveData.postValue(new Resource<>(Resource.SUCCESS, data, ""));

                    }


                    @Override
                    public void onError(Throwable e) {
                        mOrderStockOutListLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                        super.onError(e);
                    }
                });
        return mOrderStockOutListLiveData;
    }

    /**
     * 获取订单信息
     *
     * @param bean 来自订单列表的订单数据
     */
    public LiveData<Resource<OrderStockOutEntity>> getOrderStockOut(CommonOrderStockOutListBean bean) {
        ValueKeeperLiveData<Resource<OrderStockOutEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(bean)
                .map(bean1 -> {
                    OrderStockOutEntity orderStockOutEntity = mOrderStockOutOperator.queryEntityByCode(bean1.houseList);
                    if (orderStockOutEntity != null) {
                        if (!TextUtils.isEmpty(orderStockOutEntity.getUpdateTime()) &&
                                !TextUtils.equals(bean.updateTime, orderStockOutEntity.getUpdateTime())) {
                            orderStockOutEntity.setIsInvalid(true);
                            orderStockOutEntity.setUpdateTime(bean1.updateTime);
                            mOrderStockOutOperator.putData(orderStockOutEntity);
                        }
                    } else {
                        OrderStockOutEntity entity = new OrderStockOutEntity();
                        entity.setCreateTime(bean1.createTime);
                        entity.setWarehouse(bean1.warehouse);
                        entity.setUpdateTime(bean1.updateTime);
                        entity.setOrderCode(bean1.houseList);
                        entity.setReceiveOrganizationName(bean1.receiveOrganizationName);
                        entity.setReceiveOrganizationCode(bean1.receiveOrganizationCode);
                        entity.setOrderId(bean1.id);
                        long userId = LocalUserUtils.getCurrentUserId();
                        entity.getUserEntity().setTargetId(userId);
                        orderStockOutEntity = entity;
                    }
                    return orderStockOutEntity;

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<OrderStockOutEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));

                    }

                    @Override
                    public void onNext(OrderStockOutEntity orderStockOutEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, orderStockOutEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });

        return liveData;
    }

    /**
     * 获取订单信息
     *
     * @param orderId 数据库订单id
     */
    public LiveData<Resource<OrderStockOutEntity>> getOrderStockOut(long orderId) {
        ValueKeeperLiveData<Resource<OrderStockOutEntity>> liveData = new ValueKeeperLiveData<>();
        Observable.just(orderId)
                .map(mOrderStockOutOperator::queryDataById)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<OrderStockOutEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));

                    }

                    @Override
                    public void onNext(OrderStockOutEntity orderStockOutEntity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, orderStockOutEntity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });

        return liveData;
    }

    /**
     * 获取订单详情
     */
    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> getOrderStockOutDetails(OrderStockOutEntity orderStockOutEntity) {
        ValueKeeperLiveData<Resource<List<OrderStockOutProductInfoEntity>>> liveData = new ValueKeeperLiveData<>();
        String createTime = orderStockOutEntity.getCreateTime();
        Date date = FormatUtils.decodeDate(createTime, FormatUtils.FULL_TIME_PATTERN);
        String yyyy = FormatUtils.formatDate(date, "yyyy");
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .getOrderStockOutDetails(orderStockOutEntity.getOrderCode(), orderStockOutEntity.getOrderId(), yyyy)
                .compose(HttpUtils.applyMainSchedulers())
                .map((Function<CommonOrderStockOutDetailsBean, List<OrderStockOutProductInfoEntity>>) bean -> {
                    ArrayList<OrderStockOutProductInfoEntity> temp = new ArrayList<>();
                    for (CommonOrderStockOutDetailsBean.ListBean b : bean.houseListProducts) {
                        OrderStockOutProductInfoEntity entity = new OrderStockOutProductInfoEntity();
                        entity.setProductName(b.getProductName());
                        entity.setProductCode(b.getProductCode());
                        entity.setProductId(b.getProductId());
                        entity.setBatchName(b.getProductBatch());
                        entity.setBatchId(b.getProductBatchId());
                        entity.setPlanNumber(b.getSingleCodeNumber());
                        entity.setWareHouseName(b.getWareHouseName());
                        entity.setWareHouseId(b.getWareHouseId());
                        entity.setWareHouseCode(b.getWareHouseCode());
                        entity.setScanCodeNumber(b.getActualSingleCodeNumber());
                        entity.setProductRecordId(b.getProductRecordId());
                        temp.add(entity);
                    }
                    return temp;
                })
                .flatMap((Function<List<OrderStockOutProductInfoEntity>, ObservableSource<OrderStockOutProductInfoEntity>>)
                        this::getOrderStockOutProductInfoEntityObservable)
                .subscribe(new CustomObserver<OrderStockOutProductInfoEntity>() {
                    final List<OrderStockOutProductInfoEntity> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutProductInfoEntity data) {
                        list.add(data);
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }


    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> onOrderInfoChange(OrderStockOutEntity entity, List<OrderStockOutProductInfoEntity> mList) {
        ValueKeeperLiveData<Resource<List<OrderStockOutProductInfoEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(entity1 -> {
                    List<OrderStockOutProductInfoEntity> productList = entity1.getProductList();
                    ArrayList<OrderStockOutProductInfoEntity> tempProducts = new ArrayList<>();
                    for (OrderStockOutProductInfoEntity b : productList) {
                        boolean contain = false;
                        for (OrderStockOutProductInfoEntity b1 : mList) {
                            if (TextUtils.equals(b.getProductId(), b1.getProductId())) {
                                b1.setId(b.getId());
                                contain = true;
                                break;
                            }
                        }
                        if (contain) {
                            continue;
                        }
                        tempProducts.add(b);
                        //noinspection rawtypes
                        List scanCodeList = b.getScanCodeList();
                        //noinspection unchecked
                        mOrderScanCodeOperator.removeData(scanCodeList);
                    }
                    mOrderProductOperator.removeData(tempProducts);
                    mOrderProductOperator.putData(mList);
                    entity1.setIsInvalid(false);
                    mOrderStockOutOperator.putData(entity1);
                    return entity1.getProductList();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<OrderStockOutProductInfoEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<OrderStockOutProductInfoEntity> s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> insertOrderData(OrderStockOutEntity input, List<OrderStockOutProductInfoEntity> mList) {
        ValueKeeperLiveData<Resource<List<OrderStockOutProductInfoEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(input)
                .flatMap((Function<OrderStockOutEntity, ObservableSource<OrderStockOutEntity>>) entity ->
                        mTaskModel.createTask(entity.getOrderCode(), TaskListViewModel.TYPE_TASK_STOCK_OUT)
                                .map(s -> {
                                    entity.setTaskId(s);
                                    return entity;
                                }))
                .map(entity1 -> {
                    entity1.setOperatingTime(System.currentTimeMillis());
                    mOrderStockOutOperator.putData(entity1);
                    return entity1;
                })
                .map(aLong -> {
                    for (OrderStockOutProductInfoEntity b : mList) {
                        b.getOrderStockOutEntity().setTargetId(aLong.getId());//设置订单产品关联的订单id
                        b.setTaskId(aLong.getTaskId());
                    }
                    mOrderProductOperator.putData(mList);
                    return aLong.getProductList();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<OrderStockOutProductInfoEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<OrderStockOutProductInfoEntity> s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    public void insertCodeEntity(OrderStockOutScanCodeEntity entity) {
        mOrderScanCodeOperator.putData(entity);
    }

    private ValueKeeperLiveData<Resource<OrderStockOutScanCodeEntity>> mCheckOrderStockOutCodeLiveData;

    public LiveData<Resource<OrderStockOutScanCodeEntity>> singleCheckCode(OrderStockOutProductInfoEntity bean, String code) {
        if (mCheckOrderStockOutCodeLiveData == null) {
            mCheckOrderStockOutCodeLiveData = new ValueKeeperLiveData<>();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("productId", bean.getProductId());
        if (!TextUtils.isEmpty(bean.getBatchId())) {
            map.put("productBatchId", bean.getBatchId());
        }
        map.put("wareHouseId", bean.getWareHouseId());
        map.put("outerCodeId", code);
        map.put("productRecordId", bean.getProductRecordId());
        map.put("taskId", bean.getTaskId());
        HttpUtils.getGatewayApi(ApiLogisticsService.class)
                .checkCommonOrderStockOutCode(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map((Function<OrderStockScanBean, OrderStockOutScanCodeEntity>) orderStockScanBean -> {
                    OrderStockOutScanCodeEntity entity = mOrderScanCodeOperator.queryEntityByCode(code);
                    entity.setCodeStatus(BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS);
                    entity.setSingleNumber(orderStockScanBean.singleNumber);
                    entity.setCodeLevel(orderStockScanBean.currentLevel);
                    mOrderScanCodeOperator.putData(entity);
                    return entity;
                })
                .subscribe(new CustomObserver<OrderStockOutScanCodeEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCheckOrderStockOutCodeLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutScanCodeEntity data) {
                        mCheckOrderStockOutCodeLiveData.postValue(new Resource<>(Resource.SUCCESS, data, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        OrderStockOutScanCodeEntity data = mOrderScanCodeOperator.queryEntityByCode(code);
                        data.setCodeStatus(BaseCodeEntity.STATUS_CODE_FAIL);
                        mOrderScanCodeOperator.putData(data);
                        if (!NetUtils.iConnected()) {
                            mCheckOrderStockOutCodeLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, data, "网络繁忙，验证失败，请重新扫码"));
                            return;
                        }
                        if (e instanceof CustomHttpApiException && ((CustomHttpApiException) e).getApiExceptionCode() == 500) {
                            mCheckOrderStockOutCodeLiveData.postValue(new Resource<>(Resource.ERROR, data, e.getMessage()));
                        } else {
                            super.onError(e);
                            mCheckOrderStockOutCodeLiveData.postValue(new Resource<>(Resource.NETWORK_ERROR, data, "网络繁忙，验证失败，请重新扫码"));
                        }
                    }
                });
        return mCheckOrderStockOutCodeLiveData;
    }

    public LiveData<Resource<List<OrderStockScanBean>>> groupCheckCode(OrderStockOutProductInfoEntity bean, List<String> input) {
        ValueKeeperLiveData<Resource<List<OrderStockScanBean>>> liveData = new ValueKeeperLiveData<>();
        Observable.fromIterable(input)
                .map(code -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("productId", bean.getProductId());
                    if (!TextUtils.isEmpty(bean.getBatchId())) {
                        map.put("productBatchId", bean.getBatchId());
                    }
                    map.put("wareHouseId", bean.getWareHouseId());
                    map.put("outerCodeId", code);
                    map.put("productRecordId", bean.getProductRecordId());
                    map.put("taskId", bean.getTaskId());
                    OrderStockOutScanCodeEntity entity = mOrderScanCodeOperator.queryEntityByCode(code);
                    HttpResult<OrderStockScanBean> httpResult = HttpUtils.getGatewayApi(ApiLogisticsService.class)
                            .checkCommonOrderStockOutCode(map)
                            .blockingSingle();
                    if (httpResult != null) {
                        if (httpResult.state == 200) {
                            OrderStockScanBean results = httpResult.results;
                            results.outerCodeId = code;
                            results.codeStatus = BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS;
                            entity.setCodeStatus(BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS);
                            entity.setSingleNumber(results.singleNumber);
                            entity.setCodeLevel(results.currentLevel);
                            mOrderScanCodeOperator.putData(entity);
                            return results;
                        } else if (httpResult.state == 500) {
                            OrderStockScanBean bean1 = new OrderStockScanBean(code);
                            bean1.codeStatus = BaseOrderScanCodeEntity.STATUS_CODE_FAIL;
                            mOrderScanCodeOperator.removeData(entity);
                            return bean1;
                        } else {
                            OrderStockScanBean bean1 = new OrderStockScanBean(code);
                            entity.setCodeStatus(BaseOrderScanCodeEntity.STATUS_CODE_FAIL);
                            mOrderScanCodeOperator.putData(entity);
                            return bean1;
                        }
                    } else {
                        return new OrderStockScanBean(code);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<OrderStockScanBean>() {
                    List<OrderStockScanBean> list = new ArrayList<>();

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockScanBean uploadResultBean) {
                        list.add(uploadResultBean);
                    }

                    @Override
                    public void onComplete() {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }


    private ValueKeeperLiveData<Resource<Long>> mCalculationTotalLiveData;

    public LiveData<Resource<Long>> getCalculationTotal(Long productId) {
        if (mCalculationTotalLiveData == null) {
            mCalculationTotalLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(productId)
                .map(mOrderScanCodeOperator::queryCountByProductId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<Long>() {
                    @Override
                    public void onNext(Long l) {
                        mCalculationTotalLiveData.postValue(new Resource<>(Resource.SUCCESS, l, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mCalculationTotalLiveData.postValue(new Resource<>(Resource.ERROR, 0L, e.getMessage()));
                    }
                });
        return mCalculationTotalLiveData;
    }


    public LiveData<Resource<OrderStockOutProductInfoEntity>> getProductInfo(long input) {
        if (mUpdateProductInfoLiveData == null) {
            mUpdateProductInfoLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(input)
                .map(mOrderProductOperator::queryDataById)
                .flatMap((Function<OrderStockOutProductInfoEntity, ObservableSource<OrderStockOutProductInfoEntity>>) this::getUpdateProductScanCodeNumberObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<OrderStockOutProductInfoEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUpdateProductInfoLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutProductInfoEntity entity) {
                        mUpdateProductInfoLiveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUpdateProductInfoLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return mUpdateProductInfoLiveData;
    }

    public void updateCodeStatus(OrderStockScanBean bean, int status) {
        //更新头部数据从数据库获取 所以需要同步更新完成才能刷新界面
        OrderStockOutScanCodeEntity entity = mOrderScanCodeOperator.queryEntityByCode(bean.outerCodeId);
        entity.setCodeStatus(status);
        if (status == BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS) {
            entity.setSingleNumber(bean.singleNumber);
            entity.setCodeLevel(bean.currentLevel);
            entity.setCodeLevelName(bean.currentLevel + "");
        }
        mOrderScanCodeOperator.putData(entity);
    }

    public void removeEntityByCode(String code) {
        mOrderScanCodeOperator.removeEntityByCode(code);
    }

    public OrderStockOutScanCodeEntity queryEntityByCode(String code) {
        return mOrderScanCodeOperator.queryEntityByCode(code);
    }

    private ValueKeeperLiveData<Resource<List<BaseOrderScanCodeEntity>>> mGetOrderProductCodeListLiveData;

    public LiveData<Resource<List<BaseOrderScanCodeEntity>>> getOrderProductCodeList(Long productId, int mPage) {
        if (mGetOrderProductCodeListLiveData == null) {
            mGetOrderProductCodeListLiveData = new ValueKeeperLiveData<>();
        }

        Observable.just("whatever")
                .map(s -> {

                    List<OrderStockOutScanCodeEntity> temp = mOrderScanCodeOperator.queryDataByPage(productId, mPage, CustomRecyclerAdapter.ITEM_PAGE_SIZE);
                    return new ArrayList<BaseOrderScanCodeEntity>(temp);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<BaseOrderScanCodeEntity>>() {
                    @Override
                    public void onNext(List<BaseOrderScanCodeEntity> list) {
                        mGetOrderProductCodeListLiveData.postValue(new Resource<>(Resource.SUCCESS, list, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        mGetOrderProductCodeListLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return mGetOrderProductCodeListLiveData;
    }

    public LiveData<Resource<List<OrderStockOutProductInfoEntity>>> getLocalProductList(OrderStockOutEntity entity) {
        ValueKeeperLiveData<Resource<List<OrderStockOutProductInfoEntity>>> liveData = new ValueKeeperLiveData<>();
        Observable.just(entity)
                .map(OrderStockOutEntity::getProductList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<List<OrderStockOutProductInfoEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(List<OrderStockOutProductInfoEntity> entity) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return liveData;
    }

    private ValueKeeperLiveData<Resource<String>> mUploadLiveData;

    public LiveData<Resource<String>> upload(OrderStockOutEntity entity) {
        if (mUploadLiveData == null) {
            mUploadLiveData = new ValueKeeperLiveData<>();
        }
        mTaskModel.executeTask(entity.getTaskId(), TaskListViewModel.TYPE_TASK_STOCK_OUT, 1)
                .map(s -> {
                    ToMany<OrderStockOutProductInfoEntity> productList = entity.getProductList();
                    for (OrderStockOutProductInfoEntity e : productList) {
                        ToMany<OrderStockOutScanCodeEntity> codeList = e.getCodeList();
                        if (codeList.isEmpty()) {
                            continue;
                        }
                        ArrayList<OrderStockOutScanCodeEntity> temp = new ArrayList<>();
                        for (OrderStockOutScanCodeEntity e1 : codeList) {
                            if (e1.getCodeStatus() == BaseCodeEntity.STATUS_CODE_SUCCESS) {
                                temp.add(e1);
                            }
                        }
                        mOrderScanCodeOperator.removeData(temp);
                    }
                    return s;
                })
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mUploadLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        mUploadLiveData.setValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mUploadLiveData.setValue(new Resource<>(Resource.ERROR, null, ""));
                    }
                });
        return mUploadLiveData;
    }

    public LiveData<Resource<OrderStockOutProductInfoEntity>> inputNumber(OrderStockOutProductInfoEntity bean) {
        if (mInputNumberLiveData == null) {
            mInputNumberLiveData = new ValueKeeperLiveData<>();
        }

        mTaskModel.editInputNumber(bean.getTempInputSingleNumber(), bean.getProductRecordId(), bean.getTaskId())
                .map(s -> {
                    bean.setCurrentInputSingleNumber(bean.getTempInputSingleNumber());
                    mOrderProductOperator.putData(bean);
                    return s;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mInputNumberLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String data) {
                        mInputNumberLiveData.setValue(new Resource<>(Resource.SUCCESS, bean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mInputNumberLiveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return mInputNumberLiveData;
    }

    public LiveData<Resource<OrderStockOutProductInfoEntity>> checkScanCodeNumber(OrderStockOutProductInfoEntity bean) {
        if (mCheckScanCodeNumberLiveData == null) {
            mCheckScanCodeNumberLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(bean)
                .filter(entity -> {
                    int scanCodeSingleNumber = entity.getScanCodeSingleNumber();
                    boolean needUpdate = scanCodeSingleNumber != entity.getCurrentSingleNumber();
                    if (!needUpdate) {
                        mCheckScanCodeNumberLiveData.setValue(new Resource<>(Resource.SUCCESS, entity, ""));
                    }
                    return needUpdate;
                })
                .flatMap((Function<OrderStockOutProductInfoEntity, ObservableSource<OrderStockOutProductInfoEntity>>) entity1 ->
                        mTaskModel.getOrderProductAllCodeList(entity1.getTaskId(), entity1.getProductRecordId())
                                .map(orderStockScanBeans -> {
                                    List<OrderStockOutScanCodeEntity> scanCodeEntityList = mOrderScanCodeOperator.queryAllDataByProductId(entity1.getId());

                                    ArrayList<OrderStockOutScanCodeEntity> tempRemove = new ArrayList<>();
                                    ArrayList<OrderStockOutScanCodeEntity> tempUpdate = new ArrayList<>();
                                    ArrayList<OrderStockOutScanCodeEntity> tempAdd = new ArrayList<>();
                                    for (OrderStockOutScanCodeEntity o : scanCodeEntityList) {
                                        int indexOf = orderStockScanBeans.indexOf(o);
                                        if (indexOf == -1) {
                                            if (o.getCodeStatus() == BaseCodeEntity.STATUS_CODE_SUCCESS) {
                                                tempRemove.add(o);
                                            }
                                            continue;
                                        }
                                        OrderStockOutScanCodeEntity scanBean = orderStockScanBeans.get(indexOf);
                                        if (o.getCodeStatus() != BaseCodeEntity.STATUS_CODE_SUCCESS) {
                                            o.setCodeStatus(BaseCodeEntity.STATUS_CODE_SUCCESS);
                                            o.setSingleNumber(scanBean.getSingleNumber());
                                            o.setCodeLevel(scanBean.getCodeLevel());
                                            tempUpdate.add(o);
                                        }
                                    }
                                    for (OrderStockOutScanCodeEntity o : orderStockScanBeans) {
                                        if (scanCodeEntityList.contains(o)) {
                                            continue;
                                        }
                                        o.getOrderStockOutProductInfoEntity().setTargetId(entity1.getId());
                                        tempAdd.add(o);
                                    }
                                    mOrderScanCodeOperator.removeData(tempRemove);
                                    mOrderScanCodeOperator.putData(tempUpdate);
                                    mOrderScanCodeOperator.putData(tempAdd);
                                    return entity1;
                                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<OrderStockOutProductInfoEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCheckScanCodeNumberLiveData.setValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutProductInfoEntity data) {
                        mCheckScanCodeNumberLiveData.setValue(new Resource<>(Resource.SUCCESS, bean, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mCheckScanCodeNumberLiveData.setValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
        return mCheckScanCodeNumberLiveData;
    }


    private Observable<OrderStockOutProductInfoEntity> getUpdateProductScanCodeNumberObservable(OrderStockOutProductInfoEntity entity) {
        if (!NetUtils.iConnected()){
            return Observable.just(entity);
        }
        return mTaskModel.getOrderProductCurrentCodeInfo(entity.getProductRecordId())
                .map(s -> {
                    JsonObject jsonObject = JsonUtils.parseObject(s);
                    //noinspection ConstantConditions
                    int currentInputSingleNumber = jsonObject.getInt("enterNumber");
                    entity.setCurrentInputSingleNumber(currentInputSingleNumber);
                    int currentSingleNumber = jsonObject.getInt("singleNumber");
                    entity.setCurrentSingleNumber(currentSingleNumber);
                    mOrderProductOperator.putData(entity);
                    return entity;
                });
    }

    public LiveData<Resource<String>> refreshProductCodeNumber(OrderStockOutEntity input) {
        if (mRefreshProductCodeNumberLiveData == null) {
            mRefreshProductCodeNumberLiveData = new ValueKeeperLiveData<>();
        }
        Observable.just(input)
                .observeOn(Schedulers.io())
                .map((Function<OrderStockOutEntity, List<OrderStockOutProductInfoEntity>>) OrderStockOutEntity::getProductList)
                .flatMap((Function<List<OrderStockOutProductInfoEntity>, ObservableSource<OrderStockOutProductInfoEntity>>)
                        this::getOrderStockOutProductInfoEntityObservable)
                .map(entity -> {
                    mOrderProductOperator.putData(entity);
                    return entity;
                })
                .subscribe(new CustomObserver<OrderStockOutProductInfoEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mRefreshProductCodeNumberLiveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(OrderStockOutProductInfoEntity data) {

                    }

                    @Override
                    public void onComplete() {
                        mRefreshProductCodeNumberLiveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!(e instanceof CustomHttpApiException)) {
                            mRefreshProductCodeNumberLiveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
                            return;
                        }
                        super.onError(e);
                        mRefreshProductCodeNumberLiveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));

                    }
                });
        return mRefreshProductCodeNumberLiveData;
    }

    private Observable<OrderStockOutProductInfoEntity> getOrderStockOutProductInfoEntityObservable(List<OrderStockOutProductInfoEntity> orderStockOutDetailsBean) {
        return Observable.fromIterable(orderStockOutDetailsBean)
                .flatMap(bean1 ->
                        mTaskModel.getOrderProductCurrentCodeInfo(bean1.getProductRecordId())
                                .map(s -> {
                                    JsonObject jsonObject = JsonUtils.parseObject(s);
                                    //noinspection ConstantConditions
                                    int currentInputSingleNumber = jsonObject.getInt("enterNumber");
                                    bean1.setCurrentInputSingleNumber(currentInputSingleNumber);
                                    int currentSingleNumber = jsonObject.getInt("singleNumber");
                                    bean1.setCurrentSingleNumber(currentSingleNumber);
                                    return bean1;
                                }));
    }

}
