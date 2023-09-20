package com.jgw.delingha.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.BR;
import com.jgw.delingha.R;
import com.jgw.delingha.sql.entity.CustomerEntity;
import com.jgw.delingha.sql.entity.ProductBatchEntity;
import com.jgw.delingha.sql.entity.ProductInfoEntity;
import com.jgw.delingha.sql.entity.WareHouseEntity;

import java.io.Serializable;
import java.util.List;

public class StatisticsParamsBean extends BaseObservable implements Serializable {
    private List<StatisticalDimension> list;
    private String productName;
    private String productCode;

    private String productId;
    private String batchId;
    private String batchName;

    //单条仓库信息 不论出入
    private String wareHouseId;
    private String wareHouseCode;
    private String wareHouseName;

    //同时存在出入仓库时 成对使用
    private String wareHouseCodeIn;
    private String wareHouseIdIn;
    private String wareHouseNameIn;


    private String wareHouseIdOut;
    private String wareHouseCodeOut;
    private String wareHouseNameOut;


    //单条客户信息 不论出入
    private String customerId;
    private String customerName;

    //同时存在出入客户时 成对使用
    private String customerIdIn;
    private String customerNameIn;
    private String customerIdOut;
    private String customerNameOut;

    //yyyy-MM-dd
    private String startTime;
    private String endTime;


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerIdIn() {
        return customerIdIn;
    }

    public void setCustomerIdIn(String customerIdIn) {
        this.customerIdIn = customerIdIn;
    }

    public String getCustomerIdOut() {
        return customerIdOut;
    }

    public void setCustomerIdOut(String customerIdOut) {
        this.customerIdOut = customerIdOut;
    }

    public String getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Bindable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        notifyPropertyChanged(BR.startTime);
    }

    @Bindable
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
        notifyPropertyChanged(BR.endTime);
    }

    @Bindable
    public String getProductText() {
        if (!TextUtils.isEmpty(productCode) && !TextUtils.isEmpty(productName)) {
            return "(" + productCode + ")" + productName;
        } else if (!TextUtils.isEmpty(productName)) {
            return productName;
        } else {
            return null;
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
        notifyPropertyChanged(BR.productText);
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
        notifyPropertyChanged(BR.productText);
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Bindable
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
        notifyPropertyChanged(BR.batchName);
    }

    public String getWareHouseCode() {
        return wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
    }

    @Bindable
    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
        notifyPropertyChanged(BR.wareHouseName);
    }

    public String getWareHouseIdIn() {
        return wareHouseIdIn;
    }

    public void setWareHouseIdIn(String wareHouseIdIn) {
        this.wareHouseIdIn = wareHouseIdIn;
    }

    public String getWareHouseIdOut() {
        return wareHouseIdOut;
    }

    public void setWareHouseIdOut(String wareHouseIdOut) {
        this.wareHouseIdOut = wareHouseIdOut;
    }

    public String getWareHouseCodeIn() {
        return wareHouseCodeIn;
    }

    public void setWareHouseCodeIn(String wareHouseCodeIn) {
        this.wareHouseCodeIn = wareHouseCodeIn;
    }

    @Bindable
    public String getWareHouseNameIn() {
        return wareHouseNameIn;
    }

    public void setWareHouseNameIn(String wareHouseNameIn) {
        this.wareHouseNameIn = wareHouseNameIn;
        notifyPropertyChanged(BR.wareHouseNameIn);
    }

    public String getWareHouseCodeOut() {
        return wareHouseCodeOut;
    }

    public void setWareHouseCodeOut(String wareHouseCodeOut) {
        this.wareHouseCodeOut = wareHouseCodeOut;
    }

    @Bindable
    public String getWareHouseNameOut() {
        return wareHouseNameOut;
    }

    public void setWareHouseNameOut(String wareHouseNameOut) {
        this.wareHouseNameOut = wareHouseNameOut;
        notifyPropertyChanged(BR.wareHouseNameOut);
    }

    @Bindable
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customer) {
        this.customerName = customer;
        notifyPropertyChanged(BR.customerName);
    }

    @Bindable
    public String getCustomerNameIn() {
        return customerNameIn;
    }

    public void setCustomerNameIn(String customerIn) {
        this.customerNameIn = customerIn;
        notifyPropertyChanged(BR.customerNameIn);
    }

    @Bindable
    public String getCustomerNameOut() {
        return customerNameOut;
    }

    public void setCustomerNameOut(String customerNameOut) {
        this.customerNameOut = customerNameOut;
        notifyPropertyChanged(BR.customerNameOut);
    }

    @Bindable
    public String getDimensionInfo() {
        if (list == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i).dimensionName;
            sb.append(s);
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public void setList(List<StatisticalDimension> list) {
        this.list = list;
        notifyPropertyChanged(BR.dimensionInfo);
    }

    public List<StatisticalDimension> getList() {
        return list;
    }

    public void setProductInfo(@NonNull ProductInfoEntity data) {
        setProductId(data.getProductId());
        setProductCode(data.getProductCode());
        setProductName(data.getProductName());
    }

    public void setBatchInfo(@NonNull ProductBatchEntity data) {
        setBatchId(data.getBatchId());
        setBatchName(data.getBatchName());
    }

    public void setWarehouseInfo(@NonNull WareHouseEntity data) {
        setWareHouseId(data.getWareHouseId());
        setWareHouseCode(data.getWareHouseCode());
        setWareHouseName(data.getWareHouseName());
    }

    public void setWarehouseInInfo(@NonNull WareHouseEntity data) {
        setWareHouseIdIn(data.getWareHouseId());
        setWareHouseCodeIn(data.getWareHouseCode());
        setWareHouseNameIn(data.getWareHouseName());
    }

    public void setWarehouseOutInfo(@NonNull WareHouseEntity data) {
        setWareHouseIdOut(data.getWareHouseId());
        setWareHouseCodeOut(data.getWareHouseCode());
        setWareHouseNameOut(data.getWareHouseName());
    }

    public void setCustomerInfo(@NonNull CustomerEntity data) {
        setCustomerId(data.getCustomerId());
        setCustomerName(data.getCustomerName());
    }

    public void setCustomerInfoIn(@NonNull CustomerEntity data) {
        setCustomerIdIn(data.getCustomerId());
        setCustomerNameIn(data.getCustomerName());
    }

    public void setCustomerInfoOut(@NonNull CustomerEntity data) {
        setCustomerIdOut(data.getCustomerId());
        setCustomerNameOut(data.getCustomerName());
    }

    public static class StatisticalDimension extends BaseObservable implements Serializable {
        public String dimensionName;
        public String dimensionId;
        public boolean select;//0未选中 1选中

        public void setSelect(boolean select) {
            this.select = select;
            notifyPropertyChanged(BR.selectBG);
        }

        @Bindable
        public int getSelectBG() {
            return select? ResourcesUtils.getColor(R.color.gray_dd) :ResourcesUtils.getColor(R.color.white);
        }
    }
    public String getTimeRange(){
        return startTime+"　～　"+endTime;
    }
}
