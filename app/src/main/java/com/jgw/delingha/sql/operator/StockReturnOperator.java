package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.StockReturnEntity;
import com.jgw.delingha.sql.entity.StockReturnEntity_;

import java.util.List;

/**
 * author : Cxz
 * data : 2020/1/19
 * description :
 */
public class StockReturnOperator extends BaseWaitUploadCodeOperator<StockReturnEntity> {

    @Override
    public StockReturnEntity queryEntityByCode(String code) {
        return queryEntityByString(StockReturnEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(StockReturnEntity_.code, code);
    }

    public long queryVerifyingDataByConfigId(long configId) {
        return queryCountByStatus(StockReturnEntity_.configEntityId, configId
                , StockReturnEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(StockReturnEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(StockReturnEntity_.configEntityId, configId);
    }

    @Nullable
    public List<StockReturnEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(StockReturnEntity_.configEntityId, configId
                , StockReturnEntity_.id, currentId, limit);
    }

    @Override
    public List<StockReturnEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(StockReturnEntity_.configEntityId, configId
                , StockReturnEntity_.id, page, pageSize);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(StockReturnEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(StockReturnEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
