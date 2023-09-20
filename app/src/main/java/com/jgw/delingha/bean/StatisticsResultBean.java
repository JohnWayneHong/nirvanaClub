package com.jgw.delingha.bean;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.jgw.common_library.utils.ResourcesUtils;
import com.jgw.delingha.R;

public abstract class StatisticsResultBean {

    private String dimensionInfo;

    public String getProductText() {
        if (!TextUtils.isEmpty(getProductCode()) && !TextUtils.isEmpty(getProductName())) {
            return "(" + getProductCode() + ")" + getProductName();
        } else if (!TextUtils.isEmpty(getProductName())) {
            return getProductName();
        } else {
            return null;
        }
    }

    public String getWarehouseText() {
        if (!TextUtils.isEmpty(getWareHouseCode()) && !TextUtils.isEmpty(getWareHouseName())) {
            return "(" + getWareHouseCode() + ")" + getWareHouseName();
        } else if (!TextUtils.isEmpty(getWareHouseName())) {
            return getWareHouseName();
        } else {
            return null;
        }
    }

    public String getWarehouseInText() {
        if (!TextUtils.isEmpty(getWareHouseCodeIn()) && !TextUtils.isEmpty(getWareHouseNameIn())) {
            return "(" + getWareHouseCodeIn() + ")" + getWareHouseNameIn();
        } else if (!TextUtils.isEmpty(getWareHouseNameIn())) {
            return getWareHouseNameIn();
        } else {
            return null;
        }
    }

    public String getWarehouseOutText() {
        if (!TextUtils.isEmpty(getWareHouseCodeOut()) && !TextUtils.isEmpty(getWareHouseNameOut())) {
            return "(" + getWareHouseCodeOut() + ")" + getWareHouseNameOut();
        } else if (!TextUtils.isEmpty(getWareHouseNameOut())) {
            return getWareHouseNameOut();
        } else {
            return null;
        }
    }

    public int getProductVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.product_name)) ? View.VISIBLE : View.GONE;
    }

    public int getBatchVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.batch_name)) ? View.VISIBLE : View.GONE;
    }

    public int getWareHouseVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.warehouse_name)) ? View.VISIBLE : View.GONE;
    }

    public int getWareHouseInVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.warehouse_in_name)) ? View.VISIBLE : View.GONE;
    }

    public int getWareHouseOutVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.warehouse_out_name)) ? View.VISIBLE : View.GONE;
    }

    public int getCustomerVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.customer_name)) ? View.VISIBLE : View.GONE;
    }

    public int getCustomerInVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.customer_in_name)) ? View.VISIBLE : View.GONE;
    }

    public int getCustomerOutVisible() {
        return dimensionInfo.contains(ResourcesUtils.getString(R.string.customer_out_name)) ? View.VISIBLE : View.GONE;
    }

    public abstract String getSingleCodeNumber();

    public abstract String getProductName();

    public abstract String getProductCode();

    public abstract String getBatchName();

    public abstract String getWareHouseName();

    public abstract String getWareHouseCode();

    public abstract String getWareHouseNameIn();

    public abstract String getWareHouseCodeIn();

    public abstract String getWareHouseNameOut();

    public abstract String getWareHouseCodeOut();

    public abstract String getCustomerName();

    public abstract String getCustomerNameIn();

    public abstract String getCustomerNameOut();


    public void setStatisticsDimensionInfo(@NonNull String dimensionInfo) {
        this.dimensionInfo = dimensionInfo;
    }

    ;
}
