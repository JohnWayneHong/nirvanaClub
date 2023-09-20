package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.BaseCodeEntity;

import java.util.List;

import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public abstract class BaseNoConfigCodeOperator<T extends BaseCodeEntity> extends BaseCodeOperator<T> {
    public abstract void deleteAllByCurrentUser();

    public void deleteAllByCurrentUser(Property<T> userProperty) {
        Query<T> query = box.query()
                .equal(userProperty, LocalUserUtils.getCurrentUserId())
                .build();
        removeDataByQuery(query);
    }

    /**
     * 根据数量来查询码
     * (实现反扫删1补1)
     */
    public abstract List<T> queryListByCount(int count);

    public final List<T> queryListByCount(Property<T> userEntityId, Property<T> orderBy, int count) {
        return queryListByPage(userEntityId, orderBy, 0, count);
    }

    public final List<T> queryListByPage(Property<T> userEntityId, Property<T> orderBy, int page, int pageSize) {
        Query<T> query = box.query()
                .equal(userEntityId, LocalUserUtils.getCurrentUserId())
                .orderDesc(orderBy)
                .build();
        return queryListByPage(query, page, pageSize);
    }

    public final List<T> queryListByStatus(Property<T> userProperty, Property<T> statusProperty, int status) {
        QueryBuilder<T> qb = box.query();
        Query<T> query = qb
                .equal(userProperty, LocalUserUtils.getCurrentUserId())
                .equal(statusProperty, status)
                .build();
        return queryListByQuery(query);
    }

    public final long queryCountByStatus(Property<T> userProperty, Property<T> statusProperty, int status) {
        QueryBuilder<T> qb = box.query();
        Query<T> query = qb
                .equal(userProperty, LocalUserUtils.getCurrentUserId())
                .equal(statusProperty, status)
                .build();
        return queryCountByQB(query);
    }

    public final List<T> queryGroupDataByUserId(Property<T> userProperty, Property<T> idProperty, long currentId, int limit) {
        return queryGroupDataByUserId(userProperty, idProperty, currentId, 0, limit);
    }

    public final List<T> queryGroupDataByUserId(Property<T> userProperty, Property<T> idProperty, long currentId, int page, int pageSize) {
        Query<T> query = box.query()
                .equal(userProperty, LocalUserUtils.getCurrentUserId())
                .greaterOrEqual(idProperty, currentId)
                .orderDesc(idProperty)
                .build();
        return queryListByPage(query, page, pageSize);
    }

    public final long queryCountByCurrentUser(Property<T> userProperty) {
        QueryBuilder<T> qb = box.query();
        Query<T> query = qb.equal(userProperty, LocalUserUtils.getCurrentUserId()).build();
        return queryCountByQB(query);
    }
}
