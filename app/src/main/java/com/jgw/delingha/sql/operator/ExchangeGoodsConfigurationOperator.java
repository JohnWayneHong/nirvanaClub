package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsConfigurationEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsConfigurationEntity_;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.RelationInfo;

public class ExchangeGoodsConfigurationOperator extends BaseOperator<ExchangeGoodsConfigurationEntity> {
    public final List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        QueryBuilder<ExchangeGoodsConfigurationEntity> qb = box
                .query()
                .equal(ExchangeGoodsConfigurationEntity_.userEntityId, LocalUserUtils.getCurrentUserId());
        qb.backlink(relationInfo);

        long[] longs = qb.build()
                .property(ExchangeGoodsConfigurationEntity_.id)
                .distinct()
                .findLongs();
        ArrayList<Long> list = new ArrayList<>();
        for (long l : longs) {
            list.add(l);
        }
        return list;
    }
}
