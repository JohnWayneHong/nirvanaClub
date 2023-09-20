package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.StockInPackagedEntity;
import com.jgw.delingha.sql.entity.StockInPackagedEntity_;

import java.util.List;

public class StockInPackagedOperator extends BaseWaitUploadCodeOperator<StockInPackagedEntity> {

    @Override
    public StockInPackagedEntity queryEntityByCode(String code) {
        return queryEntityByString(StockInPackagedEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(StockInPackagedEntity_.code, code);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(StockInPackagedEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(StockInPackagedEntity_.configEntityId, configId);
    }

    @Nullable
    public List<StockInPackagedEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(StockInPackagedEntity_.configEntityId, configId
                , StockInPackagedEntity_.id, currentId, limit);
    }

    @Override
    public List<StockInPackagedEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(StockInPackagedEntity_.configEntityId, configId
                , StockInPackagedEntity_.id, page, pageSize);
    }

    public long queryVerifyingDataByConfigId(long configId) {
        return queryCountByStatus(StockInPackagedEntity_.configEntityId, configId
                , StockInPackagedEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(StockInPackagedEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(StockInPackagedEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
