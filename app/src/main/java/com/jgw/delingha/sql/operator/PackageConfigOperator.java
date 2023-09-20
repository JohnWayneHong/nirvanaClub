package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity;
import com.jgw.delingha.sql.entity.PackageConfigEntity_;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.query.QueryBuilder;
import io.objectbox.relation.RelationInfo;

public class PackageConfigOperator extends BaseOperator<PackageConfigEntity> {
    public final List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        QueryBuilder<PackageConfigEntity> qb = box
                .query()
                .equal(PackageConfigEntity_.userEntityId, LocalUserUtils.getCurrentUserId());
        qb.backlink(relationInfo);

        long[] longs = qb.build()
                .property(PackageConfigEntity_.id)
                .distinct()
                .findLongs();
        ArrayList<Long> list = new ArrayList<>();
        for (long l : longs) {
            list.add(l);
        }
        return list;
    }
}
