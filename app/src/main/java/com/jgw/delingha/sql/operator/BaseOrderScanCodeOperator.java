package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.BaseOrderScanCodeEntity;

import java.util.List;

import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * Created by xswwg
 * on 2021/3/15
 */
public abstract class BaseOrderScanCodeOperator<T extends BaseOrderScanCodeEntity> extends BaseOperator<T> {
    /**
     * 根据code删除某一条数据
     */
    public abstract void removeEntityByCode(String code);


    public abstract void deleteByProductId(Long productId);

    public void deleteByProductId(Property<T> productIdProperty, Long productId) {
        box.query().equal(productIdProperty, productId).build().remove();
    }

    public abstract void deleteVerifyingCodesByProductId(Long productId);

    public void deleteVerifyingCodesByProductId(Property<T> productIdProperty, Long productId, Property<T> codeStatusProperty) {
        Query<T> query = box.query().equal(productIdProperty, productId)
                .notEqual(codeStatusProperty, BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS)
                .build();
        deleteDataByQB(query);
    }

    public List<T> queryVerifyingCodesByProductId(Property<T> productIdProperty, Long productId, Property<T> codeStatusProperty) {
        Query<T> query = box.query().equal(productIdProperty, productId)
                .notEqual(codeStatusProperty, BaseOrderScanCodeEntity.STATUS_CODE_SUCCESS)
                .build();
       return queryListByQuery(query);
    }

    public boolean isRepeatCode(String code) {
        return queryEntityByCode(code) != null;
    }

    public abstract T queryEntityByCode(String code);


    public abstract List<T> queryDataByPage(long productId, int page, int pageSize);

    public List<T> queryDataByPageOrderDesc(Property<T> productIdProperty, long productId, int page, int pageSize,
                                            Property<T> orderBy) {
        Query<T> query = box.query().equal(productIdProperty, productId)
                .orderDesc(orderBy)
                .build();
        return queryListByPage(query, page, pageSize);
    }

    public abstract long queryCountByProductId(long productId);

    public final long queryCountByProductId(Property<T> productIdProperty, long productId) {
        QueryBuilder<T> qb = box.query();
        return qb.equal(productIdProperty, productId).build().count();
    }
}
