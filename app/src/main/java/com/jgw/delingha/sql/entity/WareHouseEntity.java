package com.jgw.delingha.sql.entity;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.jgw.delingha.module.select_list.common.SelectItemSupport;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;

/**
 * author : xsw
 * data : 2020/3/2
 * description : 仓库信息离线数据
 */
@Entity
public class WareHouseEntity extends BaseEntity implements SelectItemSupport {
    @Id
    private long id;

    @Unique
    private String wareHouseId; //仓库ID 唯一

    private String wareHouseCode;//仓库编码

    private String wareHouseName;//仓库名

    //获取仓库显示名称 编码+名称
    public String getWareHouseContentText() {
        if (TextUtils.isEmpty(wareHouseCode) || TextUtils.isEmpty(wareHouseName)) {
            return "无";
        }
        return "(" + wareHouseCode + ")" + wareHouseName;
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(wareHouseId)) {
            return super.hashCode();
        } else {
            return wareHouseId.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof WareHouseEntity) {
            return TextUtils.equals(((WareHouseEntity) obj).wareHouseId, wareHouseId);
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

    public String getWareHouseId() {
        return this.wareHouseId;
    }

    public void setWareHouseId(String wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getWareHouseCode() {
        return this.wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
    }

    public String getWareHouseName() {
        return this.wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    @Override
    public String getShowName() {
        return getWareHouseContentText();
    }

    @Override
    public long getItemId() {
        return getId();
    }
}
