package com.jgw.delingha.sql.entity;

import androidx.databinding.BaseObservable;

/**
 * 创建表基类
 * Created by XiongShaoWu
 * on 2022年6月27日16:45:38
 */
public abstract class BaseEntity extends BaseObservable {
    public abstract long getId();

    @Override
    public int hashCode() {
        return (int) getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;
        return getId() == that.getId();
    }
}
