package com.jgw.delingha.sql.operator;

import androidx.annotation.Nullable;

import com.jgw.delingha.bean.CodeBean;
import com.jgw.delingha.sql.LocalUserUtils;
import com.jgw.delingha.sql.entity.ConfigurationEntity_;
import com.jgw.delingha.sql.entity.StockReturnNoVerificationEntity;
import com.jgw.delingha.sql.entity.StockReturnNoVerificationEntity_;

import java.util.List;

/**
 * @author : J-T
 * @date : 2022/4/12 14:40
 * description :退货 operator 不校验库存 顶层码和下级码都能退货 （目前 冈本用）
 */
public class StockReturnNoVerificationOperator extends BaseWaitUploadCodeOperator<StockReturnNoVerificationEntity> {

    @Override
    public StockReturnNoVerificationEntity queryEntityByCode(String code) {
        return queryEntityByString(StockReturnNoVerificationEntity_.code, code);
    }

    @Override
    public long removeEntityByCode(String code) {
        return removeEntityByString(StockReturnNoVerificationEntity_.code, code);
    }
    
    public long queryVerifyingDataByConfigId(long configId) {
        return queryCountByStatus(StockReturnNoVerificationEntity_.configEntityId, configId
                , StockReturnNoVerificationEntity_.codeStatus, CodeBean.STATUS_CODE_VERIFYING);
    }

    @Override
    public void deleteAllByConfigId(long configId) {
        deleteAllByConfigId(StockReturnNoVerificationEntity_.configEntityId, configId);
    }

    @Override
    public long queryCountByConfigId(long configId) {
        return queryCountByConfigId(StockReturnNoVerificationEntity_.configEntityId, configId);
    }

    @Nullable
    public List<StockReturnNoVerificationEntity> queryGroupDataByConfigId(long configId, long currentId, int limit) {
        return queryGroupDataByConfigId(StockReturnNoVerificationEntity_.configEntityId, configId
                , StockReturnNoVerificationEntity_.id, currentId, limit);
    }

    @Override
    public List<StockReturnNoVerificationEntity> queryPageDataByConfigId(long configId, int page, int pageSize) {
        return queryPageDataByConfigId(StockReturnNoVerificationEntity_.configEntityId, configId
                , StockReturnNoVerificationEntity_.id, page, pageSize);
    }

    @Override
    public List<Long> queryAllConfigIdList() {
        return queryAllDistinctConfigIdList(StockReturnNoVerificationEntity_.configEntity);
    }

    @Override
    public long queryCount() {
        return queryCountByCurrentUser(StockReturnNoVerificationEntity_.configEntity
                , ConfigurationEntity_.userEntityId.equal(LocalUserUtils.getCurrentUserId()));
    }
}
