package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.ExchangeWarehouseConfigurationEntity;
import com.jgw.delingha.sql.entity.ExchangeWarehouseConfigurationEntity_;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.RelationInfo;

public class ExchangeWarehouseConfigurationOperator extends BaseOperator<ExchangeWarehouseConfigurationEntity> {
    public final List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        QueryBuilder<ExchangeWarehouseConfigurationEntity> qb = box
                .query()
                .equal(ExchangeWarehouseConfigurationEntity_.userEntityId, LocalUserUtils.getCurrentUserId());
        qb.backlink(relationInfo);

        long[] longs = qb.build()
                .property(ExchangeWarehouseConfigurationEntity_.id)
                .distinct()
                .findLongs();
        ArrayList<Long> list = new ArrayList<>();
        for (long l : longs) {
            list.add(l);
        }
        return list;
    }
}
