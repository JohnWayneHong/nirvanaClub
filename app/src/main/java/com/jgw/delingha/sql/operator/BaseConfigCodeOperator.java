package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.BaseCodeEntity;

import java.util.List;

import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.objectbox.query.QueryCondition;
import io.objectbox.relation.RelationInfo;

/**
 * Created by XiongShaoWu
 * on 2020/3/16
 */
public abstract class BaseConfigCodeOperator<T extends BaseCodeEntity> extends BaseCodeOperator<T> {

    /**
     * 根据配置Id和page信息查询
     */
    public abstract List<T> queryPageDataByConfigId(long configId, int page, int pageSize);

    public final List<T> queryPageDataByConfigId(Property<T> configProperty, long configId, Property<T> orderBy, int page, int pageSize) {
        Query<T> query = box.query()
                .equal(configProperty, configId)
                .orderDesc(orderBy)
                .build();
        return queryListByPage(query, page, pageSize);
    }

    /**
     * 根据configId查询count个
     */
    public final List<T> queryPageDataByConfigId(long configId, int count) {
        return queryPageDataByConfigId(configId, 1, count);
    }

    /**
     * 根据配置Id删除
     */
    public abstract void deleteAllByConfigId(long configId);

    public final long deleteAllByConfigId(Property<T> property, long configId) {
        Query<T> query = box.query()
                .equal(property, configId)
                .build();
        return removeDataByQuery(query);
    }

    /**
     * 根据配置Id查询数量
     */
    public abstract long queryCountByConfigId(long configId);

    public final long queryCountByConfigId(Property<T> property, long configId) {
        Query<T> query = box.query()
                .equal(property, configId)
                .build();
        return queryCountByQB(query);
    }

    public final List<T> queryListByStatus(Property<T> configProperty, long configId, Property<T> statusProperty, int status) {
        Query<T> query = box.query()
                .equal(configProperty, configId)
                .equal(statusProperty, status)
                .build();
        return queryListByQuery(query);
    }

    public final long queryCountByStatus(Property<T> configProperty, long configId, Property<T> statusProperty, int status) {
        Query<T> query = box.query()
                .equal(configProperty, configId)
                .equal(statusProperty, status)
                .build();
        return queryCountByQB(query);
    }

    public final long querySingleNumberCountByConfigId(Property<T> configProperty, long configId, Property<T> singleNumberProperty) {
        Query<T> query = box.query()
                .equal(configProperty, configId)
                .build();
        return query
                .property(singleNumberProperty)
                .sum();
    }

    public final List<T> queryGroupDataByConfigId(Property<T> configProperty, long configId, Property<T> idProperty, long currentId, int limit) {
        return queryGroupDataByConfigId(configProperty, configId, idProperty, currentId, 0, limit);
    }

    public final List<T> queryGroupDataByConfigId(Property<T> configProperty, long configId, Property<T> idProperty, long currentId, int page, int pageSize) {
        Query<T> query = box.query()
                .equal(configProperty, configId)
                .greaterOrEqual(idProperty, currentId)
                .order(idProperty)
                .build();
        return queryListByPage(query, page, pageSize);
    }

    public final List<T> queryGroupDataByConfigIdDesc(Property<T> configProperty, long configId, Property<T> idProperty, long currentId, int limit) {
        return queryGroupDataByConfigIdDesc(configProperty, configId, idProperty, currentId, 0, limit);
    }

    public final List<T> queryGroupDataByConfigIdDesc(Property<T> configProperty, long configId, Property<T> idProperty, long currentId, int page, int pageSize) {
        Query<T> query = box.query()
                .equal(configProperty, configId)
                .greaterOrEqual(idProperty, currentId)
                .orderDesc(idProperty)
                .build();
        return queryListByPage(query, page, pageSize);
    }

    public final <K> long queryCountByCurrentUser(RelationInfo<?, K> relationInfo, QueryCondition<K> queryCondition) {
        QueryBuilder<T> qb = box.query();
        qb.link(relationInfo)
                .apply(queryCondition);
        Query<T> query = qb.build();
        return queryCountByQB(query);
    }
}
