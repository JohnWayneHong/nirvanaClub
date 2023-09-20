package com.jgw.delingha.module.packaging.in_warehouse.viewmodel;

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
public class InWarehousePackageSettingViewModel extends BaseViewModel {

    private final PackageConfigInfoModel model;

    private List<String> mLevelList = new ArrayList<>();
    private List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> mRatiosList = new ArrayList<>();

    private final MutableLiveData<Long> mCheckWaitUploadLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetProductInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<String> mGetProductPackageInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mGetProductBatchInfoLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mWareHouseLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<Long> mStorePlaceLiveData = new ValueKeeperLiveData<>();
    private final MutableLiveData<PackageConfigEntity> mSaveConfigLiveData = new ValueKeeperLiveData<>();

    private PackageConfigEntity mEntity = new PackageConfigEntity();

    public InWarehousePackageSettingViewModel(@NonNull Application application) {
        super(application);
        model = new PackageConfigInfoModel();
    }

    public void checkWaitUpload() {
        mCheckWaitUploadLiveData.setValue(null);
    }

    public LiveData<Resource<List<Long>>> getCheckWaitUploadLiveData() {
        return Transformations.switchMap(mCheckWaitUploadLiveData, input -> model.checkInWarehousePackageWaitUpload());
    }

    public void getProductInfo(long productId) {
        mGetProductInfoLiveData.setValue(productId);
    }

    public LiveData<Resource<ProductInfoEntity>> getProductInfoLiveData() {
        return Transformations.switchMap(mGetProductInfoLiveData, model::getProductInfo);
    }

    public void setProductInfo(ProductInfoEntity entity) {
        mEntity.setProductId(entity.getProductId());
        mEntity.setProductName(entity.getProductName());
        mEntity.setProductCode(entity.getProductCode());
        mEntity.setProductBatchId(null);
        mEntity.setProductBatchName(null);
    }

    /**
     * 根据商品id获取商品包装信息
     *
     * @param productId 商品id
     */
    public void getProductPackageInfo(String productId) {
        mGetProductPackageInfoLiveData.setValue(productId);
    }

    public LiveData<Resource<List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean>>> getProductPackageInfoLiveData() {
        return Transformations.switchMap(mGetProductPackageInfoLiveData, model::getProductPackageInfo);
    }

    /**
     * 保存商品包装规格列表 创建规格选择列表
     *
     * @param list
     */
    public void setProductPackageInfoList(List<ProductWareHouseBean.ListBean.ProductPackageRatiosBean> list) {
        mRatiosList = list;
        mLevelList.clear();
        for (ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean : mRatiosList) {
            mLevelList.add(bean.getLevel());
        }
    }

    public ProductWareHouseBean.ListBean.ProductPackageRatiosBean getProductPackageSelected(int position) {
        return mRatiosList.get(position);
    }

    /**
     * 规格选择列表中选择包装规格
     *
     * @param bean 规格信息
     */
    public void setProductPackageInfo(ProductWareHouseBean.ListBean.ProductPackageRatiosBean bean) {
        mEntity.setPackageSpecification(String.valueOf(bean.lastNumber));//包装规格
        mEntity.setNumber(bean.lastNumber + "");//包装数量重置为包装规格 重置零箱包装状态
        mEntity.setLastNumberName(bean.lastNumberName);
        mEntity.setLastNumberCode(bean.lastNumberCode);
        mEntity.setFirstNumber(bean.firstNumber);
        mEntity.setFirstNumberName(bean.firstNumberName);
        mEntity.setFirstNumberCode(bean.firstNumberCode);
    }

    public void getProductBatchInfo(long batchId) {
        mGetProductBatchInfoLiveData.setValue(batchId);
    }

    public LiveData<Resource<ProductBatchEntity>> getProductBatchInfoLiveData() {
        return Transformations.switchMap(mGetProductBatchInfoLiveData, model::getProductBatchInfo);
    }

    public void setProductBatchInfo(ProductBatchEntity entity) {
        mEntity.setProductBatchName(entity.getBatchName());
        mEntity.setProductBatchId(entity.getBatchId());
    }

    public void saveConfig(PackageConfigEntity entity) {
        mSaveConfigLiveData.setValue(entity);
    }

    public LiveData<Resource<Long>> getSaveConfigLiveData() {
        return Transformations.switchMap(mSaveConfigLiveData, model::saveConfig);
    }

    public PackageConfigEntity getConfigEntity() {
        return mEntity;
    }

    /**
     * 判断是否选择过商品
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasSelectProduct() {
        return !TextUtils.isEmpty(mEntity.getProductId());
    }

    /**
     * 商品的关联级别是否为空
     */
    public boolean hasProductAssociationLevel() {
        return !mLevelList.isEmpty();
    }

    public List<String> getProductAssociationLevelList() {
        return mLevelList;
    }

    public String getProductId() {
        return mEntity.getProductId();
    }

    public int getProductPackageNumber() {
        return Integer.parseInt(mEntity.getPackageSpecification());
    }

    public String getWareHouseId() {
        return mEntity.getWareHouseId();
    }

    public void getWareHouse(long wareHouseId) {
        mWareHouseLiveData.setValue(wareHouseId);
    }

    public LiveData<Resource<WareHouseEntity>> getWareHouseInfoLiveData() {
        return Transformations.switchMap(mWareHouseLiveData, model::getWareHouseInfo);
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
        return Transformations.switchMap(mStorePlaceLiveData, model::getStorePlaceInfo);
    }

    /**
     * 通过获取的storePlace数据进行设置
     */
    public void setStorePlace(StorePlaceEntity entity) {
        mEntity.setStoreHouseId(entity.getStoreHouseId());
        mEntity.setStoreHouseName(entity.getStoreHouseName());
    }
}
