package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.ConfigurationEntity;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.RelationInfo;

/**
 * author : Cxz
 * data : 2020/1/17
 * description : 配置表的操作Bean类
 */
public class ConfigurationOperator extends BaseOperator<ConfigurationEntity> {
    public final List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        QueryBuilder<ConfigurationEntity> qb = box
                .query()
                .equal(ConfigurationEntity_.userEntityId, LocalUserUtils.getCurrentUserId());
        qb.backlink(relationInfo);

        long[] longs = qb.build()
                .property(ConfigurationEntity_.id)
                .distinct()
                .findLongs();
        ArrayList<Long> list = new ArrayList<>();
        for (long l : longs) {
            list.add(l);
        }
        return list;
    }
}
