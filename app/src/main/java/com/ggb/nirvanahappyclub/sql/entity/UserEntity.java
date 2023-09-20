package com.ggb.nirvanahappyclub.sql.entity;

import androidx.annotation.NonNull;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * author : Cxz
 * data : 2020/1/17
 * description : 数据库用户表
 */
@Entity
public class UserEntity extends BaseEntity {
    @Id//注意：Long型id，如果传入null，则GreenDao会默认设置自增长的值。
    private long id;

    private String phone;

    private String companyId;

    public UserEntity() {
    }

    public UserEntity(long id, String phone, String companyId) {
        this.id = id;
        this.phone = phone;
        this.companyId = companyId;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n Id ; " + id + "\n UserNumber : " + phone + "\n companyName :" + companyId;
    }

    public long getId() {
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getPhone() {
        return this.phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getCompanyId() {
        return this.companyId;
    }


    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
