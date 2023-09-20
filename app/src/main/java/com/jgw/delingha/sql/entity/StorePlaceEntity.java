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
public class StorePlaceEntity extends BaseEntity implements SelectItemSupport {
    @Id
    private long id;

    @Unique
    private String storeHouseId; //库位ID

    private String storeHouseName; //库位名称

    private String storeHouseCode; //库位编码

    private String wareHouseId;//所属仓库ID

    private int disableFlag;//是否启用 1启用 0禁用

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(storeHouseId)) {
            return super.hashCode();
        } else {
            return storeHouseId.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof StorePlaceEntity) {
            return TextUtils.equals(((StorePlaceEntity) obj).storeHouseId, storeHouseId);
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

    public String getStoreHouseId() {
        return this.storeHouseId;
    }

    public void setStoreHouseId(String storeHouseId) {
        this.storeHouseId = storeHouseId;
    }

    public String getStoreHouseName() {
        return this.storeHouseName;
    }

    public void setStoreHouseName(String storeHouseName) {
        this.storeHouseName = storeHouseName;
    }

    public String getStoreHouseCode() {
        return this.storeHouseCode;
    }

    public void setStoreHouseCode(String storeHouseCode) {
        this.storeHouseCode = storeHouseCode;
    }

    public String getWareHouseId() {
        return this.wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public int getDisableFlag() {
        return this.disableFlag;
    }

    public void setDisableFlag(int disableFlag) {
        this.disableFlag = disableFlag;
    }

    public String getStoreHouse() {
        if (TextUtils.isEmpty(storeHouseCode) || TextUtils.isEmpty(storeHouseName)) {
            return "无";
        }
        return "(" + storeHouseCode + ")" + storeHouseName;
    }

    @Override
    public String getShowName() {
        return getStoreHouse();
    }

    @Override
    public long getItemId() {
        return getId();
    }
}
