package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;

/**
 * author : xsw
 * data : 2020/3/3
 * description : 商品信息
 */
@Entity
public class ProductInfoEntity extends BaseEntity implements SelectItemSupport {
    @Id
    private long id;

    @Unique
    private String productId;

    private String productName;

    private String productCode;

    private String productSortId;

    private String productSortName;

    private boolean haveWarehouse;//是否存在仓储信息(物流模块商品列表)

    private boolean havePackaged;//是否存在包装信息(包装关联)

    public String getProductContentText() {
        return "(" + productCode + ")" + productName;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(productId)) {
            return super.hashCode();
        } else {
            return productId.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ProductInfoEntity) {
            return TextUtils.equals(((ProductInfoEntity) obj).productId, productId);
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

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return this.productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public boolean getHavePackaged() {
        return this.havePackaged;
    }

    public void setHavePackaged(boolean havePackaged) {
        this.havePackaged = havePackaged;
    }

    public boolean getHaveWarehouse() {
        return this.haveWarehouse;
    }

    public void setHaveWarehouse(boolean haveWarehouse) {
        this.haveWarehouse = haveWarehouse;
    }

    public String getProductSortId() {
        return this.productSortId;
    }

    public void setProductSortId(String productSortId) {
        this.productSortId = productSortId;
    }

    public String getProductSortName() {
        return this.productSortName;
    }

    public void setProductSortName(String productSortName) {
        this.productSortName = productSortName;
    }

    @Override
    public String getShowName() {
        return getProductContentText();
    }

    @Override
    public long getItemId() {
        return getId();
    }
}
