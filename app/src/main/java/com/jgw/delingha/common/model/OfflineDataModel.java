package com.jgw.delingha.common.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jgw.common_library.base.CustomApplication;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.http.rxjava.CustomObserver;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.common_library.utils.BuildConfigUtils;
import com.jgw.common_library.utils.LogUtils;
import com.jgw.common_library.utils.MMKVUtils;
import com.jgw.common_library.utils.json.JsonUtils;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.bean.WareHouseBean;
import com.jgw.delingha.common.AppConfig;
import com.jgw.delingha.module.select_list.customer_list.CustomerListActivity;
import com.jgw.delingha.network.HttpUtils;
import com.jgw.delingha.network.api.ApiMyService;
import com.jgw.delingha.network.api.ApiService;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.ObjectBoxUtils;
import com.jgw.delingha.sql.converter_bean.AllBatchInfoBean;
import com.jgw.delingha.sql.converter_bean.CustomerBean;
import com.jgw.delingha.sql.converter_bean.LogisticsCompanyBean;
import com.jgw.delingha.sql.converter_bean.MyCustomerBean;
import com.jgw.delingha.sql.converter_bean.ProductBean;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.LogisticsCompanyEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.ProductPackageInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.UserEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;
import com.jgw.delingha.sql.operator.CustomerOperator;
import com.jgw.delingha.sql.operator.LogisticsCompanyOperator;
import com.jgw.delingha.sql.operator.ProductBatchOperator;
import com.jgw.delingha.sql.operator.ProductInfoOperator;
import com.jgw.delingha.sql.operator.ProductPackageOperator;
import com.jgw.delingha.sql.operator.StorePlaceOperator;
import com.jgw.delingha.sql.operator.UserOperator;
import com.jgw.delingha.sql.operator.WareHouseOperator;
import com.jgw.delingha.utils.ConstantUtil;
import com.jgw.delingha.utils.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by XiongShaoWu
 * on 2020/5/6
 */
public class OfflineDataModel {

    private final WareHouseOperator mWareHouseOperator = new WareHouseOperator();
    private final StorePlaceOperator mStorePlaceOperator = new StorePlaceOperator();
    private final CustomerOperator mCustomerOperator = new CustomerOperator();
    private final LogisticsCompanyOperator mLogisticsCompanyOperator = new LogisticsCompanyOperator();
    private final ProductInfoOperator mProductInfoOperator = new ProductInfoOperator();
    private final ProductPackageOperator mProductPackageOperator = new ProductPackageOperator();
    private final ProductBatchOperator mProductBatchOperator = new ProductBatchOperator();


    public LiveData<Resource<String>> requestCurrentCustomerInfo() {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        HttpUtils.getGatewayApi(ApiService.class)
                .getMyCustomerInfo()
                .compose(HttpUtils.applyMainSchedulers())
                .safeSubscribe(new CustomObserver<MyCustomerBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(@NonNull MyCustomerBean myCustomerBean) {
                        if (!TextUtils.isEmpty(myCustomerBean.customerId)) {
                            MMKVUtils.save(ConstantUtil.CURRENT_CUSTOMER_ID, myCustomerBean.customerId);
                            MMKVUtils.save(ConstantUtil.CURRENT_CUSTOMER_TYPE, 0);
                        } else {
                            MMKVUtils.save(ConstantUtil.CURRENT_CUSTOMER_ID, "");
                            MMKVUtils.save(ConstantUtil.CURRENT_CUSTOMER_TYPE, 1);
                        }
                        updateOfflineData(liveData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                        MMKVUtils.save(ConstantUtil.CURRENT_CUSTOMER_ID, "null");
                    }
                });
        return liveData;
    }

    private void updateOfflineData(MutableLiveData<Resource<String>> liveData) {
        Observable.zip(getWareHouseList(), getStorePlaceList(), getCustomerList()
                        , getLogisticsCompanyList(), getProductList(), getProductPackageList(), getProductBatchList(),
                        (wareHouseBean, storePlaceList, customerBean, logisticsCompanyBean, productBean, productPackageBEan, batchBean) -> {
                            if (productBean.list != null) {
                                //商品和包装信息
                                initProductPackageData(productBean.list, productPackageBEan.list);
                            }
                            return "更新成功";
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                        if (LogUtils.getShowLogEnable()) {
                            long l = mCustomerOperator.queryCount();
                            LogUtils.xswShowLog("mCustomerOperator=" + l);
                            long l1 = mLogisticsCompanyOperator.queryCount();
                            LogUtils.xswShowLog("mLogisticsCompanyOperator=" + l1);
                            long l2 = mProductBatchOperator.queryCount();
                            LogUtils.xswShowLog("mProductBatchOperator=" + l2);
                            long l3 = mProductInfoOperator.queryCount();
                            LogUtils.xswShowLog("mProductInfoOperator=" + l3);
                            long l4 = mProductPackageOperator.queryCount();
                            LogUtils.xswShowLog("mProductPackageOperator=" + l4);
                            long l5 = mWareHouseOperator.queryCount();
                            LogUtils.xswShowLog("mWareHouseOperator=" + l5);
                            long l6 = mStorePlaceOperator.queryCount();
                            LogUtils.xswShowLog("mStorePlaceOperator=" + l6);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        liveData.postValue(new Resource<>(Resource.ERROR, null, e.getMessage()));
                    }
                });
    }

    private void initProductBatchData(List<AllBatchInfoBean.ListBean> list) {
        ArrayList<ProductBatchEntity> productBatchEntities = new ArrayList<>();
        for (AllBatchInfoBean.ListBean listBean : list) {
            AllBatchInfoBean.ListBean.ObjectMapBean b = listBean.objectMap;
            ProductBatchEntity productBatchEntity = new ProductBatchEntity();
            productBatchEntity.setBatchId(b.batchId);
            productBatchEntity.setBatchCode(b.batchCode);
            productBatchEntity.setBatchName(b.batchName);
            productBatchEntity.setProductId(b.productId);
            productBatchEntity.setProductName(b.productName);

            int index = productBatchEntities.indexOf(productBatchEntity);
            if (index != -1) {
                continue;
            }
            productBatchEntities.add(productBatchEntity);
        }
        mProductBatchOperator.saveData(productBatchEntities);
    }

    /**
     * 获取商品和商品包装信息存入数据库
     * 因为商品包装信息会影响商品的包装属性 所以必须等商品获取到之后 再获取商品包装信息然后对对应的商品更新状态
     *
     * @param data 商品信息
     * @param list 商品包装信息
     */
    private void initProductPackageData(List<ProductBean.Data> data, List<ProductWareHouseBean.ListBean> list) {
        final long requestTimeTime = System.currentTimeMillis();
        HashMap<String, ProductInfoEntity> productInfoEntities = new HashMap<>();
        for (ProductBean.Data b : data) {
            if (b.disableFlag != 1) {
                continue;
            }
            ProductInfoEntity productInfoEntity = new ProductInfoEntity();
            productInfoEntity.setProductId(b.productId);
            productInfoEntity.setProductCode(b.productCode);
            productInfoEntity.setProductName(b.productName);
            productInfoEntity.setProductSortId(b.productSortId);
            productInfoEntity.setProductSortName(b.productSortName);
            productInfoEntity.setHaveWarehouse(false);
            productInfoEntity.setHavePackaged(false);
            productInfoEntities.put(b.productId, productInfoEntity);
        }
        long wheelTimeTime1 = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductList wheelTimeTime111" + (wheelTimeTime1 - requestTimeTime));
        ArrayList<ProductPackageInfoEntity> productPackageInfoEntities = new ArrayList<>();
        for (ProductWareHouseBean.ListBean b : list) {
            ProductInfoEntity productInfoEntity = productInfoEntities.get(b.productId);
            if (productInfoEntity == null) {
                //无对应商品
                continue;
            }
            boolean have_warehouse = TextUtils.equals(b.sweepCodeIn, "Y");
            productInfoEntity.setHaveWarehouse(have_warehouse);
            boolean have_packaged = b.packageLevel > 1;
            productInfoEntity.setHavePackaged(have_packaged);

            if (TextUtils.isEmpty(b.productPackageRatios)) {
                //没有包装层级
                continue;
            }

            ProductPackageInfoEntity productPackageInfoEntity = new ProductPackageInfoEntity();
            productPackageInfoEntity.setProductId(b.productId);
            productPackageInfoEntity.setProductWareHouseId(b.productWareHouseId);
            productPackageInfoEntity.setPackageRestricted(TextUtils.equals(b.isPackageRestricted, "Y") ? 1 : 0);

            productPackageInfoEntity.setProductPackageRatios(b.productPackageRatios);

            productPackageInfoEntity.setProductPackageName("");
            productPackageInfoEntities.add(productPackageInfoEntity);
        }
        long wheelTimeTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductList wheelTimeTime" + (wheelTimeTime - requestTimeTime));

        mProductInfoOperator.saveData(new ArrayList<>(productInfoEntities.values()));
        long insertTimeTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductList insertTimeTime" + (insertTimeTime - wheelTimeTime));

        mProductPackageOperator.saveData(productPackageInfoEntities);
        long insertTimeTime2 = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductPackageList insertTimeTime" + (insertTimeTime2 - insertTimeTime));

    }

    private void initLogisticsCompanyData(List<LogisticsCompanyBean.ListBean> list) {
        ArrayList<LogisticsCompanyEntity> logisticsCompanyEntities = new ArrayList<>();
        for (LogisticsCompanyBean.ListBean b : list) {
            LogisticsCompanyEntity logisticsCompanyEntity = new LogisticsCompanyEntity();
            logisticsCompanyEntity.setShipperName(b.shipperName);
            logisticsCompanyEntity.setShipperCode(b.shipperCode);

            int index = logisticsCompanyEntities.indexOf(logisticsCompanyEntity);
            if (index != -1) {
                continue;
            }
            logisticsCompanyEntities.add(logisticsCompanyEntity);
        }
        mLogisticsCompanyOperator.saveData(logisticsCompanyEntities);
    }

    private void initCustomerData(List<CustomerBean> list) {
        HashSet<CustomerEntity> customerEntities = new HashSet<>();
        for (CustomerBean b : list) {
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setCustomerId(b.customerId);
            customerEntity.setCustomerCode(b.customerCode);
            customerEntity.setCustomerName(b.customerName);
            customerEntity.setContacts(b.contacts);
            customerEntity.setCustomerSuperior(b.customerSuperior);
            customerEntity.setCustomerSuperiorName(b.customerSuperiorName);
            customerEntities.add(customerEntity);
        }
        mCustomerOperator.saveData(new ArrayList<>(customerEntities));
    }

    private void initStorePlaceData(List<StorePlaceEntity> storePlaceList) {
        List<StorePlaceEntity> list = new ArrayList<>();
        for (StorePlaceEntity s : storePlaceList) {
            if (s.getDisableFlag() != 1) {
                continue;
            }
            int index = list.indexOf(s);
            if (index != -1) {
                continue;
            }
            list.add(s);
        }
        mStorePlaceOperator.saveData(list);
    }

    private void initWareHouseData(WareHouseBean wareHouseBean) {
        ArrayList<WareHouseEntity> wareHouseEntities = new ArrayList<>();
        for (WareHouseBean.Data b : wareHouseBean.list) {
            if (b.disableFlag != 1) {
                continue;
            }
            WareHouseEntity wareHouseEntity = new WareHouseEntity();
            wareHouseEntity.setWareHouseId(b.wareHouseId);
            wareHouseEntity.setWareHouseCode(b.wareHouseCode);
            wareHouseEntity.setWareHouseName(b.wareHouseName);
            int index = wareHouseEntities.indexOf(wareHouseEntity);
            if (index != -1) {
                continue;
            }
            wareHouseEntities.add(wareHouseEntity);
        }
        mWareHouseOperator.saveData(wareHouseEntities);
    }


    /**
     * 企业全部的仓库列表
     */
    private Observable<WareHouseBean> getWareHouseList() {
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getWareHouseList startTime" + startTime);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getWareHouseList("", 1, 100000)
                .compose(HttpUtils.applyIOSchedulers())
                .map(wareHouseBean -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getWareHouseList requestTimeTime" + (requestTimeTime - startTime));
                    if (wareHouseBean.list != null) {
                        //仓库
                        initWareHouseData(wareHouseBean);
                    }
                    final long insertTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getWareHouseList insertTimeTime" + (insertTimeTime - requestTimeTime));

                    return wareHouseBean;
                });
    }

    /**
     * 企业全部的库位列表
     */
    private Observable<List<StorePlaceEntity>> getStorePlaceList() {
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getStorePlaceList startTime" + startTime);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getAllStorePlaceList(1, 100000)
                .compose(HttpUtils.applyIOSchedulers())
                .map(storePlaceEntities -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getStorePlaceList requestTimeTime" + (requestTimeTime - startTime));
                    if (storePlaceEntities != null) {
                        //库位
                        initStorePlaceData(storePlaceEntities);
                    }
                    final long insertTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getStorePlaceList insertTimeTime" + (insertTimeTime - requestTimeTime));

                    return storePlaceEntities;
                });
    }


    /**
     * 当前用户的全部下级用户
     */
    private Observable<List<CustomerBean>> getCustomerList() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", CustomerListActivity.TYPE_ALL);
        map.put("current", 1);
        map.put("pageSize", 100000);
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getCustomerList startTime" + startTime);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getCustomerList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map((Function<String, List<CustomerBean>>) s -> {
                    if (TextUtils.isEmpty(s)) {
                        return new ArrayList<>();
                    }
                    //noinspection ConstantConditions
                    return JsonUtils.parseObject(s).getJsonArray("list").toJavaList(CustomerBean.class);
                })
                .map(list -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getCustomerList requestTimeTime" + (requestTimeTime - startTime));
                    initCustomerData(list);
                    final long insertTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getCustomerList insertTimeTime" + (insertTimeTime - requestTimeTime));

                    return list;
                });
    }

    /**
     * 全部的物流公司
     */
    private Observable<LogisticsCompanyBean> getLogisticsCompanyList() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("current", 1);
        map.put("pageSize", 100000);
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getLogisticsCompanyList startTime" + startTime);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getLogisticsCompanyList(map)
                .compose(HttpUtils.applyIOSchedulers())
                .map(logisticsCompanyBean -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getLogisticsCompanyList requestTimeTime" + (requestTimeTime - startTime));
                    if (logisticsCompanyBean.list != null) {
                        //快递公司
                        initLogisticsCompanyData(logisticsCompanyBean.list);
                    }
                    final long insertTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getLogisticsCompanyList insertTimeTime" + (insertTimeTime - requestTimeTime));
                    return logisticsCompanyBean;
                });
    }

    /**
     * 全部商品列表和包装信息
     */
    private Observable<ProductBean> getProductList() {
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductList startTime" + startTime);
        String org_id = MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getProductAndPackageInfoList(org_id, 1, 1, 100000)
                .compose(HttpUtils.applyIOSchedulers())
                .map(productBean -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getProductList requestTimeTime" + (requestTimeTime - startTime));
                    return productBean;
                });
    }

    /**
     * 全部商品包装(?仓储)信息
     */
    private Observable<ProductWareHouseBean> getProductPackageList() {
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductPackageList startTime" + startTime);
        String org_id = MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getProductPackageInfoList(org_id, 1, 100000)
                .compose(HttpUtils.applyIOSchedulers())
                .map(productBean -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getProductPackageList requestTimeTime" + (requestTimeTime - startTime));
                    return productBean;
                });
    }

    /**
     * 企业内全部商品批次
     */
    private Observable<AllBatchInfoBean> getProductBatchList() {
        final long startTime = System.currentTimeMillis();
        LogUtils.xswShowLog("getProductBatchList startTime" + startTime);
        return HttpUtils.getGatewayApi(ApiService.class)
                .getAllProductBatchList(1, 100000)
                .compose(HttpUtils.applyIOSchedulers())
                .map(allBatchInfoBean -> {
                    final long requestTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getProductBatchList requestTimeTime" + (requestTimeTime - startTime));
                    if (allBatchInfoBean.list != null) {
                        //商品批次信息
                        initProductBatchData(allBatchInfoBean.list);
                    }
                    final long insertTimeTime = System.currentTimeMillis();
                    LogUtils.xswShowLog("getProductBatchList insertTimeTime" + (insertTimeTime - requestTimeTime));
                    return allBatchInfoBean;
                });
    }

    public LiveData<Resource<String>> clearData() {
        MutableLiveData<Resource<String>> liveData = new ValueKeeperLiveData<>();
        Observable.just(1)
                .map(integer -> {
                    ObjectBoxUtils.deleteAll();
                    String user_mobile = MMKVUtils.getString(ConstantUtil.USER_MOBILE);
                    String organization_id = MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID);
                    UserEntity userEntity = new UserEntity(0, user_mobile, organization_id);
                    UserOperator userOperator = new UserOperator();
                    MMKVUtils.save(ConstantUtil.USER_ENTITY_ID, userOperator.getUserIdByInfo(userEntity));
                    return "清除成功";
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, s, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        liveData.postValue(new Resource<>(Resource.ERROR, null, "清除失败"));
                    }
                });
        return liveData;
    }

    public LiveData<Resource<String>> saveLocalDataToFile(String code) {
        MutableLiveData<Resource<String>> liveData = new MutableLiveData<>();
        //noinspection ConstantConditions
        Observable.just(CustomApplication.getCustomApplicationContext().getFilesDir())
                .flatMap((Function<File, ObservableSource<File>>) file -> Observable.fromArray(file.listFiles()))
                .filter(file -> file.isDirectory() && TextUtils.equals(file.getName(), "objectbox"))
                .flatMap((Function<File, ObservableSource<File>>) file -> Observable.fromArray(file.listFiles()))
                .filter(file -> file.isDirectory() && TextUtils.equals(file.getName(), "objectbox"))
                .flatMap((Function<File, ObservableSource<File>>) file -> Observable.fromArray(file.listFiles()))
                .filter(file -> TextUtils.equals("data.mdb", file.getName()))
                .flatMap((Function<File, ObservableSource<File>>) inFile -> {
                    RequestBody requestBody = RequestBody.create(inFile, MediaType.parse("multipart/form-data"));
                    MultipartBody.Builder builder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM);//表单类型
                    builder.addFormDataPart("file", inFile.getName(), requestBody);// "file"后台接收图片流的参数名
                    List<MultipartBody.Part> parts = builder.build().parts();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("phone",LocalUserUtils.getCurrentUserEntity().getPhone());
                    map.put("versionType", AppConfig.CURRENT_VERSION);
                    map.put("versionCode", BuildConfigUtils.getVersionCode());
                    return HttpUtils.getMyApi(ApiMyService.class)
                            .uploadFiles(parts,code, JsonUtils.toJsonString(map))
                            .compose(HttpUtils.applyIOSchedulers())
                            .map(s -> inFile);
                })
                .map(inFile -> {
                    File outFile = new File(FileUtils.getFileDirPath() + "data");
                    DataInputStream dis = null;
                    DataOutputStream dos = null;
                    try {
                        dis = new DataInputStream(new FileInputStream(inFile));
                        dos = new DataOutputStream(new FileOutputStream(outFile));
                        byte[] data = new byte[1024 * 4];
                        int len = -1;
                        while ((len = dis.read(data)) != -1) {
                            dos.write(data, 0, len);
                        }

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (dos != null) {
                            try {
                                dos.close();
                            } catch (IOException e) {
                                System.out.println("非法操作");
                            }
                        }
                        if (dis != null) {
                            try {
                                dis.close();
                            } catch (IOException e) {
                                System.out.println("非法操作");
                            }
                        }
                    }
                    return "输出完成";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CustomObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        liveData.postValue(new Resource<>(Resource.LOADING, null, ""));
                    }

                    @Override
                    public void onNext(String s) {
                        liveData.postValue(new Resource<>(Resource.SUCCESS, null, ""));
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
