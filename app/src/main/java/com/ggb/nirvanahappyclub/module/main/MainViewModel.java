package com.ggb.nirvanahappyclub.module.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.livedata.ValueKeeperLiveData;

import org.jetbrains.annotations.NotNull;


public class MainViewModel extends BaseViewModel {
    private final MainModel model;
    private final MutableLiveData<String> mSystemExpireTimeLiveData = new ValueKeeperLiveData<>();

    public MainViewModel(@NonNull @NotNull Application application) {
        super(application);
        model = new MainModel();
    }

//    public void getSystemExpireTime() {
//        mSystemExpireTimeLiveData.setValue(null);
//    }
//
//    public LiveData<Resource<String>> getSystemExpireTimeLiveData() {
//        return Transformations.switchMap(mSystemExpireTimeLiveData, input -> model.getSystemExpireTime());
//    }
//
//    public void checkUserInfoError() {
//        UserOperator userOperator = new UserOperator();
//        if (userOperator.queryCount() != 0) {
//            return;
//        }
//        String user_mobile = MMKVUtils.getString(ConstantUtil.USER_MOBILE);
//        String organization_id = MMKVUtils.getString(ConstantUtil.ORGANIZATION_ID);
//        UserEntity userEntity = new UserEntity(0, user_mobile, organization_id);
//        MMKVUtils.save(ConstantUtil.USER_ENTITY_ID, userOperator.getUserIdByInfo(userEntity));
//    }
//
//    public void cleanEmptyBox() {
//        Observable.just("whatever")
//                .map(s -> {
//                    cleanPackagingAssociationEmptyBox();
//                    cleanPackagingAssociationMGPEmptyBox();
//                    cleanPackageStockInEmptyBox();
//                    cleanInWareHousePackageEmptyBox();
//                    return "清除成功";
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new CustomObserver<String>() {
//                    @Override
//                    public void onNext(@io.reactivex.annotations.NonNull String s) {
//                        LogUtils.xswShowLog(s);
//                    }
//                });
//
//    }
//
//    private void cleanPackagingAssociationEmptyBox() {
//        PackagingAssociationOperator operator = new PackagingAssociationOperator();
//        List<String> parentList = operator.queryParentCodeList();
//        List<String> realParentList = operator.queryRealParentCodeList();
//        parentList.removeAll(realParentList);
//        for (String aLong : parentList) {
//            operator.removeEntityByCode(aLong);
//        }
//    }
//
//    private void cleanPackagingAssociationMGPEmptyBox() {
//        PackagingAssociationCustomOperator operator = new PackagingAssociationCustomOperator();
//        List<String> parentList = operator.queryParentCodeList();
//        List<String> realParentList = operator.queryRealParentCodeList();
//        parentList.removeAll(realParentList);
//        for (String aLong : parentList) {
//            operator.removeEntityByCode(aLong);
//        }
//    }
//
//    private void cleanPackageStockInEmptyBox() {
//        PackageStockInOperator operator = new PackageStockInOperator();
//        List<String> parentList = operator.queryParentCodeList();
//        List<String> realParentList = operator.queryRealParentCodeList();
//        parentList.removeAll(realParentList);
//        for (String aLong : parentList) {
//            operator.removeEntityByCode(aLong);
//        }
//    }
//
//    private void cleanInWareHousePackageEmptyBox() {
//        InWarehousePackageOperator operator = new InWarehousePackageOperator();
//        List<String> parentList = operator.queryParentCodeList();
//        List<String> realParentList = operator.queryRealParentCodeList();
//        parentList.removeAll(realParentList);
//        for (String aLong : parentList) {
//            operator.removeEntityByCode(aLong);
//        }
//    }
//
//    public void cleanEmptyOrder() {
//        Observable.just("whatever")
//                .map(s -> {
//                    cleanOrderStockOutEmptyOrder();
//                    return "清除空订单成功";
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new CustomObserver<String>() {
//                    @Override
//                    public void onNext(@io.reactivex.annotations.NonNull String s) {
//                        LogUtils.xswShowLog(s);
//                    }
//                });
//    }
//
//    private void cleanOrderStockOutEmptyOrder() {
//        OrderStockOutOperator operator = new OrderStockOutOperator();
//        operator.clearEmptyOrder();
//    }
}
