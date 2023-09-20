package com.jgw.delingha.module.stock_in.package_stock_in.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.jgw.common_library.base.viewmodel.BaseViewModel;
import com.jgw.common_library.http.Resource;
import com.jgw.common_library.livedata.ValueKeeperLiveData;
import com.jgw.delingha.bean.ProductWareHouseBean;
import com.jgw.delingha.common.model.PackageConfigInfoModel;
import com.jgw.delingha.module.stock_in.package_stock_in.model.PackageStockInModel;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.StorePlaceEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * author : Cxz
 * data : 2019/12/17
 * description : 包装关联设置ViewModel
 */
public class PackageStockInSettingViewModel extends BaseViewModel {

    private final PackageStockInModel model;
    private final List<String> levelList = new ArrayList<>();
    private List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> ratiosList = new ArrayList<>();

    private final PackageConfigEntity mEntity = new PackageConfigEntity();
    private final PackageConfigInfoModel mConfigModel;

    private final MutableLiveData<Long> mWaitLoadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mProductWareMessageLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mWareHouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mProductBatchLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mStorePlaceLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageConfigEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();

    public PackageStockInSettingViewModel(@NonNull Application application) {
        super(application);
        mConfigModel = new PackageConfigInfoModel();
        model = new PackageStockInModel();
    }

    /**
     * 触发获取待执行任务数
     */
    public void hasWaitUpload() {
        mWaitLoadLiveData.setValue(null);
    }

    /**
     * 获取待执行任务数
     */
    public LiveData<Resource<List<Long>>> getWaitUploadLiveData() {
        return Transformations.switchMap(mWaitLoadLiveData, input -> model.checkWaitUpload());
    }

    /**
     * @return 本地的configEntity
     */
    public PackageConfigEntity getConfigEntity() {
        return mEntity;
    }

    /**
     * 触发获取productInfo
     */
    public void getProductInfo(long productId) {
        mProductInfoLiveData.setValue(productId);
    }

    /**
     * 获取productInfo
     */
    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mProductInfoLiveData, mConfigModel::getProductInfo);
    }

    /**
     * 根据productInfoId设置相关属性
     */
    public void setProductInfo(ProductInfoEntity entity) {
        mEntity.setProductId(entity.getProductId());
        mEntity.setProductName(entity.getProductName());
        mEntity.setProductCode(entity.getProductCode());

        mEntity.setProductBatchName("");
        mEntity.setProductBatchId("");
    }

    /**
     * 触发获取productWareMessage
     */
    public void getProductWareMessage(String productId) {
        mProductWareMessageLiveData.setValue(productId);
    }

    /**
     * 获取productWareMessage
     */
    public LiveData<Resource<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>> getProductWareInfoLiveData() {
        return Transformations.switchMap(mProductWareMessageLiveData, mConfigModel::getProductPackageInfo);
    }

    /**
     * 根据productWareMessage设置ratiosList,levelList
     */
    public void setProductWareMessageList(List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> list) {
        ratiosList = list;
        levelList.clear();
        for (ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean : ratiosList) {
            levelList.add(bean.getLevel());
        }
    }

    /**
     * return 关联级别
     */
    public List<String> getProductAssociationLevelList() {
        return levelList;
    }

    /**
     * 选择的关联级别
     */
    public ProductWareHouseBean.ListBean.ProductPackageRatiosBean getProductPackageSelected(int position) {
        return ratiosList.get(position);
    }

    /**
     * 根据productWareMessage设置相关属性
     */
    public void setAssociationLevelSpecifications(ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean) {
        mEntity.setPackageSpecification(String.valueOf(bean.lastNumber)); //包装规格
        mEntity.setNumber(bean.lastNumber + ""); //包装数量重置为包装规格 重置零箱包装状态
        mEntity.setLastNumberName(bean.lastNumberName);
        mEntity.setLastNumberCode(bean.lastNumberCode);
        mEntity.setFirstNumber(bean.firstNumber);
        mEntity.setFirstNumberName(bean.firstNumberName);
        mEntity.setFirstNumberCode(bean.firstNumberCode);
    }

    /**
     * 触发获取ProductBatch
     */
    public void getProductBatch(long productBatchId) {
        mProductBatchLiveData.setValue(productBatchId);
    }

    /**
     * 获取ProductBatch
     */
    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfoLiveData() {
        return Transformations.switchMap(mProductBatchLiveData, mConfigModel::getProductBatchInfo);
    }

    /**
     * 根据productBatch设置相关属性
     */
    public void setProductBatch(ProductBatchEntity entity) {
        mEntity.setProductBatchName(entity.getBatchName());
        mEntity.setProductBatchCode(entity.getBatchCode());
        mEntity.setProductBatchId(entity.getBatchId());
    }

    /**
     * 触发获取WareHouse
     */
    public void getWareHouse(long wareHouseId) {
        mWareHouseLiveData.setValue(wareHouseId);
    }

    /**
     * 获取WareHouse
     */
    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoLiveData() {
        return Transformations.switchMap(mWareHouseLiveData, mConfigModel::getWareHouseInfo);
    }

    /**
     * 通过获取的WareHouse数据进行设置
     */
    public void setWareHouse(WareHouseEntity entity) {
        mEntity.setWareHouseName(entity.getWareHouseName());
        mEntity.setWareHouseCode(entity.getWareHouseCode());
        mEntity.setWareHouseId(entity.getWareHouseId());
        mEntity.setStoreHouseId("");
        mEntity.setStoreHouseName("");
    }

    /**
     * 触发获取storePlace
     */
    public void getStorePlace(long storePlaceId) {
        mStorePlaceLiveData.setValue(storePlaceId);
    }

    /**
     * 获取storePlace
     */
    public LiveData<Resource<StorePlaceEntity>> getStorePlaceInfoLiveData() {
        return Transformations.switchMap(mStorePlaceLiveData, mConfigModel::getStorePlaceInfo);
    }

    /**
     * 通过获取的storePlace数据进行设置
     */
    public void setStorePlace(StorePlaceEntity entity) {
        mEntity.setStoreHouseId(entity.getStoreHouseId());
        mEntity.setStoreHouseName(entity.getStoreHouseName());
    }

    /**
     * 触发保存configEntity
     */
    public void getSaveConfig(PackageConfigEntity entity) {
        mSaveConfigLiveData.setValue(entity);
    }

    /**
     * 保存configEntity
     */
    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, mConfigModel::saveConfig);
    }

    /**
     * @return 产品Id
     */
    public String getProductId() {
        return mEntity.getProductId();
    }

    /**
     * return 产品名称为空
     */
    public boolean noSelectProduct() {
        return TextUtils.isEmpty(mEntity.getProductName());
    }

    /**
     * return 包装级别为空
     */
    public boolean noAssociationLevel() {
        return levelList.isEmpty();
    }

    /**
     * return 仓库为空
     */
    public boolean noWareHouse() {
        return TextUtils.isEmpty(mEntity.getWareHouseName());
    }

    /**
     * return 产品规格
     */
    public String getProductPackageNumber() {
        return mEntity.getPackageSpecification();
    }

    /**
     * @return 仓库Id
     */
    public String getWareHouseId() {
        return mEntity.getWareHouseId();
    }

}
