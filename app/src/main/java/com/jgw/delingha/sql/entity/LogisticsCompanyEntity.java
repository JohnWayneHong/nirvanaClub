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
 * description : 快递公司信息表
 */
@Entity
public class LogisticsCompanyEntity extends BaseEntity implements SelectItemSupport {
    @Id
    private long id;

    @Unique
    private String shipperCode;

    private String shipperName;

    public LogisticsCompanyEntity() {
    }

    public LogisticsCompanyEntity(long id, String shipperCode, String shipperName) {
        this.id = id;
        this.shipperCode = shipperCode;
        this.shipperName = shipperName;
    }

    public String getLogisticsCompanyText() {
        if (!TextUtils.isEmpty(shipperCode) && !TextUtils.isEmpty(shipperName)) {
            return "(" + shipperCode + ")" + shipperName;
        } else if (!TextUtils.isEmpty(shipperName)) {
            return shipperName;
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(shipperCode)) {
            return super.hashCode();
        } else {
            return shipperCode.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof LogisticsCompanyEntity) {
            return TextUtils.equals(((LogisticsCompanyEntity) obj).shipperCode, shipperCode);
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

    public String getShipperCode() {
        return this.shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public String getShipperName() {
        return this.shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    @Override
    public String getShowName() {
        return getLogisticsCompanyText();
    }

    @Override
    public long getItemId() {
        return getId();
    }
}
