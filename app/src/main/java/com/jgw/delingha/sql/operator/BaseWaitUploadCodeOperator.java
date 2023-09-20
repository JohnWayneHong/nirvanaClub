package com.jgw.delingha.sql.operator;

import com.jgw.delingha.sql.entity.BaseCodeEntity;
import com.jgw.delingha.sql.entity.ConfigEntity;

import java.util.List;

import io.objectbox.relation.RelationInfo;

/**
 * @author : J-T
 * @date : 2022/1/24 11:08
 * description :待上传码基础操作类
 */
public abstract class BaseWaitUploadCodeOperator<T extends BaseCodeEntity> extends BaseConfigCodeOperator<T> {
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?,? extends ConfigEntity> relationInfo) {
        ConfigurationOperator configurationOperator = new ConfigurationOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }

    public abstract List<Long> queryAllConfigIdList();
}
