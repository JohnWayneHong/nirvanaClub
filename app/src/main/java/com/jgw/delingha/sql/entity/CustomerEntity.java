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
 * description : 客户信息
 */
@Entity
public class CustomerEntity extends BaseEntity implements SelectItemSupport {
    @Id
    private long id;

    @Unique
    private String customerId;

    private String customerName;
    
    private String customerCode;

    private String customerSuperior;

    private String customerSuperiorName;
    
    private String contacts;//联系人姓名

    public CustomerEntity() {
    }

    public CustomerEntity(long id, String customerId, String customerName, String customerCode, String customerSuperior, String customerSuperiorName, String contacts) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerCode = customerCode;
        this.customerSuperior = customerSuperior;
        this.customerSuperiorName = customerSuperiorName;
        this.contacts = contacts;
    }

    public String getCustomerText() {
        if (!TextUtils.isEmpty(customerCode) && !TextUtils.isEmpty(customerName)) {
            return "(" + customerCode + ")" + customerName;
        } else if (!TextUtils.isEmpty(customerName)) {
            return customerName;
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        if (TextUtils.isEmpty(customerId)) {
            return super.hashCode();
        } else {
            return customerId.hashCode();
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CustomerEntity) {
            return TextUtils.equals(((CustomerEntity) obj).customerId, customerId);
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

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode() {
        return this.customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerSuperior() {
        return this.customerSuperior;
    }

    public void setCustomerSuperior(String customerSuperior) {
        this.customerSuperior = customerSuperior;
    }

    public String getContacts() {
        return this.contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }


    public String getCustomerSuperiorName() {
        return this.customerSuperiorName;
    }


    public void setCustomerSuperiorName(String customerSuperiorName) {
        this.customerSuperiorName = customerSuperiorName;
    }

    @Override
    public String getShowName() {
        return getCustomerText();
    }

    @Override
    public long getItemId() {
        return getId();
    }
}
