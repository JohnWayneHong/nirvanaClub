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
 * description : 全部商品批次信息
 */
@Entity
public class ProductBatchEntity extends BaseEntity implements SelectItemSupport {
    @Id
    private long id;

    @Unique
    private String batchId;

    private String batchName;

    private String batchCode;

    private String productId;

    private String productName;

    public String getBatchContentText() {
        return "(" + batchCode + ")" + batchName;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(batchId)) {
            return super.hashCode();
        } else {
            return batchId.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ProductBatchEntity) {
            return TextUtils.equals(((ProductBatchEntity) obj).batchId, batchId);
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

    public String getBatchId() {
        return this.batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getBatchCode() {
        return this.batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
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

    @Override
    public String getShowName() {
        return getBatchContentText();
    }

    @Override
    public long getItemId() {
        return getId();
    }
}
