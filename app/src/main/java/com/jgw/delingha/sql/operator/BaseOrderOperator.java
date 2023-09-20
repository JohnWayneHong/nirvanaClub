package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.BaseEntity;

import java.util.List;

import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * Created by xswwg
 * on 2021/3/15
 */
public abstract class BaseOrderOperator<T extends BaseEntity> extends BaseOperator<T> {

    public abstract T queryEntityByCode(String code);

    public abstract void deleteAllCodeByOrderCode(String orderCode);

    public void deleteAllCodeByOrderEntity(T entity) {

    }

    public final long queryCountByCurrentUser(Property<T> userProperty) {
        QueryBuilder<T> qb = box.query();
        Query<T> query = qb.equal(userProperty, LocalUserUtils.getCurrentUserId()).build();
        return queryCountByQB(query);
    }

    public final List<T> queryListByCurrentUser(Property<T> userProperty) {
        QueryBuilder<T> qb = box.query();
        Query<T> query = qb.equal(userProperty, LocalUserUtils.getCurrentUserId()).build();
        return queryListByQuery(query);
    }

}
