package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.StockOutFastEntity;
import com.jgw.delingha.sql.entity.StockOutFastEntity_;

import java.util.List;

/**
 * author : xsw
 * data : 2020/2/12
 * description : 出库数据库操作类
 */
public class StockOutFastOperator extends BaseWaitUploadCodeOperator<StockOutFastEntity> {

    @Override
    public StockOutFastEntity queryEntityByCode(String code) {
        return queryEntityByString(StockOutFastEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(StockOutFastEntity_.code, code);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(StockOutFastEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(StockOutFastEntity_.configEntityId, configId);
    }

    @Nullable
    public List<StockOutFastEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(StockOutFastEntity_.configEntityId, configId
                , StockOutFastEntity_.id, currentId, limit);
    }

    @Override
    public List<StockOutFastEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(StockOutFastEntity_.configEntityId, configId
                , StockOutFastEntity_.id, page, pageSize);
    }

    public long queryVerifyingDataByConfigId(long configId) {
        return queryCountByStatus(StockOutFastEntity_.configEntityId, configId
                , StockOutFastEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    public long querySingleNumberByConfigId(long configId) {
        return querySingleNumberCountByConfigId(StockOutFastEntity_.configEntityId
                , configId
                , StockOutFastEntity_.singleNumber);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(StockOutFastEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(StockOutFastEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
