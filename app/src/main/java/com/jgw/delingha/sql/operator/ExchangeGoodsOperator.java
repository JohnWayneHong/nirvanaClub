package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsConfigurationEntity_;
import com.jgw.delingha.sql.entity.ExchangeGoodsEntity;
import com.jgw.delingha.sql.entity.ExchangeGoodsEntity_;

import java.util.List;

import io.objectbox.relation.RelationInfo;

// TODO: 2022/6/29 数据库使用方法参考此类
public class ExchangeGoodsOperator extends BaseWaitUploadCodeOperator<ExchangeGoodsEntity> {

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(ExchangeGoodsEntity_.outerCode, code);
    }

    @Override
    public ExchangeGoodsEntity queryEntityByCode(String code) {
        return queryEntityByString(ExchangeGoodsEntity_.outerCode, code);
    }

    @Nullable
    public List<ExchangeGoodsEntity> queryDataByConfigIdV2(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(ExchangeGoodsEntity_.configEntityId, configId, ExchangeGoodsEntity_.id, currentId, limit);
    }

    // TODO: 2022/6/30 根据ConfigId删除

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(ExchangeGoodsEntity_.configEntityId, configId);
    }

    // TODO: 2022/6/30 根据ConfigId获取数量

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(ExchangeGoodsEntity_.configEntityId, configId);
    }

    // TODO: 2022/6/30 根据ConfigId获取分页数据
    @Nullable
    @Override
    public List<ExchangeGoodsEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(ExchangeGoodsEntity_.configEntityId, configId, ExchangeGoodsEntity_.id, page, pageSize);
    }

    // TODO: 2022/6/30 获取不同ConfigId
    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(ExchangeGoodsEntity_.configEntity);
    }

    @Override
    public List<Long> queryAllDistinctConfigIdList(RelationInfo<?, ? extends ConfigEntity> relationInfo) {
        ExchangeGoodsConfigurationOperator configurationOperator = new ExchangeGoodsConfigurationOperator();
        return configurationOperator.queryAllDistinctConfigIdList(relationInfo);
    }

    // TODO: 2022/6/30 获取当前用户数量
    @Override
    public long queryCount() {
        return queryCountByCurrentUser(ExchangeGoodsEntity_.configEntity
                , ExchangeGoodsConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
