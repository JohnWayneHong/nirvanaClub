package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.ExchangeWarehouseConfigurationEntity_;
import com.jgw.delingha.sql.entity.ExchangeWarehouseEntity;
import com.jgw.delingha.sql.entity.ExchangeWarehouseEntity_;

import java.util.List;

import io.objectbox.relation.RelationInfo;

public class ExchangeWarehouseOperator extends BaseWaitUploadCodeOperator<ExchangeWarehouseEntity> {


    @Nullable
    public List<ExchangeWarehouseEntity> queryDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(ExchangeWarehouseEntity_.configEntityId, configId, ExchangeWarehouseEntity_.id, currentId, limit);
    }

    @Override
    public ExchangeWarehouseEntity queryEntityByCode(String code) {
        return queryEntityByString(ExchangeWarehouseEntity_.outerCode, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(ExchangeWarehouseEntity_.outerCode, code);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(ExchangeWarehouseEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(ExchangeWarehouseEntity_.configEntityId, configId);
    }

    @Nullable
    @Override
    public List<ExchangeWarehouseEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(ExchangeWarehouseEntity_.configEntityId, configId,
                ExchangeWarehouseEntity_.id, page, pageSize);
    }

    public long querySingleNumberByConfigId(long configId) {
        return querySingleNumberCountByConfigId(ExchangeWarehouseEntity_.configEntityId, configId, ExchangeWarehouseEntity_.singleNumber);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(ExchangeWarehouseEntity_.configEntity);
    }

    @Override
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        ExchangeWarehouseConfigurationOperator configurationOperator = new ExchangeWarehouseConfigurationOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }
    @Override
    public long queryCount() {
        return queryCountByCurrentUser(ExchangeWarehouseEntity_.configEntity
                , ExchangeWarehouseConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
