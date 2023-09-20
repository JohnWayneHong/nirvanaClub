package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.StockInEntity;
import com.jgw.delingha.sql.entity.StockInEntity_;

import java.util.List;

public class StockInOperator extends BaseWaitUploadCodeOperator<StockInEntity> {

    @Override
    public StockInEntity queryEntityByCode(String code) {
        return queryEntityByString(StockInEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(StockInEntity_.code, code);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(StockInEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(StockInEntity_.configEntityId, configId);
    }

    @Nullable
    public List<StockInEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(StockInEntity_.configEntityId, configId
                , StockInEntity_.id, currentId, limit);
    }

    @Override
    public List<StockInEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(StockInEntity_.configEntityId, configId
                , StockInEntity_.id, page, pageSize);
    }

    public long queryVerifyingDataByConfigId(long configId) {
        return queryCountByStatus(StockInEntity_.configEntityId, configId
                , StockInEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(StockInEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(StockInEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
