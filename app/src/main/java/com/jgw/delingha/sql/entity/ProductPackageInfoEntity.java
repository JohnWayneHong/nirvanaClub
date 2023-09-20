package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;


/**
 * author : xsw
 * data : 2020/3/3
 * description : 商品包装信息
 */
@Entity
public class ProductPackageInfoEntity extends BaseEntity {
    @Id
    private long id;

    private String productWareHouseId;

    private String productId;

    //json字符串 ProductPackageInfoBean集合
    private String productPackageRatios;

    private String productPackageName;

    private int packageRestricted;//0不限制 1限制

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(productWareHouseId)) {
            return super.hashCode();
        } else {
            return productWareHouseId.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj != null) {
            return TextUtils.equals(((ProductPackageInfoEntity) obj).productWareHouseId, productWareHouseId);
        } else {
            return false;
        }

    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductWareHouseId() {
        return this.productWareHouseId;
    }

    public void setProductWareHouseId(String productWareHouseId) {
        this.productWareHouseId = productWareHouseId;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductPackageRatios() {
        return this.productPackageRatios;
    }

    public void setProductPackageRatios(String productPackageRatios) {
        this.productPackageRatios = productPackageRatios;
    }

    public String getProductPackageName() {
        return this.productPackageName;
    }

    public void setProductPackageName(String productPackageName) {
        this.productPackageName = productPackageName;
    }

    public int getPackageRestricted() {
        return this.packageRestricted;
    }

    public void setPackageRestricted(int packageRestricted) {
        this.packageRestricted = packageRestricted;
    }
}
